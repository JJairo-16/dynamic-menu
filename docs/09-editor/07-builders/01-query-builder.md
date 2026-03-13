# Query Builder

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
- preparar una cadena que després continuï amb altres builders

Internament, actua com a façana sobre la família de consulta del paquet.

---

# 1. Mètodes públics de configuració

## `where(selector)`

Defineix la condició de selecció.

```java
MenuEditor.query(menu)
    .where((index, option) -> option.label().equals("Exit"))
    .exists();
```

La condició s’expressa amb un `OptionSelector`.

## `whereAny()`

Selecciona totes les opcions.

```java
MenuEditor.query(menu)
    .whereAny()
    .count();
```

És l’equivalent conceptual a fer servir un selector que sempre retorna `true`.

## `range(range)`

Defineix el rang de consulta amb un objecte `Range`.

```java
MenuEditor.query(menu)
    .whereAny()
    .range(Range.of(2, 6))
    .count();
```

## `range(fromInclusive, toExclusive)`

Drecera per crear i aplicar el rang.

```java
MenuEditor.query(menu)
    .whereAny()
    .range(2, 6)
    .count();
```

Aquest rang limita les opcions que es tenen en compte durant la consulta.

---

# 2. Operacions terminals de consulta

## `exists()`

Indica si hi ha almenys una coincidència.

```java
boolean exists = MenuEditor.query(menu)
    .where((index, option) -> option.label().contains("Play"))
    .exists();
```

## `count()`

Retorna el nombre de coincidències.

```java
int count = MenuEditor.query(menu)
    .where((index, option) -> option.label().startsWith("Play"))
    .count();
```

## `firstIndex()`

Retorna el primer índex coincident.

```java
int first = MenuEditor.query(menu)
    .where((index, option) -> option.label().contains("Play"))
    .firstIndex();
```

## `lastIndex()`

Retorna l’últim índex coincident.

```java
int last = MenuEditor.query(menu)
    .where((index, option) -> option.label().contains("Play"))
    .lastIndex();
```

## `indexes()`

Retorna tots els índexs coincidents.

```java
List<Integer> indexes = MenuEditor.query(menu)
    .where((index, option) -> option.label().startsWith("Settings"))
    .indexes();
```

## `options()`

Retorna totes les opcions coincidents.

```java
List<MenuOption<T, C>> options = MenuEditor.query(menu)
    .where((index, option) -> option.label().startsWith("Settings"))
    .options();
```

---

# 3. Encadenament amb altres builders

`QueryBuilder` també pot formar part d’una cadena d’operacions.

```java
MenuEditor.query(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .thenRemove()
    .whereAny()
    .execute();
```

Les crides `thenX()` **no executen cap operació immediatament**.

Cada builder afegeix una operació pendent que només s’aplica quan es crida una operació terminal com `execute()` o `apply()`.

## Quan convé usar-lo

És útil quan vols:

- inspeccionar el menú abans de modificar-lo
- reutilitzar la mateixa lògica de selecció que faries servir en un remove o replace
- iniciar una cadena declarativa que després continuï amb altres operacions
