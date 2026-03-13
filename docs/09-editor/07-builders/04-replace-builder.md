# Replace Builder

`ReplaceBuilder<T, C>` construeix operacions de reemplaç.

S’obté des de:

```java
MenuEditor.replace(menu)
```

o bé:

```java
MenuEditor.replace(menu, selector)
```

## Constructor públic

## `new ReplaceBuilder<>(menu)`

Existeix com a constructor públic, però habitualment es fa servir a través de `MenuEditor.replace(...)`.

```java
ReplaceBuilder<String, Void> builder = new ReplaceBuilder<>(menu);
```

En ús normal, és preferible:

```java
ReplaceBuilder<String, Void> builder = MenuEditor.replace(menu);
```

---

# 1. Mètodes públics de `ReplaceBuilder`

## `where(selector)`

Defineix la condició de selecció.

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().contains("config"));
```

Sense `where(...)`, `execute()` i `executeAny()` fallaran perquè la condició és obligatòria.

## `map(optionMapper)`

Defineix una transformació completa de l’opció.

```java
MenuEditor.replace(menu)
    .where((index, option) -> index % 2 == 0)
    .map((index, option) -> new MenuOption<>(
        option.label().toUpperCase(),
        option.action()
    ))
    .execute();
```

Aquesta és la forma més flexible de replace.

---

# 2. Reemplaç només del label

## `label(String newLabel)`

Substitueix el label mantenint l’acció original.

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().contains("config"))
    .label("Configuració")
    .execute();
```

## `label(LabelMapper<T, C> mapper)`

Calcula el nou label dinàmicament per a cada coincidència.

```java
MenuEditor.replace(menu)
    .where((index, option) -> index < 3)
    .label((index, option) -> "[" + index + "] " + option.label())
    .execute();
```

Aquest mètode només canvia el text. L’acció es conserva.

---

# 3. Reemplaç només de l’acció

`ReplaceBuilder` ofereix quatre variants públiques d’`action(...)`.

## `action(MenuRuntimeAction<T, C> newAction)`

Substitueix directament l’acció runtime.

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Sortir"))
    .action((ctx, runtime) -> MenuResult.exitMenu())
    .execute();
```

## `action(MenuAction<T, C> newAction)`

Accepta una `MenuAction` i la converteix internament a runtime action.

```java
MenuAction<String, Void> action = (ctx, secondary) -> MenuResult.repeatLoop();

MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Tornar"))
    .action(action)
    .execute();
```

## `action(SimpleMenuAction<T> newAction)`

Accepta una `SimpleMenuAction`.

```java
SimpleMenuAction<String> action = ctx -> MenuResult.repeatLoop();

MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Tornar"))
    .action(action)
    .execute();
```

## `action(ActionMapper<T, C> mapper)`

Calcula una nova acció per a cada opció coincident.

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().startsWith("Admin"))
    .action((index, option) -> (ctx, runtime) -> MenuResult.repeatLoop())
    .execute();
```

Aquesta família només canvia el comportament. El label es conserva.

---

# 4. Reemplaç de l’opció completa

`ReplaceBuilder` també permet substituir simultàniament label i acció.

## `option(String newLabel, MenuRuntimeAction<T, C> newAction)`

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Sortir"))
    .option("Tancar", (ctx, runtime) -> MenuResult.exitMenu())
    .execute();
```

## `option(String newLabel, MenuAction<T, C> newAction)`

```java
MenuAction<String, Void> action = (ctx, secondary) -> MenuResult.exitMenu();

MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Sortir"))
    .option("Tancar", action)
    .execute();
```

## `option(String newLabel, SimpleMenuAction<T> newAction)`

```java
SimpleMenuAction<String> action = ctx -> MenuResult.exitMenu();

MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Sortir"))
    .option("Tancar", action)
    .execute();
```

## `option(MenuOption<T, C> newOption)`

Substitueix l’opció completa per una instància concreta.

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Sortir"))
    .option(new MenuOption<>("Tancar", (ctx, runtime) -> MenuResult.exitMenu()))
    .execute();
```

---

# 5. Configuració comuna de `ReplaceBuilder`

A partir d’aquí, `ReplaceBuilder` comparteix la mateixa filosofia que `RemoveBuilder`.

## `range(range)`

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .label("Temporal")
    .range(Range.of(0, 5))
    .execute();
```

## `range(fromInclusive, toExclusive)`

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .label("Temporal")
    .range(0, 5)
    .execute();
```

## `limit(limit)`

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .label("Temporal")
    .limit(2)
    .execute();
```

## `reverse()`

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .label("Temporal")
    .reverse()
    .execute();
```

## `reverse(boolean reverse)`

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .label("Temporal")
    .reverse(true)
    .execute();
```

## `first()`

Afecta només la primera coincidència.

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Config"))
    .label("Configuració")
    .first()
    .execute();
```

## `last()`

Afecta només l’última coincidència.

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Config"))
    .label("Configuració")
    .last()
    .execute();
```

## `all()`

Fa explícit que s’han de considerar totes les coincidències.

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Config"))
    .label("Configuració")
    .all()
    .execute();
```

## `config(config)`

Carrega la configuració des d’un `EditConfig`.

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .label("Temporal")
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

Construeix la configuració equivalent a l’estat actual del builder.

```java
EditConfig config = MenuEditor.replace(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .label("Temporal")
    .range(0, 10)
    .limit(2)
    .reverse(true)
    .buildConfig();
```

## `execute()`

Executa el replace i retorna el nombre d’elements afectats.

```java
int changed = MenuEditor.replace(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .label("Temporal")
    .execute();
```

## `executeAny()`

Executa el replace i retorna `true` si s’ha modificat almenys una opció.

```java
boolean changed = MenuEditor.replace(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .label("Temporal")
    .executeAny();
```

---

# 6. Patrons habituals amb `ReplaceBuilder`

## Canviar només el label

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().contains("config"))
    .label("Configuració")
    .execute();
```

## Canviar només l’acció

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Sortir"))
    .action((ctx, runtime) -> MenuResult.exitMenu())
    .execute();
```

## Canviar label i acció alhora

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Sortir"))
    .option("Tancar", (ctx, runtime) -> MenuResult.exitMenu())
    .execute();
```

## Transformar l’opció completa

```java
MenuEditor.replace(menu)
    .where((index, option) -> index % 2 == 0)
    .map((index, option) -> new MenuOption<>(
        "[PARELL] " + option.label(),
        option.action()
    ))
    .execute();
```

## Reemplaçar només l’última coincidència

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().equals("Config"))
    .label("Configuració")
    .last()
    .execute();
```

## Reemplaçar dins d’un rang amb límit

```java
MenuEditor.replace(menu)
    .where((index, option) -> option.label().startsWith("Temp"))
    .label("Temporal")
    .range(0, 8)
    .limit(2)
    .execute();
```
