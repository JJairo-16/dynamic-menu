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
- refèrencia al context

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

## Edició parcial d'opcions dins d'un snapshot

A més dels wrappers de `DynamicMenu`, `MenuSnapshot` també ofereix operacions pròpies per modificar opcions concretes sense haver de buidar i reconstruir tota la llista.

Això és especialment útil en operacions internes com:

- reemplaçar una opció per índex
- actualitzar només una etiqueta o una acció
- reconstruir només un rang ja ordenat o transformat
- evitar un `clearOptions()` + `addOption(...)` complet quan només canvien algunes posicions

### Reemplaçar una opció concreta

Pots reemplaçar directament una opció existent per índex:

- `setOptionAt(int index, MenuOption<T, C> option)`
- `setOptionAt(int index, String label, MenuAction<T, C> action)`
- `setOptionAt(int index, String label, SimpleMenuAction<T> action)`
- `setOptionAt(int index, String label, MenuRuntimeAction<T, C> action)`

Exemple:

```java
MenuSnapshot<String, Void> snapshot = menu.createSnapshot();

snapshot.setOptionAt(0, "Nova opció", () -> "ok");

menu.restoreSnapshot(snapshot);
```

Aquesta operació:

- manté la mida de la llista
- no reordena cap element
- només substitueix la posició indicada

### Reemplaçar un rang d'opcions

Quan ja tens un bloc contigu d'opcions noves, pots reemplaçar-lo de cop amb:

- `replaceOptions(int fromInclusive, List<MenuOption<T, C>> options)`

El nombre d'opcions de reemplaç ha de coincidir amb la mida del rang afectat. Aquesta operació tampoc canvia la mida total del snapshot.

Exemple:

```java
MenuSnapshot<String, Void> snapshot = menu.createSnapshot();
List<MenuOption<String, Void>> current = snapshot.getOptionSnapshot();

List<MenuOption<String, Void>> replacement = List.of(
        MenuOption.of("A", () -> "a"),
        MenuOption.of("B", () -> "b"),
        MenuOption.of("C", () -> "c")
);

snapshot.replaceOptions(2, replacement);

menu.restoreSnapshot(snapshot);
```

Això reemplaça les posicions `2`, `3` i `4`.

### Quan convé usar aquestes APIs

Aquestes APIs són útils sobretot quan:

- ja saps exactament quins índexs vols modificar
- no vols reconstruir totes les opcions del snapshot
- estàs implementant edició parcial, ordenació per rang o reemplaços per lots
- la llista manté el mateix nombre d'elements

### Quan no calen

Si vols:

- afegir opcions noves
- eliminar opcions
- canviar la mida del menú
- reconstruir completament l'estat

aleshores continua sent més natural usar:

- `addOption(...)`
- `addOptionAt(...)`
- `removeOptionAt(...)`
- `clearOptions()`

## Quan convé usar snapshots

Són especialment útils quan:

- canvies temporalment el títol o les opcions
- vols preparar estats reutilitzables del menú
- necessites navegació manual dins d'un mateix menú
- no cal crear un menú fill complet
