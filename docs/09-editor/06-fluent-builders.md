# 09-editor/06-fluent-builders.md

# Fluent Builders

A més dels mètodes estàtics, `MenuEditor` ofereix una API fluïda basada en dos builders públics:

- `RemoveBuilder<T, C>`
- `ReplaceBuilder<T, C>`

Aquests builders serveixen per descriure operacions d’edició de manera declarativa i llegible.

Són especialment útils quan necessites combinar:

- selectors
- rangs
- límits
- recorregut invers
- transformacions de label, acció o opció completa

---

# 1. RemoveBuilder

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

# 2. Mètodes públics de `RemoveBuilder`

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

# 3. Patrons habituals amb `RemoveBuilder`

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

---

# 4. ReplaceBuilder

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

# 5. Mètodes públics de `ReplaceBuilder`

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

# 6. Reemplaç només del label

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

# 7. Reemplaç només de l’acció

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

# 8. Reemplaç de l’opció completa

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

# 9. Configuració comuna de `ReplaceBuilder`

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

# 10. Patrons habituals amb `ReplaceBuilder`

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

---

# 11. Diferència pràctica entre `RemoveBuilder` i `ReplaceBuilder`

## Quan fer servir `RemoveBuilder`

Quan vols **eliminar** opcions.

Exemples típics:

- netejar opcions temporals
- suprimir duplicats
- esborrar una opció concreta pel seu label

## Quan fer servir `ReplaceBuilder`

Quan vols **mantenir l’opció però transformar-la**.

Exemples típics:

- canviar el text visible
- canviar el comportament d’una opció
- migrar diverses opcions a un nou format

---

# 12. Errors habituals

## Oblidar el selector

A tots dos builders, `where(...)` és obligatori si no has passat ja el selector a `MenuEditor.remove(menu, selector)` o `MenuEditor.replace(menu, selector)`.

Aquest codi és incorrecte:

```java
MenuEditor.replace(menu)
    .label("Configuració")
    .execute();
```

Perquè no hi ha condició de selecció.

## Oblidar la transformació al replace

En `ReplaceBuilder`, a més del selector, també cal definir **què** s’ha de canviar.

Aquest codi també és incorrecte:

```java
MenuEditor.replace(menu)
    .where((index, option) -> true)
    .execute();
```

Perquè no s’ha definit ni `label(...)`, ni `action(...)`, ni `option(...)`, ni `map(...)`.

## Confondre `first()` i `last()`

- `first()` busca en sentit normal i limita a una coincidència
- `last()` busca en sentit invers i limita a una coincidència

---

# 13. Recomanació d’aprenentatge

Si un usuari nou vol dominar aquesta llibreria, el camí recomanat és:

1. aprendre primer `removeIf(...)` i `removeFirstIf(...)`
2. entendre `Range`
3. entendre `EditConfig`
4. aprendre `RemoveBuilder`
5. aprendre `ReplaceBuilder`
6. combinar-ho amb els helpers de consulta de `MenuEditor`

Amb això ja es pot utilitzar pràcticament tota la superfície pública de l’editor.