# Remove Builder

`RemoveBuilder<T, C>` construeix operacions d'eliminació.

És el builder adequat quan vols suprimir opcions del menú
de manera declarativa i configurable.

S'obté des de:

```java
MenuEditor.remove(menu)
```

## Selecció

Com en altres builders, el primer pas és indicar quines opcions s’han d’eliminar.

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .execute();
```

També es poden usar altres variants de selecció, com ara:

- `where(selector)`
- `whereIndex(predicate)`
- `whereLabel(predicate)`
- `whereLabelEquals(String text)`
- `whereLabelEqualsIgnoreCase(String text)`
- `whereLabelStartsWith(String prefix)`
- `whereLabelEndsWith(String suffix)`
- `whereAny()`

## Configuració bàsica

Després de seleccionar coincidències, es pot ajustar com s’aplica l’eliminació.

### `range(...)`

Restringeix l’operació a una part concreta del menú.

```java
MenuEditor.remove(menu)
    .whereAny()
    .range(0, 10)
    .execute();
```

### `limit(...)`

Limita el nombre màxim d’opcions eliminades.

```java
MenuEditor.remove(menu)
    .whereAny()
    .limit(2)
    .execute();
```

### `reverse()`

Recorre les coincidències en sentit invers abans d’eliminar.

```java
MenuEditor.remove(menu)
    .whereAny()
    .reverse()
    .execute();
```

Això és especialment útil quan l’ordre d’eliminació és important.

### `first()`

Restringeix l’operació a la primera coincidència.

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.equals("Duplicada"))
    .first()
    .execute();
```

### `last()`

Restringeix l’operació a l’última coincidència.

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.equals("Duplicada"))
    .last()
    .execute();
```

### `all()`

Aplica l’operació a totes les coincidències seleccionades.

```java
MenuEditor.remove(menu)
    .whereAny()
    .all()
    .execute();
```

És la forma natural quan no vols restringir l’edició a una sola coincidència.

## Execució

L’operació principal d’aquest builder és l’eliminació efectiva de coincidències.

### `execute()`

Executa l’eliminació i retorna quantes opcions s’han eliminat.

```java
int removed = MenuEditor.remove(menu)
    .whereAny()
    .execute();
```

### `executeAny()`

Indica si s’ha eliminat almenys una opció.

```java
boolean changed = MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .executeAny();
```

És útil quan només interessa saber si el menú ha canviat.

## Configuració avançada

`RemoveBuilder` utilitza internament un `EditConfig`.

Això permet construir una configuració reusable o aplicar-la de cop.

### `config(config)`

Permet aplicar una configuració completa d’edició.

```java
MenuEditor.remove(menu)
    .whereAny()
    .config(
        EditConfig.builder()
            .range(Range.of(0, 10))
            .limit(2)
            .reverse(true)
            .build()
    )
    .execute();
```

### `buildConfig()`

Construeix la configuració actual sense executar l’operació.

```java
EditConfig config = MenuEditor.remove(menu)
    .whereAny()
    .range(0, 5)
    .limit(1)
    .buildConfig();
```

Això és útil si vols inspeccionar o reutilitzar la configuració fluent actual.

## Encadenament

`RemoveBuilder` també pot formar part d’un pipeline.

Per defecte, quan l'encadenament surt de `RemoveBuilder`, el builder següent no hereta cap estat fluent.

### `thenRemove()`

Continua amb un nou `RemoveBuilder` sense herència per defecte.

```java
MenuEditor.remove(menu)
    .whereAny()
    .thenRemove()
    .whereAny()
    .execute();
```

### `thenReplace()`

Continua amb `ReplaceBuilder` sense herència per defecte.

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenReplace()
    .whereAny()
    .label("Temporal")
    .execute();
```

### `thenSort()`

Continua amb `SortBuilder` sense herència per defecte.

```java
MenuEditor.remove(menu)
    .whereAny()
    .thenSort()
    .byLabel()
    .apply();
```

### `thenQuery()`

Continua amb `QueryBuilder` sense herència per defecte.

```java
MenuEditor.remove(menu)
    .whereAny()
    .thenQuery()
    .whereAny()
    .count();
```

### `thenShuffle()`

Continua amb `ShuffleBuilder`.

```java
MenuEditor.remove(menu)
    .whereAny()
    .range(0, 10)
    .thenShuffle()
    .apply();
```

En aquest cas, el mode per defecte és `InheritanceMode.RANGE`, de manera que el rang actual es transfereix al pas següent.

## Herència explícita amb `InheritanceMode`

Quan vols controlar explícitament la transferència d’estat, pots usar la variant `thenX(InheritanceMode)`.

### `thenRemove(InheritanceMode)`

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .range(0, 10)
    .thenRemove(InheritanceMode.ALL)
    .execute();
```

### `thenReplace(InheritanceMode)`

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenReplace(InheritanceMode.SELECTION)
    .label("Temporal")
    .execute();
```

### `thenSort(InheritanceMode)`

```java
MenuEditor.remove(menu)
    .whereAny()
    .range(0, 10)
    .thenSort(InheritanceMode.RANGE)
    .byLabel()
    .apply();
```

### `thenQuery(InheritanceMode)`

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .range(0, 10)
    .thenQuery(InheritanceMode.SELECTION)
    .count();
```

### `thenShuffle(InheritanceMode)`

```java
MenuEditor.remove(menu)
    .whereAny()
    .range(0, 10)
    .thenShuffle(InheritanceMode.RANGE)
    .apply();
```

## Modes disponibles

- `InheritanceMode.NONE`: no hereta res
- `InheritanceMode.RANGE`: hereta només el rang
- `InheritanceMode.SELECTION`: hereta selector i rang
- `InheritanceMode.ALL`: hereta tot l'estat compatible amb el builder destí

Quan el destí és `SortBuilder` o `ShuffleBuilder`, l’herència efectiva només afecta el rang.

### Obtenció

S'importa utilitzant:

```java
import menu.editor.builders.base.InheritanceMode;
```