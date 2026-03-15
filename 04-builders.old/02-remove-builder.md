# Remove Builder

`RemoveBuilder<T, C>` construeix operacions d’eliminació.

S’obté des de:

```java
MenuEditor.remove(menu)
```

Aquest builder **modifica el menú real** quan s’executa una operació terminal.

---

# 1. Selecció

## `where(selector)`

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().startsWith("[TEMP]"));
```

Sense selector, `execute()` llançarà una excepció.

---

## `whereLabel(predicate)`

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("[TEMP]"))
    .execute();
```

---

## `whereAny()`

```java
MenuEditor.remove(menu)
    .whereAny()
    .execute();
```

---

# 2. Rang

## `range(range)`

```java
MenuEditor.remove(menu)
    .whereAny()
    .range(Range.of(2, 6))
    .execute();
```

---

## `range(fromInclusive, toExclusive)`

```java
MenuEditor.remove(menu)
    .whereAny()
    .range(2, 6)
    .execute();
```

---

# 3. Configuració d’edició

Aquest builder comparteix configuració amb `ReplaceBuilder`.

## `limit(limit)`

```java
MenuEditor.remove(menu)
    .whereAny()
    .limit(2)
    .execute();
```

---

## `reverse()`

```java
MenuEditor.remove(menu)
    .whereAny()
    .reverse()
    .execute();
```

---

## `first()`

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.equals("Duplicada"))
    .first()
    .execute();
```

---

## `last()`

```java
MenuEditor.remove(menu)
    .whereLabel(label -> label.equals("Duplicada"))
    .last()
    .execute();
```

---

## `all()`

```java
MenuEditor.remove(menu)
    .whereAny()
    .all()
    .execute();
```

---

## `config(config)`

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

---

## `buildConfig()`

```java
EditConfig config = MenuEditor.remove(menu)
    .whereAny()
    .range(0, 10)
    .limit(2)
    .reverse(true)
    .buildConfig();
```

---

# 4. Operacions terminals

## `execute()`

Executa la cadena d’operacions pendents i retorna el nombre d’elements eliminats.

```java
int removed = MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("[TEMP]"))
    .execute();
```

---

## `executeAny()`

```java
boolean changed = MenuEditor.remove(menu)
    .whereLabel(label -> label.startsWith("[TEMP]"))
    .executeAny();
```