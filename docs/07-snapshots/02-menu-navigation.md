# Menu Navigation

La navegació del menú es pot modelar de diverses maneres segons el tipus de flux que vulguis construir.

Per evitar que la secció advanced es converteixi en un calaix de sastre, és útil separar tres estratègies:

- mutar temporalment el mateix menú
- usar snapshots i pila per implementar *anar enrere*
- crear menús fills independents

## 1. Canviar temporalment el mateix menú

Aquest patró és útil quan vols reutilitzar la mateixa instància de `DynamicMenu` i canviar-ne l'estat durant un temps.

```java
MenuSnapshot<String, Void> snapshot = menu.createSnapshot();

menu.setTitle("Configuració");
menu.clearOptions();
menu.addOption("Tornar", () -> {
    menu.restoreSnapshot(snapshot);
    return MenuResult.repeatLoop();
});
```

Això funciona bé per menús simples o pantalles temporals.

## 2. Navegació amb pila

La pila interna permet conservar l'estat anterior i recuperar-lo després.

### Entrar en un altre estat

```java
menu.pushSnapshot();
menu.setTitle("Configuració");
```

### Tornar enrere

```java
menu.popSnapshot();
```

### Carregar un snapshot registrat guardant l'actual

```java
menu.saveCurrentAs("main");
menu.saveCurrentAs("settings");

menu.pushSnapshot("settings");
```

### Consultar o netejar la pila

```java
int depth = menu.snapshotStackSize();
menu.clearSnapshotStack();
```

Aquest patró és molt útil quan un mateix menú té diversos estats i vols implementar una navegació semblant a una pila de pantalles.

## 3. Navegació amb snapshots fills

Un snapshot fill et permet derivar un nou estat a partir del menú actual.

```java
MenuSnapshot<String, Void> child = menu.createChildSnapshot("Configuració");
menu.pushChildSnapshot(child);
```

És una alternativa molt bona quan vols un subestat del mateix menú sense crear una nova instància executable.

## 4. Menús fills

Quan un submenú té entitat pròpia, sovint és millor crear un menú fill.

```java
menu.addOption("Configuració", (ctx, runtime) -> {
    DynamicMenu<String, AppContext> child = runtime.createChildMenu("Configuració");

    child.clearOptions();
    child.addOption("Tornar", () -> MenuResult.returnValue("back"));

    child.run();
    return MenuResult.repeatLoop();
});
```

Aquest enfocament és especialment clar quan el submenú té moltes opcions o una lògica pròpia.

## `returnValue` com a mecanisme de navegació

En menús fills, `returnValue(...)` és una manera molt pràctica de comunicar al menú pare què ha passat.

```java
String result = child.run();
if ("back".equals(result)) {
    // continuar al menú pare
}
```

## Quan usar cada enfocament

### Usa snapshots sobre el mateix menú si:

- el canvi d'estat és curt
- no cal aïllar gaire la lògica
- vols reconfigurar ràpidament el menú actual

### Usa pila de snapshots si:

- vols una navegació tipus *back*
- tens diversos estats temporals del mateix menú
- necessites recuperar fàcilment l'estat anterior

### Usa snapshots fills si:

- vols derivar un subestat a partir de l'actual
- vols continuar sobre la mateixa instància
- no cal un `run()` separat

### Usa menús fills si:

- el submenú és una unitat clara
- vols encapsular-ne les opcions
- el flux pare/fill es llegeix millor que una mutació del mateix menú

## Recomanació pràctica

No hi ha una única manera correcta de navegar. En menús petits, snapshots i pila són molt còmodes. Quan comencin a aparèixer diverses regles de comportament, reutilització o polítiques, reparteix la documentació entre navegació, gestió d'opcions i polítiques per mantenir-la clara.
