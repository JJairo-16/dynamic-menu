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

## Configuració bàsica

Després de seleccionar coincidències, es pot ajustar com s’aplica l’eliminació.

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
    .limit(2)
    .buildConfig();
```

Això és útil quan vols inspeccionar o reutilitzar la configuració.

## Continuar amb altres builders

`RemoveBuilder` pot continuar en pipeline amb altres operacions.

### `thenRemove()`

Crea un nou `RemoveBuilder` conservant el context actual.

```java
MenuEditor.remove(menu)
    .whereAny()
    .thenRemove();
```

### `thenReplace()`

Continua amb `ReplaceBuilder`.

```java
MenuEditor.remove(menu)
    .whereAny()
    .thenReplace()
    .label("Nou");
```

### `thenSort()`

Continua amb `SortBuilder`.

```java
MenuEditor.remove(menu)
    .whereAny()
    .thenSort()
    .byLabel();
```

### `thenQuery()`

Continua amb `QueryBuilder`.

```java
MenuEditor.remove(menu)
    .whereAny()
    .thenQuery()
    .count();
```