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

/** Utilitats de MenuEditorSupport. */
public class MenuEditorSupport {
    private MenuEditorSupport() {
        throw new AssertionError("No es pot instanciar MenuEditorSupport");
    }

    /** Retorna un selector que mai no coincideix. */
    public static <T, C> OptionSelector<T, C> alwaysFalseSelector() {
        return (index, option) -> false;
    }

    /** Retorna un selector que sempre coincideix. */
    public static <T, C> OptionSelector<T, C> alwaysTrueSelector() {
        return (index, option) -> true;
    }

    /** Retorna un selector per etiqueta exacta. */
    public static <T, C> OptionSelector<T, C> exactLabelSelector(String label) {
        return (index, option) -> Objects.equals(option.label(), label);
    }

    private static final Comparator<MenuOption<?, ?>> DEFAULT_LABEL_COMPARATOR = Comparator
            .<MenuOption<?, ?>, String>comparing(
                    MenuOption::label,
                    String.CASE_INSENSITIVE_ORDER)
            .thenComparing(MenuOption::label);

    @SuppressWarnings("unchecked")
    /** Retorna el comparador de labels per defecte. */
    public static <T, C> Comparator<MenuOption<T, C>> defaultLabelComparator() {
        return (Comparator<MenuOption<T, C>>) (Comparator<?>) DEFAULT_LABEL_COMPARATOR;
    }

    /** Retorna una còpia de les opcions actuals. */
    public static <T, C> List<MenuOption<T, C>> currentOptions(DynamicMenu<T, C> menu) {
        return new ArrayList<>(menu.getCurrentOptionSnapshot());
    }

    /** Valida que l'índex existeixi. */
    public static void validateExistingIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Índex fora de rang: " + index + ", mida actual: " + size);
        }
    }

    /** Valida un rang respecte de la mida indicada. */
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

        if (to - 1 > size && to != Integer.MAX_VALUE)
            throw new IndexOutOfBoundsException("El final del rang està fora de la mida actual: " + to + " > " + size);
    }

    /** Reconstrueix el snapshot amb les opcions indicades. */
    public static <T, C> void rebuildSnapshot(MenuSnapshot<T, C> snapshot, List<MenuOption<T, C>> options) {
        snapshot.clearOptions();
        for (MenuOption<T, C> option : options) {
            snapshot.addOption(option.label(), option.action());
        }
    }

    /** Reemplaça un rang d'opcions dins del snapshot. */
    public static <T, C> void rebuildSnapshotRange(
            MenuSnapshot<T, C> snapshot,
            List<MenuOption<T, C>> options,
            int fromInclusive,
            int toExclusive) {

        Objects.requireNonNull(snapshot, "El snapshot no pot ser nul");
        Objects.requireNonNull(options, "La llista d'opcions no pot ser nul·la");

        if (fromInclusive < 0 || toExclusive < fromInclusive || toExclusive > options.size()) {
            throw new IndexOutOfBoundsException(
                    "Rang invàlid: [" + fromInclusive + ", " + toExclusive + ")");
        }

        snapshot.replaceOptions(fromInclusive, options.subList(fromInclusive, toExclusive));
    }

    /** Crea una nova opció de menú. */
    public static <T, C> MenuOption<T, C> newOption(String label, MenuRuntimeAction<T, C> action) {
        Objects.requireNonNull(label, "El label no pot ser nul");
        Objects.requireNonNull(action, "El comportament no pot ser nul");
        return new MenuOption<>(label, action);
    }

    /** Adapta una acció al format d'execució del menú. */
    public static <T, C> MenuRuntimeAction<T, C> runtimeOf(MenuAction<T, C> action) {
        Objects.requireNonNull(action, "El comportament no pot ser nul");
        return (context, menu) -> action.execute(context);
    }

    /** Adapta una acció al format d'execució del menú. */
    public static <T, C> MenuRuntimeAction<T, C> runtimeOf(SimpleMenuAction<T> action) {
        Objects.requireNonNull(action, "El comportament no pot ser nul");
        return (context, menu) -> action.execute();
    }

}
