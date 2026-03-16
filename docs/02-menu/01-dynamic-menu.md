# DynamicMenu

`DynamicMenu` és el motor principal del sistema de menús.

S'encarrega de:

- executar el bucle del menú
- conservar el context del menú
- delegar la selecció al `MenuSelector`
- gestionar opcions, hooks, snapshots i polítiques a través de la seva API pública
- crear menús fills

## Obtenció

S'importa utilitzant:

```java
import menu.DynamicMenu;
```

## Tipus genèrics

`DynamicMenu` utilitza dos tipus genèrics:

- `T` és el tipus del valor que el menú pot retornar quan finalitza
- `C` és el tipus del context compartit entre accions

Per exemple:

```java
DynamicMenu<String, AppContext> menu;
```

significa que:

- el menú pot retornar un `String`
- el context compartit és `AppContext`

El tipus `T` **no és especial**. Pot ser qualsevol tipus (`String`, `Boolean`, `Integer`, un objecte propi, etc.).  
En molts exemples es fa servir `String` simplement per simplicitat.

Si el menú no ha de retornar cap valor, és habitual utilitzar `Void`.

---

## Crear un menú sense context

Aquest és el punt d'entrada més simple.

```java
DynamicMenu<String, Void> menu =
    DynamicMenu.withoutContext("Menú principal", selector);
```

Aquest estil és ideal quan les accions no necessiten compartir estat entre elles.

---

## Crear un menú amb context

Quan diferents opcions han de compartir informació, es pot passar un context.

```java
AppContext context = new AppContext(scanner, currentUser);

DynamicMenu<String, AppContext> menu =
    new DynamicMenu<>("Menú principal", context, selector);
```

A partir d'aquí, les accions i hooks poden accedir al mateix objecte `context`.

---

## Executar el menú

```java
String result = menu.run();
```

`run()` entra en un bucle i només acaba quan una acció retorna un `MenuResult` que atura l'execució.

En la pràctica, això fa que `MenuResult` sigui una peça central del sistema:

- `MenuResult.repeatLoop()` continua el menú
- `MenuResult.returnValue(value)` finalitza el menú retornant un valor
- `MenuResult.exitMenu()` finalitza el menú retornant `null`

---

## Mètodes importants

Alguns dels mètodes més utilitzats de `DynamicMenu` són:

- `addOption(...)`
- `addOptionAt(...)`
- `addOptionBefore(...)`
- `addOptionAfter(...)`
- `moveOptionToStart(...)`
- `moveOptionToEnd(...)`
- `moveOptionToIndex(...)`
- `moveOptionBefore(...)`
- `moveOptionAfter(...)`
- `removeOption(...)`
- `removeOptionAt(...)`
- `removeAllOptions(...)`
- `clearOptions()`
- `setTitle(...)`
- `createSnapshot()`
- `restoreSnapshot(...)`
- `saveCurrentAs(...)`
- `registerSnapshot(...)`
- `useSnapshot(...)`
- `pushSnapshot()`
- `pushSnapshot(name)`
- `pushChildSnapshot(...)`
- `popSnapshot()`
- `createChildSnapshot(...)`
- `createChildMenu(...)`
- `setDuplicatePolicy(...)`
- `getDuplicatePolicy()`
- `modifiableDuringRun(...)`
- `autoCleanup(...)`
- `cleanupConfig(...)`

---

## Polítiques del menú

`DynamicMenu` exposa dos grans tipus de configuració avançada:

### Obtenció

S'importa utilitzant:

```java
import menu.snapshot.MenuDuplicatePolicy;
```

### Política de duplicats

Controla com es gestionen etiquetes repetides o situacions similars en operacions sobre opcions i snapshots.

```java
menu.setDuplicatePolicy(policy);
MenuDuplicatePolicy current = menu.getDuplicatePolicy();
```

La política concreta es descriu a la secció de polítiques avançades.

### Política de neteja

Controla com es netegen snapshots registrats, pila i herència cap a menús fills.

```java
menu.autoCleanup(true);
menu.cleanupConfig(MenuCleanupConfig.defaults());
```

---

## Flux general d'execució

A nivell conceptual, `run()` funciona així:

1. llegeix una instantània estable de l'estat actual del menú
2. executa `beforeEachDisplay`, si existeix
3. demana una opció al selector
4. executa `beforeEachAction`, si existeix
5. executa l'acció de l'opció seleccionada
6. executa `afterEachAction`, si existeix
7. interpreta el `MenuResult`

Això explica per què, quan les modificacions durant `run()` estan permeses, els canvis acostumen a aplicar-se a la **següent iteració**, no a la que ja s'està executant.

---

## Inspecció de l'estat

També hi ha mètodes útils per consultar l'estat actual del menú:

```java
boolean running = menu.isRunning();
boolean modifiable = menu.isModifiableDuringRun();

String title = menu.getTitle();
int optionCount = menu.optionCount();
int stackSize = menu.snapshotStackSize();
int registered = menu.registeredSnapshotCount();
```