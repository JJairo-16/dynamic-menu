package menu.action;

import menu.model.MenuResult;

/**
 * Acció de menú que rep el context del menú.
 *
 * @param <T> tipus del valor de retorn del menú
 * @param <C> tipus del context del menú
 */
@FunctionalInterface
public interface MenuAction<T, C> {

    /**
     * Executa l'acció utilitzant el context del menú.
     *
     * @param context context del menú; pot ser {@code null}
     * @return resultat del menú
     */
    MenuResult<T> execute(C context);
}
