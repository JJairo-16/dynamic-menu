# Selectors and Mappers

A `menu.editor.helpers` hi ha les interfícies funcionals que permeten descriure **què s’ha d’afectar** i **com s’ha de transformar**.

## OptionSelector

`OptionSelector` és una condició.

Serveix per decidir si una opció concreta compleix el criteri.

Signatura conceptual:

```java
boolean test(int index, MenuOption<T, C> option);
```

Exemple:

```java
OptionSelector<String, Void> selector =
    (index, option) -> option.label().startsWith("Admin");
```

Això es pot utilitzar amb mètodes com:

- `removeIf(...)`
- `replaceIf(...)`
- `replaceLabelIf(...)`
- `replaceActionIf(...)`
- `indexOfFirst(...)`
- `countMatches(...)`

## LabelMapper

`LabelMapper` transforma només el label d’una opció.

Signatura conceptual:

```java
String map(int index, MenuOption<T, C> option);
```

Exemple:

```java
LabelMapper<String, Void> mapper =
    (index, option) -> "[" + index + "] " + option.label();
```

## ActionMapper

`ActionMapper` transforma només l’acció d’una opció.

Signatura conceptual:

```java
MenuRuntimeAction<T, C> map(int index, MenuOption<T, C> option);
```

Exemple:

```java
ActionMapper<String, Void> mapper =
    (index, option) -> (ctx, runtime) -> MenuResult.repeatLoop();
```

## OptionMapper

`OptionMapper` transforma l’opció completa.

Signatura conceptual:

```java
MenuOption<T, C> map(int index, MenuOption<T, C> option);
```

Exemple:

```java
OptionMapper<String, Void> mapper =
    (index, option) -> new MenuOption<>(
        option.label().toUpperCase(),
        option.action()
    );
```

## Diferència entre els mappers

- `LabelMapper` canvia només el text
- `ActionMapper` canvia només el comportament
- `OptionMapper` canvia tota l’opció

## Exemple de reemplaç de labels

```java
MenuEditor.replaceLabelIf(
    menu,
    (index, option) -> option.label().contains("config"),
    (index, option) -> "Configuració"
);
```

## Exemple de reemplaç d’accions

```java
MenuEditor.replaceActionIf(
    menu,
    (index, option) -> option.label().equals("Sortir"),
    (index, option) -> (ctx, runtime) -> MenuResult.exitMenu()
);
```

## Exemple de reemplaç complet

```java
MenuEditor.replaceIf(
    menu,
    (index, option) -> index % 2 == 0,
    (index, option) -> new MenuOption<>(
        "[PARELL] " + option.label(),
        option.action()
    )
);
```

## Selectors de conveniència

`MenuEditor` ofereix dos selectors preparats per als casos més simples:

```java
MenuEditor.alwaysTrueSelector();
MenuEditor.alwaysFalseSelector();
```

### `alwaysTrueSelector()`

Retorna un `OptionSelector` que selecciona **totes** les opcions del menú.

És útil quan vols aplicar una operació global de manera explícita.

```java
MenuEditor.remove(menu)
    .where(MenuEditor.alwaysTrueSelector())
    .limit(1)
    .execute();
```

A nivell conceptual, és equivalent a fer:

```java
(index, option) -> true
```

### `alwaysFalseSelector()`

Retorna un `OptionSelector` que **no selecciona cap** opció.

És útil en proves, en composicions o quan vols desactivar temporalment una condició sense canviar l’estructura del codi.

```java
MenuEditor.remove(menu)
    .where(MenuEditor.alwaysFalseSelector())
    .execute();
```

A nivell conceptual, és equivalent a fer:

```java
(index, option) -> false
```

### Relació amb els builders

Aquests selectors són especialment útils amb `RemoveBuilder` i `ReplaceBuilder`, perquè es poden passar directament a `where(...)`.

En el cas concret de `RemoveBuilder`, `whereAny()` representa la mateixa idea que usar un selector que sempre retorna `true`.

```java
MenuEditor.remove(menu)
    .whereAny()
    .limit(1)
    .execute();
```

Per això, `alwaysTrueSelector()` i `whereAny()` cobreixen casos semblants, però en nivells diferents:

- `alwaysTrueSelector()` és un selector reutilitzable
- `whereAny()` és una drecera específica del builder