# Sort Builder

`SortBuilder<T, C>` construeix operacions d’ordenació sobre un `DynamicMenu`.

S’obté des de:

```java
MenuEditor.sort(menu)
```

L’ordenació reorganitza les opcions existents sense eliminar-les ni substituir-les. :contentReference[oaicite:1]{index=1}

Aquest builder comparteix part de la seva infraestructura amb la resta de builders a través de `AbstractChainableMenuBuilder`.

---

# 1. Definir el rang

## `range(range)`

Defineix el rang d’ordenació amb un objecte `Range`.

```java
MenuEditor.sort(menu)
    .range(Range.of(2, 6))
    .apply();
```

---

## `range(fromInclusive, toExclusive)`

Drecera per crear i aplicar el rang.

```java
MenuEditor.sort(menu)
    .range(2, 6)
    .apply();
```

Només s’ordenen les opcions dins del rang indicat.

---

# 2. Definir el comparador

## `byLabel()`

Ordena les opcions segons el label amb el comparador per defecte.

```java
MenuEditor.sort(menu)
    .byLabel()
    .apply();
```

---

## `comparator(comparator)`

Permet definir un comparador personalitzat.

```java
MenuEditor.sort(menu)
    .comparator((a, b) -> a.label().length() - b.label().length())
    .apply();
```

---

# 3. Direcció d’ordenació

## `ascending()`

Força l’ordre ascendent.

```java
MenuEditor.sort(menu)
    .byLabel()
    .ascending()
    .apply();
```

---

## `descending()`

Força l’ordre descendent.

```java
MenuEditor.sort(menu)
    .byLabel()
    .descending()
    .apply();
```

---

# 4. Opcions fixades

Aquest builder permet fixar opcions al principi o al final del segment ordenat.

---

## `pinFirst(selector)`

```java
MenuEditor.sort(menu)
    .byLabel()
    .pinFirst((index, option) -> option.label().equals("Play"))
    .apply();
```

---

## `pinLabelFirst(predicate)`

```java
MenuEditor.sort(menu)
    .byLabel()
    .pinLabelFirst(label -> label.equals("Play"))
    .apply();
```

---

## `pinLast(selector)`

```java
MenuEditor.sort(menu)
    .byLabel()
    .pinLast((index, option) -> option.label().equals("Exit"))
    .apply();
```

---

## `pinLabelLast(predicate)`

```java
MenuEditor.sort(menu)
    .byLabel()
    .pinLabelLast(label -> label.equals("Exit"))
    .apply();
```

---

# 5. Operacions terminals

## `apply()`

Aplica l’ordenació al menú després d’executar qualsevol operació pendent anterior.

```java
MenuEditor.sort(menu)
    .byLabel()
    .apply();
```

---

## `execute()`

Alias funcional equivalent a `apply()`.

```java
MenuEditor.sort(menu)
    .byLabel()
    .execute();
```

---

# 6. Encadenament amb altres builders

Els builders poden formar part d’un pipeline fluent.

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("Temp"))
    .thenSort()
    .byLabel()
    .apply();
```

Les operacions **no s’executen immediatament**.

Només s’apliquen quan es crida una operació terminal (`execute` o `apply`).