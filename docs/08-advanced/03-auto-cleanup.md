# Auto Cleanup

Aquesta pàgina descriu el sistema de neteja automàtica del menú.

Aquest sistema serveix per evitar que el consum de memòria creixi de manera innecessària quan es treballa amb:

- snapshots registrats
- pila interna de snapshots
- menús fills

La configuració es fa amb `MenuCleanupConfig`, que viu a `menu.config`.

## Per què existeix

Un `DynamicMenu` pot conservar estat intern entre execucions.

Això inclou, per exemple:

- snapshots registrats amb nom
- snapshots desats a la pila interna
- snapshots heretats per menús fills

Si es creen molts snapshots o molts menús fills, el cost de memòria pot créixer amb el temps.

La neteja automàtica permet controlar aquest creixement.

## Activació simple

La forma més fàcil d'activar-la és:

```java
menu.autoCleanup(true);
```

Quan es fa això, el menú utilitza una configuració per defecte raonable.

Pots consultar l'estat amb:

```java
boolean enabled = menu.isAutoCleanupEnabled();
MenuCleanupConfig config = menu.getCleanupConfig();
```

## Configuració avançada

També es pot configurar manualment:

```java
menu.cleanupConfig(
    MenuCleanupConfig.defaults()
        .withMaxNamedSnapshots(10)
        .withInheritNamedSnapshotsInChildMenus(false)
        .withCopyOnlyCurrentSnapshotToChild(true)
);
```

Això activa la neteja automàtica amb una configuració personalitzada.

## Què controla `MenuCleanupConfig`

`MenuCleanupConfig` permet definir, entre altres coses:

- si s'ha de buidar la pila de snapshots en acabar `run()`
- quants snapshots registrats es conserven
- si s'han d'eliminar els més antics quan se supera el límit
- si els menús fills han d'heretar snapshots registrats
- si el fill només ha de rebre l'snapshot actual
- cada quantes iteracions s'ha d'executar una neteja automàtica
  - `0` → desactiva la neteja periòdica
  - `N > 0` → la neteja s'executa cada `N` iteracions del menú

## Presets disponibles

La classe inclou tres factories útils:

```java
MenuCleanupConfig.defaults();
MenuCleanupConfig.aggressive();
MenuCleanupConfig.disabled();
```

## Exemple simple

```java
DynamicMenu<String, Void> menu =
    DynamicMenu.withoutContext("Menú principal", selector)
        .autoCleanup(true);
```

## Exemple avançat

```java
menu.cleanupConfig(
    MenuCleanupConfig.defaults()
        .withMaxNamedSnapshots(5)
        .withRemoveOldestNamedSnapshotsWhenLimitExceeded(true)
        .withClearSnapshotStackAfterRun(true)
        .withInheritNamedSnapshotsInChildMenus(false)
        .withCopyOnlyCurrentSnapshotToChild(true)
        .withIterationsForCleanup(15)
);
```

## Relació amb altres wrappers

La neteja automàtica impacta sobretot sobre wrappers com:

- `saveCurrentAs(...)`
- `registerSnapshot(...)`
- `clearRegisteredSnapshots()`
- `pushSnapshot()`
- `pushSnapshot(name)`
- `popSnapshot()`
- `clearSnapshotStack()`
- `createChildMenu(...)`

Això vol dir que la política de neteja no és una funció separada, sinó una capa de comportament aplicada sobre la resta de wrappers del menú.

## Quan convé activar-la

És recomanable activar-la si:

- crees molts snapshots durant la vida del menú
- crees molts menús fills
- no vols conservar molta navegació temporal
- vols limitar millor el cost de memòria

## Quan pot no fer falta

Pot no ser necessària si:

- el menú és petit
- tens pocs snapshots
- no crees menús fills sovint
- controles manualment la neteja
