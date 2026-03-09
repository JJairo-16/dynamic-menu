# MenuRuntimeAction

`MenuRuntimeAction` és una acció que té accés tant al **context** com al **menú en execució**.

Això permet modificar el menú dinàmicament.

## Signatura

```java
@FunctionalInterface
public interface MenuRuntimeAction<T, C> {

    MenuResult<T> execute(C context, DynamicMenu<T,C> menu);

}
```

## Exemple

```java
menu.addOption("Configuració", (ctx, menu) -> {

    menu.pushSnapshot("settings");

    return MenuResult.continueLoop();

});
```

## Casos d'ús habituals

- navegar entre snapshots
- crear menús fills
- modificar opcions en temps d'execució
- canviar el títol del menú