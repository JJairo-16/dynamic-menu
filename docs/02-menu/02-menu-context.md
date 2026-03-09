# Menu Context

El context és l'objecte compartit pel menú i per les seves accions.

No és obligatori, però és molt útil quan diferents opcions necessiten accedir a la mateixa informació sense recórrer a variables globals.

## Menú sense context

Si no necessites compartir estat, pots crear el menú sense context:

```java
DynamicMenu<String, Void> menu =
    DynamicMenu.withoutContext("Menú principal", selector);
```

En aquest cas, les accions simples o sense dependències solen ser suficients.

## Menú amb context

Quan necessites compartir dades entre opcions, defineix un objecte de context.

```java
public final class SessionContext {
    private final String username;
    private int attempts;

    public SessionContext(String username) {
        this.username = username;
        this.attempts = 0;
    }

    public String getUsername() {
        return username;
    }

    public int getAttempts() {
        return attempts;
    }

    public void incrementAttempts() {
        this.attempts++;
    }
}
```

I passa'l al constructor del menú:

```java
SessionContext context = new SessionContext("Jairo");

DynamicMenu<String, SessionContext> menu =
    new DynamicMenu<>("Sessió", context, selector);
```

## Ús del context en una acció

```java
menu.addOption("Incrementar intents", ctx -> {
    ctx.incrementAttempts();
    System.out.println("Intents: " + ctx.getAttempts());
    return MenuResult.continueLoop();
});
```

## Ús del context en un hook

```java
menu.beforeEachDisplay(state -> {
    System.out.println("Usuari actual: " + state.context().getUsername());
});
```

## Accedir al context des del menú

També pots recuperar-lo amb:

```java
SessionContext context = menu.getContext();
```

## Canviar el context des del menú

Es pot canviar la **referència** al context, però ha de ser del mateix tipus:

```java
menu.setContext(newContext);
```

## Quan convé fer servir context

És recomanable quan vols compartir:

- estat de sessió
- dades de l'usuari actual
- serveis reutilitzables
- configuració comuna del menú

## Quan no cal

No cal si:

- totes les accions són independents
- el menú només fa operacions simples
- no hi ha estat compartit entre opcions

## Recomanació pràctica

El context hauria de contenir **estat o dependències de l'aplicació**, no lògica pròpia del menú. La lògica de navegació, snapshots i estructura del menú hauria continuar vivint a `DynamicMenu`.
