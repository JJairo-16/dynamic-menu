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

Fins a aquest moment, el pipeline només descriu què s’ha de fer.

## Herència d'estat entre passos

A més d'afegir una nova operació al pipeline, `thenX()` també pot decidir si el builder següent hereta part de l'estat fluent actual.

La regla general és:

- si el pipeline surt de `QueryBuilder`, es conserva l'estat compatible per defecte
- si surt de `RemoveBuilder`, `ReplaceBuilder` o `SortBuilder`, per defecte no s'hereta res

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

## Benefici principal

Aquest model és útil quan vols construir operacions més expressives,
reutilitzant el context acumulat entre builders.