# Shuffle Builder

`ShuffleBuilder<T, C>` barreja opcions d'un menú.

És el builder adequat quan vols reorganitzar l’ordre d’un conjunt d’opcions de manera aleatòria, ja sigui sobre tot el menú o només sobre un rang concret.

S'obté des de:

```java
MenuEditor.shuffle(menu)
```

## Barreja bàsica

La forma més simple de barrejar és aplicar-la sobre totes les opcions.

### `apply()`

```java
MenuEditor.shuffle(menu)
    .apply();
```

`apply()` és un àlies semàntic de `execute()`.

### `execute()`

```java
MenuEditor.shuffle(menu)
    .execute();
```

## Control del random

`ShuffleBuilder` permet controlar la font d’aleatorietat.

### `random(Random random)`

Permet usar una instància concreta de `Random`.

```java
MenuEditor.shuffle(menu)
    .random(new Random())
    .apply();
```

### `seed(long seed)`

Permet definir una llavor per obtenir una barreja reproduïble.

```java
MenuEditor.shuffle(menu)
    .seed(42L)
    .apply();
```

## Rang

La barreja també es pot limitar a una part del menú.

### `range(...)`

Restringeix la barreja a un tram concret.

```java
MenuEditor.shuffle(menu)
    .range(0, 5)
    .apply();
```

Això permet variar l’ordre només d’una zona sense afectar la resta del menú.

## Opcions fixades

`ShuffleBuilder` també permet fixar opcions al principi o al final del segment barrejat.

### `pinFirst(selector)`

Fixa opcions al principi segons un selector complet.

```java
MenuEditor.shuffle(menu)
    .pinFirst((index, option) -> option.label().equals("Home"))
    .apply();
```

### `pinLabelFirst(predicate)`

Fixa opcions al principi segons una condició sobre el label.

```java
MenuEditor.shuffle(menu)
    .pinLabelFirst(label -> label.startsWith("[CORE]"))
    .apply();
```

### `pinLast(selector)`

Fixa opcions al final segons un selector complet.

```java
MenuEditor.shuffle(menu)
    .pinLast((index, option) -> option.label().equals("Exit"))
    .apply();
```

### `pinLabelLast(predicate)`

Fixa opcions al final segons una condició sobre el label.

```java
MenuEditor.shuffle(menu)
    .pinLabelLast(label -> label.equals("Exit"))
    .apply();
```

### `pinned(firstSelector, lastSelector)`

Permet definir alhora el selector d’opcions inicials i finals.

```java
MenuEditor.shuffle(menu)
    .pinned(
        (index, option) -> option.label().equals("Home"),
        (index, option) -> option.label().equals("Exit")
    )
    .apply();
```

### `pinnedIndexes(firstIndexes, lastIndexes)`

Permet fixar opcions pels seus índexs.

```java
MenuEditor.shuffle(menu)
    .pinnedIndexes(List.of(0), List.of(7))
    .apply();
```

## Encadenament

`ShuffleBuilder` també pot formar part d’un pipeline.

Per defecte, quan l'encadenament surt de `ShuffleBuilder`, el builder següent no hereta cap estat fluent.

Com que `ShuffleBuilder` treballa principalment amb rang, la forma habitual d'herència explícita és transferir només el rang.

### `thenShuffle()`

Continua amb un nou `ShuffleBuilder` sense herència per defecte.

```java
MenuEditor.shuffle(menu)
    .range(0, 10)
    .thenShuffle()
    .seed(99L)
    .apply();
```

### `thenRemove()`

Continua amb `RemoveBuilder` sense herència per defecte.

```java
MenuEditor.shuffle(menu)
    .range(0, 10)
    .thenRemove()
    .whereAny()
    .execute();
```

### `thenReplace()`

Continua amb `ReplaceBuilder` sense herència per defecte.

```java
MenuEditor.shuffle(menu)
    .range(0, 10)
    .thenReplace()
    .whereAny()
    .label("Nou")
    .execute();
```

### `thenSort()`

Continua amb `SortBuilder` sense herència per defecte.

```java
MenuEditor.shuffle(menu)
    .range(0, 10)
    .thenSort()
    .byLabel()
    .apply();
```

### `thenQuery()`

Continua amb `QueryBuilder` sense herència per defecte.

```java
MenuEditor.shuffle(menu)
    .range(0, 10)
    .thenQuery()
    .whereAny()
    .count();
```

## Herència explícita amb `InheritanceMode`

### Heretar el rang

```java
MenuEditor.shuffle(menu)
    .range(0, 10)
    .thenQuery(InheritanceMode.RANGE)
    .whereAny()
    .count();
```

```java
MenuEditor.shuffle(menu)
    .range(0, 10)
    .thenSort(InheritanceMode.ALL)
    .byLabel()
    .apply();
```

### `thenShuffle(InheritanceMode)`

```java
MenuEditor.shuffle(menu)
    .range(0, 10)
    .thenShuffle(InheritanceMode.RANGE)
    .apply();
```

### `thenRemove(InheritanceMode)`

```java
MenuEditor.shuffle(menu)
    .range(0, 10)
    .thenRemove(InheritanceMode.RANGE)
    .whereAny()
    .execute();
```

### `thenReplace(InheritanceMode)`

```java
MenuEditor.shuffle(menu)
    .range(0, 10)
    .thenReplace(InheritanceMode.RANGE)
    .whereAny()
    .label("Nou")
    .execute();
```

### `thenSort(InheritanceMode)`

```java
MenuEditor.shuffle(menu)
    .range(0, 10)
    .thenSort(InheritanceMode.RANGE)
    .byLabel()
    .apply();
```

### Sobre `SELECTION`

`ShuffleBuilder` no defineix selector fluent propi per a l'encadenament.

Per això, `InheritanceMode.SELECTION` no és el mode adequat quan l'origen és `ShuffleBuilder`; la crida produeix una excepció i la forma habitual és usar `RANGE` o `ALL`.

## Resum de modes

- `InheritanceMode.NONE`: no hereta res
- `InheritanceMode.RANGE`: hereta el rang
- `InheritanceMode.ALL`: hereta tot l'estat compatible; en aquest cas, essencialment el rang
- `InheritanceMode.SELECTION`: no és vàlid en aquest context i produeix una excepció

### Obtenció

S'importa utilitzant:

```java
import menu.editor.builders.base.InheritanceMode;
```