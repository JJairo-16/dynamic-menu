package menu.model;

import java.util.Objects;

import menu.action.*;

/**
 * Representa una opció de menú composta per una etiqueta i una acció.
 *
 * @param <T> tipus del valor de retorn del menú
 * @param <C> tipus del context del menú
 */
public record MenuOption<T, C>(
        String label,
        MenuRuntimeAction<T, C> action) {

    /**
     * Constructor canònic amb validació d'arguments.
     *
     * @param label  etiqueta visible de l'opció
     * @param action acció associada a l'opció
     * @throws NullPointerException si algun argument és {@code null}
     */
    public MenuOption {
        Objects.requireNonNull(label, "L'etiqueta de l'opció no pot ser nul·la");
        Objects.requireNonNull(action, "L'acció de l'opció no pot ser nul·la");
    }

    /**
     * Crea una opció a partir d'una acció amb context.
     *
     * @param label  etiqueta visible
     * @param action acció basada només en el context
     * @param <T>    tipus del valor de retorn del menú
     * @param <C>    tipus del context del menú
     * @return nova opció de menú
     */
    public static <T, C> MenuOption<T, C> of(String label, MenuAction<T, C> action) {
        Objects.requireNonNull(action, "L'acció de l'opció no pot ser nul·la");
        return new MenuOption<>(label, (context, menu) -> action.execute(context));
    }

    /**
     * Crea una opció a partir d'una acció simple.
     *
     * @param label  etiqueta visible
     * @param action acció simple
     * @param <T>    tipus del valor de retorn del menú
     * @param <C>    tipus del context del menú
     * @return nova opció de menú
     */
    public static <T, C> MenuOption<T, C> of(String label, SimpleMenuAction<T> action) {
        Objects.requireNonNull(action, "L'acció de l'opció no pot ser nul·la");
        return new MenuOption<>(label, (context, menu) -> action.execute());
    }

    /**
     * Crea una opció a partir d'una acció amb accés al menú.
     *
     * @param label  etiqueta visible
     * @param action acció amb accés al menú
     * @param <T>    tipus del valor de retorn del menú
     * @param <C>    tipus del context del menú
     * @return nova opció de menú
     */
    public static <T, C> MenuOption<T, C> ofRuntime(String label, MenuRuntimeAction<T, C> action) {
        return new MenuOption<>(label, action);
    }
}