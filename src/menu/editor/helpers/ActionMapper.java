package menu.editor.helpers;

import menu.action.MenuRuntimeAction;
import menu.model.MenuOption;

/** Transformador de comportaments. */
@FunctionalInterface
public interface ActionMapper<T, C> {

    /**
     * Genera el nou comportament.
     *
     * @param index índex actual de l'opció
     * @param option opció actual
     * @return nou comportament
     */
    MenuRuntimeAction<T, C> map(int index, MenuOption<T, C> option);
}