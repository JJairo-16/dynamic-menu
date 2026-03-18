# Replace Builder

`ReplaceBuilder<T, C>` construeix operacions de substitució.

Aquest builder permet modificar el label, l’acció
o substituir completament les opcions seleccionades.

S'obté des de:

```java
MenuEditor.replace(menu)
```

## Selecció

Com en la resta de builders editables, primer es defineix la coincidència objectiu.

```java
MenuEditor.replace(menu)
    .whereLabel(label -> label.equals("Old"))
```

També es poden usar altres variants de selecció, com ara:

- `where(selector)`
- `whereIndex(predicate)`
- `whereLabel(predicate)`
- `whereLabelEquals(String text)`
- `whereLabelEqualsIgnoreCase(String text)`
- `whereLabelStartsWidth(String prefix)`
- `whereLabelEndsWidth(String suffix)`
- `whereAny()`

## Modificacions bàsiques

Un cop seleccionades les opcions, es pot substituir una part concreta del seu contingut.

### `map(OptionMapper)`

Defineix el transformador general d’opcions.

```java
MenuEditor.replace(menu)
    .whereAny()
    .map((index, option) -> new MenuOption<>("Nou", option.action()))
    .execute();
```

És la variant més flexible quan la nova opció depèn de la coincidència original.

### `label(String)`

Canvia el label de les opcions coincidents.

```java
MenuEditor.replace(menu)
    .whereAny()
    .label("Nou label")
    .execute();
```

### `label(LabelMapper)`

Canvia el label a partir d’un transformador.

```java
MenuEditor.replace(menu)
    .whereAny()
    .label((index, option) -> "[EDITAT] " + option.label())
    .execute();
```

## Substitució del comportament

### `action(MenuRuntimeAction)`

Canvia l’acció associada a les opcions coincidents.

```java
MenuEditor.replace(menu)
    .whereAny()
    .action((ctx, runtime) -> MenuResult.repeatLoop())
    .execute();
```

### `action(MenuAction)`

```java
MenuEditor.replace(menu)
    .whereAny()
    .action(action)
    .execute();
```

### `action(SimpleMenuAction)`

```java
MenuEditor.replace(menu)
    .whereAny()
    .action(simpleAction)
    .execute();
```

### `action(ActionMapper)`

Permet generar una nova acció a partir de l’opció original.

```java
MenuEditor.replace(menu)
    .whereAny()
    .action((index, option) -> newAction)
    .execute();
```

Aquesta variant és útil quan la substitució depèn de cada coincidència.

## Substitució completa

Quan no vols modificar només una part, pots substituir tota l’opció per una altra.

### `option(label, action)`

Construeix una nova opció a partir d’un label i una acció.

```java
MenuEditor.replace(menu)
    .whereAny()
    .option("Nou", action)
    .execute();
```

### `option(label, MenuRuntimeAction)`

```java
MenuEditor.replace(menu)
    .whereAny()
    .option("Nou", (ctx, runtime) -> MenuResult.repeatLoop())
    .execute();
```

### `option(label, SimpleMenuAction)`

```java
MenuEditor.replace(menu)
    .whereAny()
    .option("Nou", simpleAction)
    .execute();
```

### `option(MenuOption)`

Substitueix directament per una instància ja creada.

```java
MenuEditor.replace(menu)
    .whereAny()
    .option(new MenuOption<>("Nou", action))
    .execute();
```

## Configuració fluent

Com a builder editable, `ReplaceBuilder` també admet configuració de rang i edició.

### `range(...)`

```java
MenuEditor.replace(menu)
    .whereAny()
    .range(0, 5)
    .label("Nou")
    .execute();
```

### `limit(...)`

```java
MenuEditor.replace(menu)
    .whereAny()
    .limit(2)
    .label("Nou")
    .execute();
```

### `reverse()`

```java
MenuEditor.replace(menu)
    .whereAny()
    .reverse()
    .label("Nou")
    .execute();
```

### `first()`

```java
MenuEditor.replace(menu)
    .whereLabel(label -> label.equals("Duplicada"))
    .first()
    .label("Primera")
    .execute();
```

### `last()`

