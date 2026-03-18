# Sort Builder

`SortBuilder<T, C>` ordena opcions d'un menú.

És el builder adequat quan vols reorganitzar l’ordre d’un conjunt d’opcions, ja sigui sobre tot el menú o només sobre un rang concret.

S'obté des de:

```java
MenuEditor.sort(menu)
```

## Ordenació bàsica

La forma més simple d’ordenar és fer-ho pel label.

### `byLabel()`

Ordena les opcions segons el text del label amb el comparador per defecte.

```java
MenuEditor.sort(menu)
    .byLabel()
    .apply();
```

### `comparator(...)`

Permet definir el comparador base.

```java
MenuEditor.sort(menu)
    .comparator((a, b) -> Integer.compare(a.label().length(), b.label().length()))
    .apply();
```

## Direcció de l’ordenació

Per defecte, l’ordenació és ascendent, però es pot invertir.

### `ascending()`

```java
MenuEditor.sort(menu)
    .byLabel()
    .ascending()
    .apply();
```

### `descending()`

Aplica l’ordenació en sentit descendent.

```java
MenuEditor.sort(menu)
    .byLabel()
    .descending()
    .apply();
```

### `descending(boolean descending)`

Permet definir explícitament si la direcció és descendent.

```java
MenuEditor.sort(menu)
    .byLabel()
    .descending(true)
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

## Opcions fixades

`SortBuilder` també permet fixar opcions al principi o al final del segment ordenat.

### `pinFirst(selector)`

```java
MenuEditor.sort(menu)
    .pinFirst((index, option) -> option.label().equals("Home"))
    .byLabel()
    .apply();
```

### `pinLabelFirst(predicate)`

```java
MenuEditor.sort(menu)
    .pinLabelFirst(label -> label.startsWith("[CORE]"))
    .byLabel()
    .apply();
```

### `pinLast(selector)`

```java
MenuEditor.sort(menu)
    .pinLast((index, option) -> option.label().equals("Exit"))
    .byLabel()
    .apply();
```

### `pinLabelLast(predicate)`

```java
MenuEditor.sort(menu)
    .pinLabelLast(label -> label.equals("Exit"))
    .byLabel()
    .apply();
```

### `pinned(firstSelector, lastSelector)`

```java
MenuEditor.sort(menu)
    .pinned(
        (index, option) -> option.label().equals("Home"),
        (index, option) -> option.label().equals("Exit")
    )
    .byLabel()
    .apply();
```

### `pinnedIndexes(firstIndexes, lastIndexes)`

```java
MenuEditor.sort(menu)
    .pinnedIndexes(List.of(0), List.of(7))
    .byLabel()
    .apply();
```

## Aplicació de l’ordenació

A diferència de `QueryBuilder`, l’ordenació no es resol com a consulta, sinó que s’aplica sobre el menú.

### `execute()`

Executa l’ordenació configurada.

```java
MenuEditor.sort(menu)
    .byLabel()
    .execute();
```

### `apply()`

És un àlies semàntic d’`execute()`.

```java
MenuEditor.sort(menu)
    .byLabel()
    .apply();
```

Habitualment retorna el mateix menú, ja modificat.

## Encadenament

`SortBuilder` també pot formar part d’un pipeline.

Per defecte, quan l'encadenament surt de `SortBuilder`, el builder següent no hereta cap estat fluent.

Com que `SortBuilder` treballa principalment amb rang i criteris d'ordenació, la forma habitual d'herència explícita és transferir només el rang.

### `thenSort()`

Continua amb un nou `SortBuilder` sense herència per defecte.

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenSort()
    .byLabel()
    .apply();
```

### `thenRemove()`

Continua amb `RemoveBuilder` sense herència per defecte.

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenRemove()
    .whereAny()
    .execute();
```

### `thenReplace()`

Continua amb `ReplaceBuilder` sense herència per defecte.

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenReplace()
    .whereAny()
    .label("Nou")
    .execute();
```

### `thenQuery()`

Continua amb `QueryBuilder` sense herència per defecte.

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenQuery()
    .whereAny()
    .count();
```

### `thenShuffle()`

Continua amb `ShuffleBuilder`.

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenShuffle()
    .apply();
```

En aquest cas, el mode per defecte és `InheritanceMode.RANGE`.

## Herència explícita amb `InheritanceMode`

### `thenSort(InheritanceMode)`

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenSort(InheritanceMode.RANGE)
    .byLabel()
    .apply();
```

### `thenRemove(InheritanceMode)`

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenRemove(InheritanceMode.RANGE)
    .whereAny()
    .execute();
```

### `thenReplace(InheritanceMode)`

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenReplace(InheritanceMode.RANGE)
    .whereAny()
    .label("Nou")
    .execute();
```

### `thenQuery(InheritanceMode)`

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenQuery(InheritanceMode.RANGE)
    .whereAny()
    .count();
```

### `thenShuffle(InheritanceMode)`

```java
MenuEditor.sort(menu)
    .range(0, 10)
    .thenShuffle(InheritanceMode.RANGE)
    .apply();
```

## Modes disponibles

- `InheritanceMode.NONE`: no hereta res
- `InheritanceMode.RANGE`: hereta només el rang
- `InheritanceMode.ALL`: hereta tot l'estat compatible; en aquest cas, essencialment el rang
- `InheritanceMode.SELECTION`: no és vàlid en aquest context i produeix una excepció

Això passa perquè `SortBuilder` no hereta selector fluent de coincidència.

### Obtenció

S'importa utilitzant:

```java
import menu.editor.builders.base.InheritanceMode;
```