package menu.editor.helpers;

import menu.model.MenuOption;

/** Transformador complet d'opcions. */
@FunctionalInterface
public interface OptionMapper<T, C> {

    /**
     * Construeix la nova opció a partir de l'actual.
     *
     * @param index índex actual de l'opció
     * @param option opció actual
     * @return nova opció
     */
    MenuOption<T, C> map(int index, MenuOption<T, C> option);
}