```java
MenuEditor.replace(menu)
    .whereLabel(label -> label.equals("Duplicada"))
    .last()
    .label("Última")
    .execute();
```

### `all()`

```java
MenuEditor.replace(menu)
    .whereAny()
    .all()
    .label("Global")
    .execute();
```

## Execució

### `execute()`

Aplica la substitució i retorna quantes opcions s’han modificat.

```java
int replaced = MenuEditor.replace(menu)
    .whereAny()
    .label("Nou")
    .execute();
```

### `executeAny()`

Indica si s’ha substituït almenys una opció.

```java
boolean changed = MenuEditor.replace(menu)
    .whereAny()
    .label("Nou")
    .executeAny();
```

## Configuració avançada

`ReplaceBuilder` comparteix la mateixa base d’edició que `RemoveBuilder`.

### `config(config)`

```java
MenuEditor.replace(menu)
    .whereAny()
    .config(
        EditConfig.builder()
            .range(Range.of(0, 10))
            .limit(2)
            .reverse(false)
            .build()
    )
    .label("Nou")
    .execute();
```

### `buildConfig()`

```java
EditConfig config = MenuEditor.replace(menu)
    .whereAny()
    .range(0, 5)
    .limit(1)
    .buildConfig();
```

## Encadenament

`ReplaceBuilder` també pot formar part d’un pipeline.

Per defecte, quan l'encadenament surt de `ReplaceBuilder`, el builder següent no hereta cap estat fluent.

### `thenReplace()`

```java
MenuEditor.replace(menu)
    .whereAny()
    .label("Nou")
    .thenReplace()
    .whereAny()
    .action(action)
    .execute();
```

### `thenRemove()`

```java
MenuEditor.replace(menu)
    .whereAny()
    .label("Nou")
    .thenRemove()
    .whereAny()
    .execute();
```

### `thenSort()`

```java
MenuEditor.replace(menu)
    .whereAny()
    .label("Nou")
    .thenSort()
    .byLabel()
    .apply();
```

### `thenQuery()`

```java
MenuEditor.replace(menu)
    .whereAny()
    .label("Nou")
    .thenQuery()
    .whereAny()
    .count();
```

### `thenShuffle()`

```java
MenuEditor.replace(menu)
    .whereAny()
    .range(0, 10)
    .label("Nou")
    .thenShuffle()
    .apply();
```

En aquest cas, el mode per defecte és `InheritanceMode.RANGE`.

## Herència explícita amb `InheritanceMode`

### `thenReplace(InheritanceMode)`

```java
MenuEditor.replace(menu)
    .whereAny()
    .range(0, 10)
    .label("Nou")
    .thenReplace(InheritanceMode.ALL)
    .action(action)
    .execute();
```

### `thenRemove(InheritanceMode)`

```java
MenuEditor.replace(menu)
    .whereLabel(label -> label.equals("Old"))
    .thenRemove(InheritanceMode.SELECTION)
    .execute();
```

### `thenSort(InheritanceMode)`

```java
MenuEditor.replace(menu)
    .whereAny()
    .range(0, 10)
    .thenSort(InheritanceMode.RANGE)
    .byLabel()
    .apply();
```

### `thenQuery(InheritanceMode)`

```java
MenuEditor.replace(menu)
    .whereLabel(label -> label.equals("Old"))
    .range(0, 10)
    .thenQuery(InheritanceMode.SELECTION)
    .count();
```

### `thenShuffle(InheritanceMode)`

```java
MenuEditor.replace(menu)
    .whereAny()
    .range(0, 10)
    .thenShuffle(InheritanceMode.RANGE)
    .apply();
```

## Modes disponibles

- `InheritanceMode.NONE`: no hereta res
- `InheritanceMode.RANGE`: hereta només el rang
- `InheritanceMode.SELECTION`: hereta selector i rang
- `InheritanceMode.ALL`: hereta tot l'estat compatible amb el builder destí

Quan el destí és `SortBuilder` o `ShuffleBuilder`, l’herència efectiva només afecta el rang.

### Obtenció

S'importa utilitzant:

```java
import menu.editor.builders.base.InheritanceMode;
```