# MenuLoopHook

`MenuLoopHook` permet executar lògica abans o després de cada iteració del menú.

És útil per:

- logging
- validacions
- actualitzar informació

## Signatura

```java
@FunctionalInterface
public interface MenuLoopHook<T,C> {

    void execute(MenuLoopState<T,C> state);

}
```

## Exemple

```java
menu.beforeEachDisplay(state -> {

    System.out.println("Iteració: " + state.iteration());

});
```