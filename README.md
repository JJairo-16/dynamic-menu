# Dynamic Menu - [Jairo Linares](https://github.com/JJairo-16)

![Java Version](https://img.shields.io/badge/Java-21%2B-blue)
![License](https://img.shields.io/badge/License-MIT-green)

---

## ▌Què és?

**Dynamic Menu** és una llibreria lleugera per crear **menús interactius dinàmics en Java**.

Permet construir menús de consola flexibles amb:

- opcions dinàmiques
- submenús
- navegació amb snapshots
- hooks d'execució
- context compartit entre accions

Està pensada per simplificar la creació de **CLI interactius, eines de terminal o jocs basats en menús**.

---

## ▌Documentació

La documentació completa de la llibreria es pot consultar aquí:

**[Índex de la documentació](docs/index.md)**

Inclou guies detallades sobre:

- creació de menús
- accions i context
- gestió d'opcions
- submenús
- snapshots
- hooks
- configuració avançada

---

## ▌Estructura del projecte

La llibreria principal es troba dins del directori [`menu`](src/menu/).

## Components del repositori

| Element    | Descripció                                                           |
| ---------- | -------------------------------------------------------------------- |
| `menu/`    | llibreria principal de menús dinàmics                                |
| `App.java` | aplicació de prova utilitzada per desenvolupar i provar la llibreria |
| `utils/`   | utilitats internes utilitzades durant el desenvolupament             |

## Nota

`App.java` i el paquet `utils` **no formen part de la llibreria pública** *(es poden utilitzar si es desitja, però habitualment no rebran cap tipus d'actualització)*.

Només s'utilitzen per la realització del desenvolupament i proves.

---

## ▌Característiques principals

- Motor de menú basat en bucle (`run()`)
- Sistema d'opcions dinàmiques
- Context compartit entre accions
- Suport per submenús independents
- Sistema de **snapshots** per guardar i restaurar l'estat del menú
- Navegació amb pila (`pushSnapshot` / `popSnapshot`)
- Hooks per executar lògica abans o després d'accions
- Gestió de polítiques i neteja automàtica

---

## ▌Conceptes principals

### ▌DynamicMenu

`DynamicMenu` és el **motor central del sistema**.

Gestiona:

- el bucle del menú
- les opcions disponibles
- la interacció amb el selector
- els snapshots
- els hooks

Exemple bàsic:

```java
DynamicMenu<Void, Void> menu =
    DynamicMenu.withoutContext("Menú principal", selector);

menu.addOption("Hola", () -> {
    System.out.println("Hola!");
    return MenuResult.continueLoop();
});

menu.addOption("Sortir", () -> MenuResult.exitMenu());

menu.run();
```

---

### ▌MenuResult

Cada acció retorna un `MenuResult`, que indica què ha de fer el menú.

| Resultat             | Efecte                               |
| -------------------- | ------------------------------------ |
| `continueLoop()`     | el menú continua                     |
| `returnValue(value)` | el menú finalitza retornant un valor |
| `exitMenu()`         | el menú finalitza retornant `null`   |

Exemple:

```java
return MenuResult.continueLoop();
```

---

### ▌Context del menú

El menú pot tenir un **context compartit** entre totes les accions.

```java
AppContext context = new AppContext();

DynamicMenu<Void, AppContext> menu =
    new DynamicMenu<>("Menú principal", context, selector);
```

Les accions poden utilitzar aquest context per compartir estat.

---

### ▌Submenús

Els menús fills permeten crear **fluxos jeràrquics**.

```java
DynamicMenu<Void, AppContext> child =
    menu.createChildMenu("Configuració");

child.run();
```

Els fills poden:

- compartir el context del pare
- copiar-lo
- o utilitzar un context propi

---

### ▌Snapshots

Els snapshots permeten guardar i restaurar l'estat del menú.

```java
MenuSnapshot<Void, AppContext> snapshot = menu.createSnapshot();

menu.setTitle("Configuració");

menu.restoreSnapshot(snapshot);
```

També es poden utilitzar per implementar navegació tipus **back**:

```java
menu.pushSnapshot();
menu.popSnapshot();
```

---

### ▌Hooks

Els hooks permeten executar codi en diferents punts del bucle del menú.

```java
menu.afterEachAction(state -> {

    if (state.hasSelectedOption()) {
        System.out.println("Opció executada: " + state.selectedOptionText());
    }

});
```

Els hooks disponibles són:

- `beforeEachDisplay`
- `beforeEachAction`
- `afterEachAction`

---

## ▌Exemple complet

```java
DynamicMenu<Void, Void> menu =
    DynamicMenu.withoutContext("Menú principal", selector);

menu.addOption("Opció 1", () -> {
    System.out.println("Has triat l'opció 1");
    return MenuResult.continueLoop();
});

menu.addOption("Sortir", () -> MenuResult.exitMenu());

menu.run();
```

---

## ▌Per a què es pot utilitzar?

Aquesta llibreria és útil per:

- eines CLI interactives
- assistents de configuració
- jocs de consola
- sistemes de menús complexos
- prototips ràpids d'interfícies de terminal

---

## ▌Execució

El menú s'executa cridant el mètode:

```java
menu.run();
```

Aquest mètode inicia el bucle interactiu fins que una acció retorna un `MenuResult` que finalitza el menú.

---

## ▌Llicència

Aquest projecte està sota la llicència [MIT](LICENSE).

---

## ▌Autor

Jairo Linares  
GitHub: https://github.com/JJairo-16