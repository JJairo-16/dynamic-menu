package menu.action;

import menu.model.MenuResult;

/**
 * Acció simple de menú que no necessita context ni accés al menú.
 *
 * @param <T> tipus del valor de retorn del menú
 */
@FunctionalInterface
public interface SimpleMenuAction<T> {

    /**
     * Executa l'acció.
     *
     * @return resultat del menú
     */
    MenuResult<T> execute();
}
