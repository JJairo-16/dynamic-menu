# MenuResult

`MenuResult` defineix què ha de passar després d'executar una acció.

En aquesta llibreria és una peça **gairebé obligatòria**: totes les accions del menú han de retornar un `MenuResult`, i `DynamicMenu.run()` basa el seu control de flux en aquest valor.

Dit d'una altra manera: sense `MenuResult`, el motor no sap si ha de continuar, retornar un valor o finalitzar el menú.

## Tipus de resultat principals

| Mètode                          | Efecte                               |
| ------------------------------- | ------------------------------------ |
| `MenuResult.continueLoop()`     | continua l'execució del menú         |
| `MenuResult.returnValue(value)` | finalitza `run()` retornant un valor |
| `MenuResult.exitMenu()`         | finalitza `run()` retornant `null`   |

## `continueLoop()`
<>
És el resultat més habitual quan una acció només ha de fer alguna operació i permetre que el menú segueixi viu.

```java
menu.addOption("Mostrar saldo", ctx -> {
    System.out.println(ctx.getBalance());
    return MenuResult.continueLoop();
});
```

## `returnValue(value)`

Atura el menú i retorna un valor al codi que ha cridat `run()`.

```java
menu.addOption("Confirmar", () -> MenuResult.returnValue("accepted"));

String result = menu.run();
```

Això és útil quan el menú forma part d'un flux més gran i ha de comunicar un resultat al codi extern.

## `exitMenu()`

Atura el menú sense retornar cap valor útil; `run()` finalitza amb `null`.

```java
menu.addOption("Sortir", () -> MenuResult.exitMenu());
```

És adequat quan vols simplement tancar el menú.

> En cas de que el menú no retorni valor (`Void`), s'utilitzarà sempre en lloc de `exitMenu()`.

## Diferència pràctica entre `returnValue` i `exitMenu`

- `returnValue(value)` transmet informació al codi que ha cridat `run()`
- `exitMenu()` només indica que el menú s'ha acabat

## Important

Les accions del menú **no haurien de retornar `null`**. El motor espera sempre un `MenuResult` vàlid.
