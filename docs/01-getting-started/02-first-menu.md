# First Menu

Aquest exemple mostra com crear un menú simple.

```java
DynamicMenu<String, Void> menu =
    DynamicMenu.withoutContext("Menú principal", selector);

menu.addOption("Dir hola", () -> {
    System.out.println("Hola!");
    return MenuResult.continueLoop();
});

menu.addOption("Sortir", () -> MenuResult.exit());

menu.run();
```

> **Nota:** El paràmetre `selector` ha de ser una implementació de `MenuSelector`.  
> Aquesta interfície defineix el mètode:
>
> ```java
> int getOption(List<String> options, String title);
> ```
>
> Aquest mètode ha de mostrar o processar les opcions disponibles i retornar el número de l'opció seleccionada (començant a **1**).

Exemple de sortida:

```
Menú principal
1. Dir hola
2. Sortir
```
