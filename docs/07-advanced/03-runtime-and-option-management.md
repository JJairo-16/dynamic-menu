# Runtime and Option Management

Aquesta pàgina agrupa la part de runtime i la gestió d'opcions del menú.

Molts d'aquests mètodes modifiquen l'estat actual de `DynamicMenu`, així que és important entendre **quan es poden cridar** i **quan fan efecte**.

## Regla general: modificació durant `run()`

Per defecte, el menú **no permet modificacions mentre s'està executant**.

```java
menu.modifiableDuringRun(false);
```

Aquest és el comportament per defecte.

Si intentes cridar mètodes de modificació durant `run()` amb aquesta opció desactivada, el menú llençarà una excepció.

Per permetre-ho explícitament:

```java
menu.modifiableDuringRun(true);
```

Pots consultar l'estat actual amb:

```java
boolean modifiable = menu.isModifiableDuringRun();
boolean running = menu.isRunning();
```

## Quan apliquen efecte els canvis

Durant `run()`, el motor treballa sobre una instantània estable de la iteració actual.
Per això, quan les modificacions estan permeses, els canvis estructurals s'apliquen **a la següent iteració**.

Això afecta especialment:

- `setTitle(...)`
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
- `restoreSnapshot(...)`
- `useSnapshot(...)`
- `pushSnapshot(...)`
- `pushChildSnapshot(...)`
- `popSnapshot()`
- `beforeEachDisplay(...)`
- `beforeEachAction(...)`
- `afterEachAction(...)`

## Gestió del títol

```java
menu.setTitle("Configuració");
String title = menu.getTitle();
```

Canviar el títol és una de les operacions més habituals quan un mateix menú canvia entre diversos estats.

## Inspecció d'opcions

```java
menu.hasOption("Guardar");
menu.indexOfOption("Guardar");
menu.optionCount();
menu.getCurrentOptionSnapshot();
```

Aquests mètodes són útils per comprovar l'estat del menú abans de modificar-lo.

## Afegir opcions

### Al final

```java
menu.addOption("Guardar", action);
```

### En una posició concreta

```java
menu.addOptionAt(1, "Guardar", action);
```

### Abans d'una altra opció

```java
menu.addOptionBefore("Sortir", "Guardar", action);
```

### Després d'una altra opció

```java
menu.addOptionAfter("Configuració", "Reset", action);
```

Tots aquests mètodes tenen sobrecàrregues per a `MenuAction`, `MenuRuntimeAction` i `SimpleMenuAction`.

## Moure opcions

Els wrappers de moviment permeten reordenar el menú sense reconstruir totes les opcions.

```java
menu.moveOptionToStart(3);
menu.moveOptionToEnd(0);
menu.moveOptionToIndex(4, 1);
menu.moveOptionBefore(4, 2);
menu.moveOptionAfter(1, 3);
```

Aquests mètodes treballen per índex i resulten especialment útils quan el menú es construeix o es reorganitza en runtime.

## Eliminar opcions

```java
menu.removeOption("Guardar");
menu.removeOptionAt(0);
menu.removeAllOptions("Separador");
menu.clearOptions();
```

- `removeOption(...)` només elimina la **primera coincidència**
- `removeAllOptions(...)` retorna el nombre d'opcions eliminades

## Hooks de runtime

Els hooks també formen part de l'estat actual del menú.

```java
menu.beforeEachDisplay(hook);
menu.beforeEachAction(hook);
menu.afterEachAction(hook);
```

Si canvies hooks durant l'execució i tens `modifiableDuringRun(true)`, el canvi s'aplicarà normalment a la següent iteració.

## Menús fills des del runtime

```java
DynamicMenu<String, AppContext> child = menu.createChildMenu("Configuració");
```

Això és especialment útil dins d'una `MenuRuntimeAction`, on ja tens accés al menú en execució.

## Exemple típic de modificació en runtime

```java
menu.modifiableDuringRun(true);

menu.addOption("Entrar a configuració", (ctx, runtime) -> {
    runtime.pushSnapshot();
    runtime.setTitle("Configuració");
    runtime.clearOptions();
    runtime.addOption("Tornar", (innerCtx, innerRuntime) -> {
        innerRuntime.popSnapshot();
        return MenuResult.continueLoop();
    });
    return MenuResult.continueLoop();
});
```

Aquest patró és vàlid perquè la modificació durant `run()` està activada.

## Recomanació pràctica

- si el menú és principalment estàtic, mantén `modifiableDuringRun(false)`
- si necessites reconfigurar el menú des d'accions, activa `modifiableDuringRun(true)`
- si cal reordenar opcions, prefereix els wrappers de moviment en lloc de reconstruir tot el menú
- si un submenú és complex, considera `createChildMenu(...)` en lloc de mutar massa el menú actual
