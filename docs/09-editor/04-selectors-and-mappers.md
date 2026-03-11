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

`MenuEditor` també ofereix dos selectors preparats:

```java
MenuEditor.alwaysTrueSelector();
MenuEditor.alwaysFalseSelector();
```

Són útils per a proves o per expressar casos globals de manera explícita.