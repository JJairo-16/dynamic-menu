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

## Modificacions bàsiques

Un cop seleccionades les opcions, es pot substituir una part concreta del seu contingut.

### `label(...)`

Canvia el label de les opcions coincidents.

```java
MenuEditor.replace(menu)
    .whereAny()
    .label("Nou label")
    .execute();
```

### `action(...)`

Canvia l’acció associada a les opcions coincidents.

```java
MenuEditor.replace(menu)
    .whereAny()
    .action((ctx, runtime) -> MenuResult.repeatLoop())
    .execute();
```

## Substitució completa

Quan no vols modificar només una part,
pots substituir tota l’opció per una altra.

### `option(label, action)`

Construeix una nova opció a partir d’un label i una acció.

```java
MenuEditor.replace(menu)
    .whereAny()
    .option("Nou", action)
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

## Variants del mètode `action`

`ReplaceBuilder` ofereix diverses formes de substituir el comportament d’una opció.

Això permet adaptar-se al tipus d’acció que ja tinguis disponible.

### `action(MenuRuntimeAction)`

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

Indica si almenys una opció ha estat modificada.

```java
boolean changed = MenuEditor.replace(menu)
    .whereAny()
    .label("Nou")
    .executeAny();
```

## Encadenament

`ReplaceBuilder` també es pot integrar en pipelines.

### `thenReplace()`

Continua amb un nou `ReplaceBuilder`.

```java
MenuEditor.replace(menu)
    .whereAny()
    .thenReplace();
```

### `thenRemove()`

Continua amb `RemoveBuilder`.

```java
MenuEditor.replace(menu)
    .whereAny()
    .thenRemove();
```

### `thenSort()`

Continua amb `SortBuilder`.

```java
MenuEditor.replace(menu)
    .whereAny()
    .thenSort()
    .byLabel();
```

### `thenQuery()`

Continua amb `QueryBuilder`.

```java
MenuEditor.replace(menu)
    .whereAny()
    .thenQuery()
    .count();
```