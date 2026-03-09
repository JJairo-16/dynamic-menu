package menu.snapshot.context;

import java.util.Objects;

import menu.snapshot.MenuSnapshot;

/**
 * Snapshot compost format per l'estat estructural del menú i l'estat intern del
 * context.
 *
 * @param <T> tipus del valor de retorn del menú
 * @param <C> tipus del context del menú
 * @param <S> tipus del snapshot intern del context
 */
public record CompositeMenuSnapshot<T, C, S>(MenuSnapshot<T, C> menuSnapshot, S contextStateSnapshot) {

    public CompositeMenuSnapshot {
        Objects.requireNonNull(menuSnapshot, "El snapshot estructural del menú no pot ser nul");
    }
}