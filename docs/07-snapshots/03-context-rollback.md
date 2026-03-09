# Context Rollback

Els snapshots normals del menú només guarden **l'estat estructural del menú**.

Inclouen:

- títol
- opcions
- hooks
- configuració del menú

Però **no guarden l'estat intern del context**.

Si el context conté estat mutable, restaurar un `MenuSnapshot` no desfà aquests canvis.

Per permetre rollback complet es pot utilitzar un **context snapshoteable**.

---

## Context snapshoteable

Un context és snapshoteable quan pot:

- crear un snapshot del seu estat
- restaurar aquest estat posteriorment

Això es defineix amb la interfície:

```java
public interface ContextStateSnapshotSupport<S> {

    S createContextStateSnapshot();

    void restoreContextStateSnapshot(S snapshot);

}
```

El tipus genèric `S` representa el tipus del snapshot del context.

---

## Exemple de context snapshoteable

```java
public class AppContext implements ContextStateSnapshotSupport<AppContext.State> {

    private String username;
    private int coins;

    public record State(String username, int coins) {}

    @Override
    public State createContextStateSnapshot() {
        return new State(username, coins);
    }

    @Override
    public void restoreContextStateSnapshot(State snapshot) {
        this.username = snapshot.username();
        this.coins = snapshot.coins();
    }

}
```

Ara el context pot guardar i restaurar el seu estat intern.

---

## Limitació dels snapshots normals

Amb snapshots normals:

```java
MenuSnapshot<T,C> snapshot = menu.createSnapshot();

menu.restoreSnapshot(snapshot);
```

Només es restaura:

- l'estructura del menú

Per restaurar també el context cal utilitzar **snapshots compostos**.