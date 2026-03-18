package menu.editor.helpers;

import menu.model.MenuOption;

/** Condició per decidir si una opció ha de ser afectada. */
@FunctionalInterface
public interface OptionSelector<T, C> {

    /**
     * Avalua si l'opció indicada compleix la condició.
     *
     * @param index índex actual de l'opció
     * @param option opció actual
     * @return {@code true} si compleix la condició
     */
    boolean test(int index, MenuOption<T, C> option);
}