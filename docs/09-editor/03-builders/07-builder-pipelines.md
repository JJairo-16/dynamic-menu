# Builder Pipelines

Els builders es poden encadenar per formar **pipelines d'operacions**.

Això permet expressar diverses transformacions consecutives
amb una sintaxi fluida i coherent.

Les crides `thenX()` no executen operacions immediatament.
Només afegeixen passos al pipeline.

## Idea general

Un pipeline permet:

- començar amb una selecció o configuració inicial
- continuar amb altres tipus d’operació
- executar només al final

Això fa que la intenció del codi sigui més clara.

## Exemple

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenReplace()
    .whereAny()
    .label("Temporal")
    .thenSort()
    .byLabel()
    .apply();
```

Pipeline conceptual:

```
remove → replace → sort → apply
```

## Execució terminal

L'execució real només passa quan es crida una operació terminal com:

- `execute()`
- `apply()`
- `count()`
- `exists()`

Fins a aquest moment, el pipeline només descriu què s’ha de fer.

## Herència d'estat entre passos

A més d'afegir una nova operació al pipeline, `thenX()` també pot decidir si el builder següent hereta part de l'estat fluent actual.

La regla general és:

- si el pipeline surt de `QueryBuilder`, es conserva l'estat compatible per defecte
- si surt de `RemoveBuilder`, `ReplaceBuilder`, `SortBuilder` o `ShuffleBuilder`, per defecte no s'hereta res
- en `thenShuffle()` des de builders no ranged-editables, el comportament habitual és transferir només el rang

Quan es necessita un comportament diferent, es pot usar `thenX(InheritanceMode)`.

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .range(0, 10)
    .thenQuery(InheritanceMode.SELECTION)
    .count();
```

Això permet controlar de manera explícita si el pas següent ha de rebre:

- res
- només el rang
- selector i rang
- tot l'estat compatible

## Compatibilitat de la herència

No tots els builders poden heretar exactament el mateix tipus d’estat.

- `QueryBuilder`, `RemoveBuilder` i `ReplaceBuilder` poden treballar amb selector i rang
- `SortBuilder` i `ShuffleBuilder` treballen amb rang, però no hereten selector com a part del seu encadenament
- per això `InheritanceMode.SELECTION` és vàlid entre builders seleccionables, però no quan el destí o l’origen efectiu és només ranged

Quan el destí és `SortBuilder` o `ShuffleBuilder`, la forma habitual d’herència és `RANGE` o `ALL`.

## Exemples de transferència

### Query → Replace

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.equals("Old"))
    .thenReplace()
    .label("New")
    .execute();
```

Aquí es reutilitzen selector i rang.

### Query → Sort

```java
MenuEditor.query(menu)
    .whereAny()
    .range(0, 10)
    .thenSort()
    .byLabel()
    .apply();
```

Aquí només es transfereix el rang.

### Replace → Query

```java
MenuEditor.replace(menu)
    .whereLabel(label -> label.equals("Old"))
    .label("New")
    .thenQuery(InheritanceMode.SELECTION)
    .count();
```

Aquí la transferència s’ha de demanar explícitament.

### Sort → Shuffle

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenShuffle(InheritanceMode.RANGE)
    .apply();
```

Aquí la transferència compatible és el rang.

## Benefici principal

Aquest model és útil quan vols construir operacions més expressives, reutilitzant el context acumulat entre builders.

També ajuda a separar clarament:

- la descripció del pipeline
- la política d’herència
- l’operació terminal que realment executa els canvis

## Obtenció

S'importa utilitzant:

```java
import menu.editor.builders.base.InheritanceMode;
```