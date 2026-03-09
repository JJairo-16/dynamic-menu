package menu.hook;

/**
 * Hook executat en diferents punts del bucle del menú.
 *
 * @param <T> tipus del valor de retorn del menú
 * @param <C> tipus del context del menú
 */
@FunctionalInterface
public interface MenuLoopHook<T, C> {

    /**
     * Executa el hook amb l'estat actual del bucle.
     *
     * @param state estat actual del bucle de menú
     */
    void execute(MenuLoopState<T, C> state);
}
