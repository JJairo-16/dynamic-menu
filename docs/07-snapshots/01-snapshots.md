# Snapshots

Els snapshots permeten guardar i restaurar l'estat estructural del menú.

> [!WARNING] La snapshot només guarda la referència del context.
>
> Els snapshots de `DynamicMenu` **no restauren l'estat intern del context**, només mantenen la mateixa instància.
>
> Si necessites restaurar també l'estat del context, consulta **[Context Rollback](./03-context-rollback.md)**, que utilitza snapshots compostos per fer un rollback complet del menú i del context.

Encara que internament existeixin `MenuSnapshot` i `MenuSnapshotManager`, l'ús habitual es fa **a través de `DynamicMenu` i els seus wrappers**.

## Obtenció

S'importa utilitzant:

```java
import menu.snapshot.MenuSnapshot;
```

## Operacions principals

Els wrappers principals per treballar amb snapshots són:

- `createSnapshot()`
- `restoreSnapshot(...)`
- `saveCurrentAs(...)`
- `registerSnapshot(...)`
- `useSnapshot(...)`

També hi ha wrappers de gestió i inspecció:

- `removeRegisteredSnapshot(...)`
- `clearRegisteredSnapshots()`
- `hasRegisteredSnapshot(...)`
- `registeredSnapshotCount()`

I wrappers relacionats amb la pila o derivació:

- `pushSnapshot()`
- `pushSnapshot(name)`
- `pushChildSnapshot(...)`
- `popSnapshot()`
- `createChildSnapshot()`
- `createChildSnapshot(title)`

## Què guarda un snapshot

Un snapshot inclou l'estat del menú actual, principalment:

- títol
- opcions
- hooks

## Exemple bàsic

```java
MenuSnapshot<String, Void> snapshot = menu.createSnapshot();

menu.setTitle("Menú temporal");
menu.clearOptions();

menu.restoreSnapshot(snapshot);
```

Aquest és el patró bàsic per modificar temporalment el menú i després tornar a l'estat anterior.

## Snapshots registrats

Quan vols reutilitzar un estat diverses vegades, pots registrar-lo amb un nom.

```java
menu.saveCurrentAs("principal");
menu.useSnapshot("principal");
```

També pots registrar un snapshot creat manualment:

```java
MenuSnapshot<String, Void> snapshot = menu.createSnapshot();
menu.registerSnapshot("copia", snapshot);
```

## Consultar i netejar snapshots registrats

Pots comprovar si existeix un snapshot i eliminar-lo quan ja no calgui.

```java
boolean exists = menu.hasRegisteredSnapshot("principal");
int total = menu.registeredSnapshotCount();

menu.removeRegisteredSnapshot("principal");
menu.clearRegisteredSnapshots();
```

`removeRegisteredSnapshot(...)` retorna `true` si el nom existia i s'ha eliminat.

## Navegació amb pila

Quan vols fer navegació tipus *entrar / tornar enrere*, la pila interna és molt útil.

```java
menu.pushSnapshot();
menu.setTitle("Configuració");

menu.popSnapshot();
```

També pots combinar guardat i càrrega d'un snapshot registrat:

```java
menu.pushSnapshot("settings");
```

## Snapshots fills

Quan vols derivar un nou estat a partir de l'actual, pots crear un snapshot fill.

```java
MenuSnapshot<String, Void> child = menu.createChildSnapshot("Configuració");
```

I després activar-lo sobre el menú actual:

```java
menu.pushChildSnapshot(child);
```

Això permet entrar en un nou estat mantenint intacta la possibilitat de tornar enrere amb la pila.

## Quan convé usar snapshots

Són especialment útils quan:

- canvies temporalment el títol o les opcions
- vols preparar estats reutilitzables del menú
- necessites navegació manual dins d'un mateix menú
- no cal crear un menú fill complet
