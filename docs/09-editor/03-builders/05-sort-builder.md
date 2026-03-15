# Sort Builder

`SortBuilder<T, C>` ordena opcions d'un menú.

És el builder adequat quan vols reorganitzar l’ordre d’un conjunt d’opcions,
ja sigui sobre tot el menú o només sobre un rang concret.

S'obté des de:

```java
MenuEditor.sort(menu)
```

## Ordenació bàsica

La forma més simple d’ordenar és fer-ho pel label.

### `byLabel()`

Ordena les opcions segons el text del label.

```java
MenuEditor.sort(menu)
    .byLabel()
    .apply();
```

## Direcció de l’ordenació

Per defecte, l’ordenació és ascendent,
però es pot invertir.

### `descending()`

Aplica l’ordenació en sentit descendent.

```java
MenuEditor.sort(menu)
    .byLabel()
    .descending()
    .apply();
```

## Rang

L’ordenació també es pot limitar a una part del menú.

### `range(...)`

Restringeix l’ordenació a un tram concret.

```java
MenuEditor.sort(menu)
    .range(0, 5)
    .byLabel()
    .apply();
```

Això permet reordenar una zona sense afectar la resta del menú.

## Aplicació de l’ordenació

A diferència de `QueryBuilder`, l’ordenació no es resol com a consulta,
sinó que s’aplica sobre el menú.

### `apply()`

Executa l’ordenació configurada.

```java
MenuEditor.sort(menu)
    .byLabel()
    .apply();
```

Habitualment retorna el mateix menú, ja modificat.

## Possibles criteris addicionals

Segons la implementació concreta, `SortBuilder` pot oferir
altres criteris o comparadors més específics.

Quan només necessites una ordenació textual estàndard,
`byLabel()` és la variant habitual.

## Encadenament

`SortBuilder` també pot formar part d’un pipeline.

### `thenSort()`

Continua amb un nou `SortBuilder`.

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenSort()
    .byLabel()
    .apply();
```

### `thenQuery()`

Continua amb `QueryBuilder`.

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenQuery()
    .count();
```

### `thenRemove()`

Continua amb `RemoveBuilder`.

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenRemove()
    .whereAny()
    .execute();
```

### `thenReplace()`

Continua amb `ReplaceBuilder`.

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenReplace()
    .whereAny()
    .label("Nou")
    .execute();
```