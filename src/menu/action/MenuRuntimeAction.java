package menu.action;

import menu.DynamicMenu;
import menu.model.MenuResult;

/**
 * Acció de menú amb accés al context i al menú en execució.
 *
 * <p>Aquesta variant permet canviar entre sets, restaurar snapshots,
 * modificar opcions o navegar entre menús fills sense necessitat de crear
 * instàncies de menú separades.</p>
 *
 * @param <T> tipus del valor de retorn del menú
 * @param <C> tipus del context del menú
 */
@FunctionalInterface
public interface MenuRuntimeAction<T, C> {

    /**
     * Executa l'acció amb accés al context i al menú actual.
     *
     * @param context context del menú; pot ser {@code null}
     * @param menu menú actual
     * @return resultat del menú
     */
    MenuResult<T> execute(C context, DynamicMenu<T, C> menu);
}
