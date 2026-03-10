# Child Menus

Els menús fills són menús independents creats a partir de l'estat actual del menú.

Es creen amb la API de `DynamicMenu`, no construint manualment snapshots interns.

## Què hereten

Quan es crea un menú fill amb `createChildMenu(...)`, el fill reutilitza:

- el mateix selector
- la configuració rellevant del menú original
- una còpia de l'estat estructural actual del menú

El context pot:

- compartir-se amb el pare
- copiar-se
- o ser completament nou

La política exacta d'herència de snapshots registrats pot canviar si tens activada la neteja automàtica amb `MenuCleanupConfig`.

---

# Crear un menú fill

La forma més simple és crear un fill que comparteixi el context amb el pare.

```java
DynamicMenu<String, AppContext> child = menu.createChildMenu("Configuració");
```

També pots conservar el títol actual:

```java
DynamicMenu<String, AppContext> child = menu.createChildMenu();
```

---

# Crear un menú fill amb context

La API també permet controlar com es gestiona el context.

## Compartir el context del pare

Aquest és el comportament per defecte.

```java
DynamicMenu<String, AppContext> child = menu.createChildMenu("Configuració");
```

Pare i fill utilitzen **la mateixa instància de context**.

---

## Copiar el context del pare

Si vols que el fill tingui el seu propi context però inicialitzat amb el mateix estat:

```java
DynamicMenu<String, AppContext> child =
    menu.createChildMenuCopyingContext("Configuració", AppContext::new);
```

Això utilitza una funció de còpia (`Function<C,C>`).

En aquest cas:

- el fill comença amb el mateix estat
- però les modificacions no afecten el pare

---

## Utilitzar un context propi

També pots crear el fill amb un context completament diferent.

```java
DynamicMenu<String, AppContext> child =
    menu.createChildMenu("Configuració", appContext2);
```

---

# Executar el menú fill

Un menú fill és completament executable.

```java
String result = child.run();
```

Executar-lo **no substitueix el menú pare**, simplement permet modelar submenús o fluxos jeràrquics.

---

# Crear primer un snapshot fill

Si vols derivar un estat fill però encara no necessites un menú executable, pots crear un `MenuSnapshot` fill.

```java
MenuSnapshot<String, AppContext> childSnapshot =
    menu.createChildSnapshot("Configuració");
```

També existeix la variant que conserva el títol actual:

```java
MenuSnapshot<String, AppContext> childSnapshot =
    menu.createChildSnapshot();
```

Això és útil quan vols preparar o registrar estructures filles abans d'activar-les.

---

# Activar un snapshot fill sobre el mateix menú

Un snapshot fill també es pot carregar sobre la instància actual guardant abans l'estat existent.

```java
MenuSnapshot<String, AppContext> childSnapshot =
    menu.createChildSnapshot("Configuració");

menu.pushChildSnapshot(childSnapshot);
```

Aquest patró és útil quan vols una navegació tipus subpantalla però sense crear una nova instància de `DynamicMenu`.

---

# Exemple complet

```java
menu.addOption("Configuració", (ctx, runtime) -> {

    DynamicMenu<String, AppContext> child =
        runtime.createChildMenu("Configuració");

    child.clearOptions();

    child.addOption("Canviar idioma", innerCtx -> {
        innerCtx.setLanguage("ca");
        return MenuResult.repeatLoop();
    });

    child.addOption("Tornar", () -> MenuResult.returnValue("back"));

    child.run();

    return MenuResult.repeatLoop();
});
```

---

# Quan convé fer servir menús fills

Són especialment útils quan:

- vols separar menús grans en blocs petits
- un submenú té vida pròpia
- necessites reutilitzar un selector i un context existents
- no vols reconstruir l'estat del menú pare manualment

---

# Quan convé fer servir snapshots fills

Els snapshots fills acostumen a encaixar millor si:

- vols continuar sobre la mateixa instància de menú
- necessites suport de `pushSnapshot()` / `popSnapshot()`
- el canvi és temporal
- no cal un `run()` separat