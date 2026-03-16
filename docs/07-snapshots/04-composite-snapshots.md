# Composite Snapshots

Un **composite snapshot** guarda:

- snapshot estructural del menú
- snapshot de l'estat intern del context

Això permet restaurar **tot l'estat del sistema del menú**.

## Estructura

Un snapshot compost es representa amb:

```java
CompositeMenuSnapshot<T,C,S>
```

Conté:

- `MenuSnapshot<T,C>`
- `S contextStateSnapshot`

```java
public record CompositeMenuSnapshot<T,C,S>(
    MenuSnapshot<T,C> menuSnapshot,
    S contextStateSnapshot
)
```

## Gestor de snapshots compostos

Els snapshots compostos es gestionen amb:

```java
CompositeMenuSnapshotSupport
```

Aquest gestor és **extern al menú**.

Manté:

- snapshots registrats
- pila de snapshots
- estat per cada instància de menú

## Crear un snapshot compost

Si el context implementa `ContextStateSnapshotSupport`:

```java
CompositeMenuSnapshotSupport support =
    new CompositeMenuSnapshotSupport();

CompositeMenuSnapshot<String, AppContext, AppContext.State> snapshot =
    support.createSnapshot(menu);
```

Això guarda:

- estat del menú
- estat del context

## Restaurar un snapshot compost

```java
support.restoreSnapshot(menu, snapshot);
```

Internament es restaura:

1. el snapshot del menú
2. el snapshot del context

## Snapshots registrats

Es poden registrar snapshots amb nom.

```java
support.saveCurrentAs(menu, "main");
support.saveCurrentAs(menu, "settings");
```

Restaurar:

```java
support.useSnapshot(menu, "main");
```

Consultar o eliminar:

```java
support.hasRegisteredSnapshot(menu, "main");

support.removeRegisteredSnapshot(menu, "main");

support.clearRegisteredSnapshots(menu);
```

## Pila de snapshots

El gestor també manté una pila per cada menú.

Guardar estat actual:

```java
support.pushSnapshot(menu);
```

Restaurar l'últim snapshot:

```java
support.popSnapshot(menu);
```

Consultar la pila:

```java
support.snapshotStackSize(menu);
```

Netejar-la:

```java
support.clearSnapshotStack(menu);
```

## Navegació amb rollback complet

```java
support.pushSnapshot(menu);

menu.setTitle("Shop");
context.coins -= 10;

support.popSnapshot(menu);
```

Quan es fa `popSnapshot` es recupera:

- estructura del menú
- estat del context