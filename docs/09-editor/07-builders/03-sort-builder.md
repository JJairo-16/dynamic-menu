# Sort Builder

`SortBuilder<T, C>` construeix operacions d’ordenació sobre un `DynamicMenu`.

S’obté des de:

```java
MenuEditor.sort(menu)
```

L’ordenació reorganitza les opcions existents sense eliminar-les ni substituir-les.

---

# 1. Mètodes públics de configuració

## `range(range)`

Defineix el rang d’ordenació amb un objecte `Range`.

```java
MenuEditor.sort(menu)
    .range(Range.of(2, 6))
    .apply();
```

## `range(fromInclusive, toExclusive)`

Drecera per crear i aplicar el rang.

```java
MenuEditor.sort(menu)
    .range(2, 6)
    .apply();
```

Només s’ordenen les opcions dins del rang indicat.

## `byLabel()`

Configura l’ordenació per label amb el comparador per defecte.

```java
MenuEditor.sort(menu)
    .byLabel()
    .apply();
```

És la forma més habitual d’ordenar el menú.

## `comparator(comparator)`

Permet definir un `Comparator<MenuOption<T, C>>` personalitzat.

```java
MenuEditor.sort(menu)
    .comparator((a, b) -> a.label().length() - b.label().length())
    .apply();
```

Això permet ordenar segons qualsevol criteri derivat de l’opció.

## `descending()`

Inverteix l’ordre del comparador configurat.

```java
MenuEditor.sort(menu)
    .byLabel()
    .descending()
    .apply();
```

## `pinFirst(selector)`

Manté certes opcions fixades al principi del bloc ordenat.

```java
MenuEditor.sort(menu)
    .byLabel()
    .pinFirst((index, option) -> option.label().equals("Play"))
    .apply();
```

## `pinLast(selector)`

Manté certes opcions fixades al final del bloc ordenat.

```java
MenuEditor.sort(menu)
    .byLabel()
    .pinLast((index, option) -> option.label().equals("Exit"))
    .apply();
```

Aquestes opcions es preserven en les vores mentre la resta es reordena.

---

# 2. Combinacions habituals

## Ordenació bàsica

```java
MenuEditor.sort(menu)
    .byLabel()
    .apply();
```

## Ordenació descendent

```java
MenuEditor.sort(menu)
    .byLabel()
    .descending()
    .apply();
```

## Ordenació parcial amb opcions fixades

```java
MenuEditor.sort(menu)
    .range(2, 8)
    .byLabel()
    .pinFirst((index, option) -> option.label().equals("Play"))
    .pinLast((index, option) -> option.label().equals("Exit"))
    .apply();
```

---

# 3. Encadenament amb altres builders

Com tots els builders del sistema, `SortBuilder` pot formar part d’una cadena d’operacions.

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .thenSort()
    .descending()
    .apply();
```

Les crides `thenX()` **no executen cap operació immediatament**.

Cada builder afegeix una operació pendent que només s’aplica quan es crida una operació terminal.

---

# 4. Operació terminal

## `apply()`

Aplica l’ordenació al menú després d’executar qualsevol operació pendent anterior a la cadena.

```java
MenuEditor.sort(menu)
    .byLabel()
    .apply();
```

## Quan convé usar-lo

És recomanable quan vols:

- ordenar opcions sense recrear el menú
- restringir l’ordenació a una part concreta
- conservar opcions importants al principi o al final
- reutilitzar l’ordenació dins d’una cadena declarativa de builders
