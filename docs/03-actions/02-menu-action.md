# MenuAction

`MenuAction` representa una acció que utilitza **només el context del menú**.

És una interfície funcional pensada per definir accions simples.

## Obtenció

S'importa utilitzant:

```java
import menu.action.MenuAction;
```

## Signatura

```java
@FunctionalInterface
public interface MenuAction<T, C> {

    MenuResult<T> execute(C context);

}
```

## Exemple

```java
menu.addOption("Guardar", ctx -> {
    guardarDades(ctx);
    return MenuResult.repeatLoop();
});
```

## Quan utilitzar-la

Utilitza `MenuAction` quan:

- només necessites el **context**
- no necessites accedir al menú
- no modificaràs snapshots o opcions
