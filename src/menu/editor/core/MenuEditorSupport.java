package menu.editor.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import menu.DynamicMenu;
import menu.action.MenuAction;
import menu.action.MenuRuntimeAction;
import menu.action.SimpleMenuAction;
import menu.editor.Range;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;
import menu.snapshot.MenuSnapshot;

public class MenuEditorSupport {
    private MenuEditorSupport() {}

    public static <T, C> OptionSelector<T, C> alwaysFalseSelector() {
        return (index, option) -> false;
    }

    public static <T, C> OptionSelector<T, C> alwaysTrueSelector() {
        return (index, option) -> true;
    }

    public static <T, C> OptionSelector<T, C> exactLabelSelector(String label) {
        return (index, option) -> Objects.equals(option.label(), label);
    }

    public static <T, C> Comparator<MenuOption<T, C>> defaultLabelComparator() {
        return Comparator.comparing(
                MenuOption<T, C>::label,
                String.CASE_INSENSITIVE_ORDER).thenComparing(MenuOption::label);
    }

    public static <T, C> List<MenuOption<T, C>> currentOptions(DynamicMenu<T, C> menu) {
        return new ArrayList<>(menu.getCurrentOptionSnapshot());
    }

    public static void validateExistingIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Índex fora de rang: " + index + ", mida actual: " + size);
        }
    }

    public static void validateRange(Range range, int size) {
        int from = range.fromInclusive();
        int to = range.toExclusive();

        if (from < 0)
            throw new IndexOutOfBoundsException("L'inici del rang no pot ser negatiu: " + from);

        if (to < 0)
            throw new IndexOutOfBoundsException("El final del rang no pot ser negatiu: " + to);

        if (from > to)
            throw new IllegalArgumentException("L'inici del rang no pot ser major que el final: " + from + " > " + to);

        if (from > size)
            throw new IndexOutOfBoundsException("L'inici del rang està fora de la mida actual: " + from + " > " + size);

        if (to > size && to != Integer.MAX_VALUE)
            throw new IndexOutOfBoundsException("El final del rang està fora de la mida actual: " + to + " > " + size);
    }

    public static <T, C> void rebuildSnapshot(MenuSnapshot<T, C> snapshot, List<MenuOption<T, C>> options) {
        snapshot.clearOptions();
        for (MenuOption<T, C> option : options) {
            snapshot.addOption(option.label(), option.action());
        }
    }

    public static <T, C> MenuOption<T, C> newOption(String label, MenuRuntimeAction<T, C> action) {
        Objects.requireNonNull(label, "El label no pot ser nul");
        Objects.requireNonNull(action, "El comportament no pot ser nul");
        return new MenuOption<>(label, action);
    }

    public static <T, C> MenuRuntimeAction<T, C> runtimeOf(MenuAction<T, C> action) {
        Objects.requireNonNull(action, "El comportament no pot ser nul");
        return (context, menu) -> action.execute(context);
    }

    public static <T, C> MenuRuntimeAction<T, C> runtimeOf(SimpleMenuAction<T> action) {
        Objects.requireNonNull(action, "El comportament no pot ser nul");
        return (context, menu) -> action.execute();
    }

}
