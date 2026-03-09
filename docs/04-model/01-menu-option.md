# MenuOption

`MenuOption` representa una **opció seleccionable del menú**.

Cada opció conté:

- una etiqueta (`label`)
- una acció (`action`)

## Estructura

```java
public record MenuOption<T,C>(
    String label,
    MenuRuntimeAction<T,C> action
)
```

## Crear opcions

### Acció amb context

```java
MenuOption.of("Guardar", ctx -> guardar(ctx));
```

### Acció simple

```java
MenuOption.of("Sortir", () -> MenuResult.exit());
```

### Acció amb accés al menú

```java
MenuOption.ofRuntime("Configuració", (ctx, menu) -> {

    menu.pushSnapshot("settings");

    return MenuResult.continueLoop();

});
```

## Notes

`MenuOption` és immutable perquè està implementada com un `record`.