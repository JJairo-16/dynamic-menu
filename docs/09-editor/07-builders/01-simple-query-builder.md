# Simple Query Builder

`QueryBuilder<T, C>` construeix consultes sobre un `DynamicMenu` sense modificar-lo.

S’obté des de:

```java
MenuEditor.query(menu)
```

## Propòsit

A diferència de `RemoveBuilder`, `ReplaceBuilder` i `SortBuilder`, aquest builder **no aplica canvis** sobre el menú.

Serveix per:

- comprovar si existeixen coincidències
- comptar opcions coincidents
- obtenir índexs
- obtenir opcions

Internament, la consulta es resol sobre **una còpia temporal del menú**, de manera que cap operació modifica el menú real.

---

# 1. Definir la selecció

## `where(selector)`

Defineix la condició de selecció.

```java
MenuEditor.query(menu)
    .where((index, option) -> option.label().equals("Exit"))
    .exists();
```

La condició s’expressa amb un `OptionSelector`.

---

## `whereLabel(predicate)`

Defineix una condició basada únicament en el `label` de l’opció.

```java
MenuEditor.query(menu)
    .whereLabel(label -> label.equals("Exit"))
    .exists();
```

Internament el predicat es transforma en un `OptionSelector`.

---

## `whereAny()`

Selecciona totes les opcions.

```java
MenuEditor.query(menu)
    .whereAny()
    .count();
```

És equivalent a un selector que sempre retorna `true`.

---

# 2. Definir el rang

## `range(range)`

Defineix el rang de consulta amb un objecte `Range`.

```java
MenuEditor.query(menu)
    .whereAny()
    .range(Range.of(2, 6))
    .count();
```

---

## `range(fromInclusive, toExclusive)`

Drecera per crear el rang.

```java
MenuEditor.query(menu)
    .whereAny()
    .range(2, 6)
    .count();
```

Aquest rang limita les opcions que es tenen en compte durant la consulta.

---

# 3. Operacions terminals

## `exists()`

Indica si hi ha almenys una coincidència.

```java
boolean exists = MenuEditor.query(menu)
    .where((index, option) -> option.label().contains("Play"))
    .exists();
```

---

## `count()`

Retorna el nombre de coincidències.

```java
int count = MenuEditor.query(menu)
    .where((index, option) -> option.label().startsWith("Play"))
    .count();
```

---

## `firstIndex()`

Retorna el primer índex coincident.

```java
int first = MenuEditor.query(menu)
    .where((index, option) -> option.label().contains("Play"))
    .firstIndex();
```

---

## `lastIndex()`

Retorna l’últim índex coincident.

```java
int last = MenuEditor.query(menu)
    .where((index, option) -> option.label().contains("Play"))
    .lastIndex();
```

---

## `indexes()`

Retorna tots els índexs coincidents.

```java
List<Integer> indexes = MenuEditor.query(menu)
    .where((index, option) -> option.label().startsWith("Settings"))
    .indexes();
```

---

## `options()`

Retorna totes les opcions coincidents.

```java
List<MenuOption<T, C>> options = MenuEditor.query(menu)
    .where((index, option) -> option.label().startsWith("Settings"))
    .options();
```

---

## `first()`

Retorna la primera opció coincident.

```java
MenuOption<T, C> option = MenuEditor.query(menu)
    .whereLabel(label -> label.equals("Exit"))
    .first();
```

Si no hi ha coincidències retorna `null`.

---

## `last()`

Retorna l’última opció coincident.

```java
MenuOption<T, C> option = MenuEditor.query(menu)
    .whereLabel(label -> label.startsWith("Settings"))
    .last();
```

Si no hi ha coincidències retorna `null`.

---

## `collect(collector)`

Permet transformar el resultat amb un col·lector.

```java
Set<String> labels = MenuEditor.query(menu)
    .whereAny()
    .collect(list ->
        list.stream()
            .map(MenuOption::label)
            .collect(Collectors.toSet()));
```

Aquest mètode és útil quan es vol construir un resultat personalitzat.