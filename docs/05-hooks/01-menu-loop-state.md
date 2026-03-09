# MenuLoopState

`MenuLoopState` conté informació sobre l'estat actual d'una iteració del bucle del menú.

Aquest objecte es passa als hooks del menú (`beforeEachDisplay`, `beforeEachAction`, `afterEachAction`) perquè puguin consultar l'estat de l'execució.

Inclou dades com:

- el context del menú
- la iteració actual
- l'últim resultat obtingut
- l'opció seleccionada
- si el menú continuarà després de l'acció

## Exemple

```java
menu.afterEachAction(state -> {

    if (state.hasSelectedOption()) {
        System.out.println(
            "Opció executada: " + state.selectedOptionText()
        );
    }

    if (state.hasLastResult()) {
        System.out.println(
            "El menú continuarà: " + state.willContinue()
        );
    }

});
```

## Camps principals

| Camp                 | Descripció                                      |
| -------------------- | ----------------------------------------------- |
| context              | context actual del menú                         |
| iteration            | número d'iteració actual                        |
| lastResult           | últim `MenuResult` retornat per una acció       |
| willContinue         | indica si el menú continuarà després de l'acció |
| selectedOptionNumber | número de l'opció seleccionada, si n'hi ha      |
| selectedOptionText   | text de l'opció seleccionada, si n'hi ha        |

## Mètodes útils

`MenuLoopState` també ofereix alguns mètodes de conveniència.

| Mètode              | Descripció                                        |
| ------------------- | ------------------------------------------------- |
| hasLastResult()     | indica si existeix un resultat previ              |
| isFirstIteration()  | indica si aquesta és la primera iteració del menú |
| willEnd()           | indica si el menú finalitzarà després de l'acció  |
| hasSelectedOption() | indica si hi ha una opció seleccionada disponible |

## Quan s'utilitza

Aquest objecte s'utilitza principalment dins dels hooks del menú:

- `beforeEachDisplay`
- `beforeEachAction`
- `afterEachAction`

Permet inspeccionar l'estat del menú sense modificar-lo.