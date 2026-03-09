# SimpleMenuAction

`SimpleMenuAction` és la forma més simple d'acció.

No utilitza:

- context
- menú

Només executa codi i retorna un resultat.

## Signatura

```java
@FunctionalInterface
public interface SimpleMenuAction<T> {

    MenuResult<T> execute();

}
```

## Exemple

```java
menu.addOption("Dir hola", () -> {

    System.out.println("Hola!");

    return MenuResult.continueLoop();

});
```

## Quan utilitzar-la

Utilitza `SimpleMenuAction` quan:

- l'acció no depèn de cap context
- només vols executar una operació simple