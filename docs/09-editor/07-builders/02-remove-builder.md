# Remove Builder

`RemoveBuilder<T, C>` construeix operacions d’eliminació.

S’obté des de:

```java
MenuEditor.remove(menu)
```

o bé:

```java
MenuEditor.remove(menu, selector)
```

## Constructor públic

## `new RemoveBuilder<>(menu)`

Existeix com a constructor públic, però normalment es fa servir a través de `MenuEditor.remove(...)`.

```java
RemoveBuilder<String, Void> builder = new RemoveBuilder<>(menu);
```

En ús normal, és preferible:

```java
RemoveBuilder<String, Void> builder = MenuEditor.remove(menu);
```

---

# 1. Mètodes públics de `RemoveBuilder`

## `where(selector)`

Defineix la condició de selecció.

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().startsWith("[TEMP]"));
```

Sense `where(...)`, `execute()` i `executeAny()` fallaran perquè la condició és obligatòria.

## `whereAny()`

Selecciona totes les opcions.

```java
MenuEditor.remove(menu)
    .whereAny()
    .limit(1)
    .execute();
```

És l’equivalent conceptual a fer servir un selector que sempre retorna `true`.

## `range(range)`

Defineix el rang d’actuació amb un objecte `Range`.

```java
MenuEditor.remove(menu)
    .whereAny()
    .range(Range.of(2, 6))
    .execute();
```

## `range(fromInclusive, toExclusive)`

Drecera per crear i aplicar el rang.

```java
MenuEditor.remove(menu)
    .whereAny()
    .range(2, 6)
    .execute();
```

## `limit(limit)`

Defineix el nombre màxim de coincidències que es poden eliminar.

```java
MenuEditor.remove(menu)
    .whereAny()
    .limit(2)
    .execute();
```

## `reverse()`

Força el recorregut en sentit invers.

```java
MenuEditor.remove(menu)
    .whereAny()
    .reverse()
    .execute();
```

## `reverse(boolean reverse)`

Permet indicar explícitament si s’ha de recórrer en sentit invers.

```java
MenuEditor.remove(menu)
    .whereAny()
    .reverse(true)
    .execute();
```

## `first()`

Configura el builder perquè només afecti la primera coincidència.

Internament equival conceptualment a:

- `limit(1)`
- recorregut normal

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().equals("Duplicada"))
    .first()
    .execute();
```

## `last()`

Configura el builder perquè només afecti l’última coincidència.

Internament equival conceptualment a:

- `limit(1)`
- recorregut invers

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().equals("Duplicada"))
    .last()
    .execute();
```

## `all()`

Indica que s’han de considerar totes les coincidències.

```java
MenuEditor.remove(menu)
    .whereAny()
    .all()
    .execute();
```

Normalment és la configuració natural per defecte, però és útil per fer la intenció explícita.

## `config(config)`

Carrega una configuració base a partir d’un `EditConfig`.

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

## `buildConfig()`

Construeix un `EditConfig` equivalent a l’estat actual del builder.

```java
EditConfig config = MenuEditor.remove(menu)
    .whereAny()
    .range(0, 10)
    .limit(2)
    .reverse(true)
    .buildConfig();
```

Aquest mètode és útil quan vols:

- reutilitzar configuracions
- depurar una construcció
- passar la mateixa configuració a una altra operació

## `execute()`

Executa l’operació i retorna el nombre d’elements eliminats.

```java
int removed = MenuEditor.remove(menu)
    .where((index, option) -> option.label().startsWith("[TEMP]"))
    .execute();
```

## `executeAny()`

Executa l’operació i retorna `true` si s’ha eliminat almenys un element.

```java
boolean changed = MenuEditor.remove(menu)
    .where((index, option) -> option.label().startsWith("[TEMP]"))
    .executeAny();
```

---

# 2. Patrons habituals amb `RemoveBuilder`

## Eliminar totes les coincidències

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().startsWith("[TEMP]"))
    .execute();
```

## Eliminar només la primera

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().equals("Duplicada"))
    .first()
    .execute();
```

## Eliminar només l’última

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().equals("Duplicada"))
    .last()
    .execute();
```

## Eliminar dins d’un rang

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().startsWith("[TEMP]"))
    .range(0, 5)
    .execute();
```

## Eliminar amb límit i sentit invers

```java
MenuEditor.remove(menu)
    .where((index, option) -> option.label().startsWith("[TEMP]"))
    .range(0, 10)
    .limit(2)
    .reverse()
    .execute();
```