package menu.editor.helpers;

import menu.model.MenuOption;

/** Transformador de labels. */
@FunctionalInterface
public interface LabelMapper<T, C> {

    /**
     * Genera el nou label.
     *
     * @param index índex actual de l'opció
     * @param option opció actual
     * @return nou label
     */
    String map(int index, MenuOption<T, C> option);
}