package menu.editor.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import menu.DynamicMenu;
import menu.editor.Range;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;

import static menu.editor.core.MenuEditorSupport.*;

/** Utilitats de QueryFamily. */
public final class QueryFamily {
    private QueryFamily() {
        throw new AssertionError("No es pot instanciar QueryFamily");
    }

    // -------------------------------------------------------------------------
    // Index queries
    // -------------------------------------------------------------------------

    /** Retorna l'índex de la primera coincidència. */
    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return indexOfFirst(menu, selector, Range.all());
    }

    /** Retorna l'índex de la primera coincidència. */
    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        List<MenuOption<T, C>> options = validatedOptions(menu, selector, range);

        Range effectiveRange = range.clamp(options.size());
        for (int i = effectiveRange.fromInclusive(); i < effectiveRange.toExclusive(); i++) {
            if (selector.test(i, options.get(i))) {
                return i;
            }
        }

        return -1;
    }

    /** Retorna l'índex de l'última coincidència. */
    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return indexOfLast(menu, selector, Range.all());
    }

    /** Retorna l'índex de l'última coincidència. */
    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        List<MenuOption<T, C>> options = validatedOptions(menu, selector, range);

        Range effectiveRange = range.clamp(options.size());
        for (int i = effectiveRange.toExclusive() - 1; i >= effectiveRange.fromInclusive(); i--) {
            if (selector.test(i, options.get(i))) {
                return i;
            }
        }

        return -1;
    }

    // -------------------------------------------------------------------------
    // Presence queries
    // -------------------------------------------------------------------------

    /** Indica si existeix alguna coincidència. */
    public static <T, C> boolean containsMatch(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return containsMatch(menu, selector, Range.all());
    }

    /** Indica si existeix alguna coincidència. */
    public static <T, C> boolean containsMatch(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return indexOfFirst(menu, selector, range) >= 0;
    }

    /** Indica si existeix una opció amb l'etiqueta indicada. */
    public static <T, C> boolean containsLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return indexOfFirstLabel(menu, label) >= 0;
    }

    // -------------------------------------------------------------------------
    // Count queries
    // -------------------------------------------------------------------------

    /** Compta les opcions que compleixen la condició. */
    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return countMatches(menu, selector, Range.all());
    }

    /** Compta les opcions que compleixen la condició. */
    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        List<MenuOption<T, C>> options = validatedOptions(menu, selector, range);

        Range effectiveRange = range.clamp(options.size());
        int count = 0;

        for (int i = effectiveRange.fromInclusive(); i < effectiveRange.toExclusive(); i++) {
            if (selector.test(i, options.get(i))) {
                count++;
            }
        }

        return count;
    }

    /** Compta les opcions amb l'etiqueta indicada. */
    public static <T, C> int countLabelMatches(
            DynamicMenu<T, C> menu,
            String label) {

        return countMatches(menu, exactLabelSelector(label));
    }

    // -------------------------------------------------------------------------
    // Bulk query results
    // -------------------------------------------------------------------------

    /** Retorna els índexs de totes les coincidències. */
    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return indexesOf(menu, selector, Range.all());
    }

    /** Retorna els índexs de totes les coincidències. */
    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        List<MenuOption<T, C>> options = validatedOptions(menu, selector, range);

        List<Integer> matches = new ArrayList<>();
        Range effectiveRange = range.clamp(options.size());
        for (int i = effectiveRange.fromInclusive(); i < effectiveRange.toExclusive(); i++) {
            if (selector.test(i, options.get(i))) {
                matches.add(i);
            }
        }

        return List.copyOf(matches);
    }

    /** Retorna les opcions que compleixen la condició. */
    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return matchingOptions(menu, selector, Range.all());
    }

    /** Retorna les opcions que compleixen la condició. */
    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        List<MenuOption<T, C>> options = validatedOptions(menu, selector, range);

        Range effectiveRange = range.clamp(options.size());
        List<MenuOption<T, C>> matches = new ArrayList<>();

        for (int i = effectiveRange.fromInclusive(); i < effectiveRange.toExclusive(); i++) {
            MenuOption<T, C> option = options.get(i);
            if (selector.test(i, option)) {
                matches.add(option);
            }
        }

        return List.copyOf(matches);
    }

    // -------------------------------------------------------------------------
    // Exact label queries
    // -------------------------------------------------------------------------

    /** Retorna l'índex de la primera opció amb l'etiqueta indicada. */
    public static <T, C> int indexOfFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return indexOfFirst(menu, exactLabelSelector(label));
    }

    /** Retorna l'índex de l'última opció amb l'etiqueta indicada. */
    public static <T, C> int indexOfLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return indexOfLast(menu, exactLabelSelector(label));
    }

    // -------------------------------------------------------------------------
    // Find queries
    // -------------------------------------------------------------------------

    /** Retorna la primera opció que compleix la condició. */
    public static <T, C> MenuOption<T, C> findFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return findFirst(menu, selector, Range.all());
    }

    /** Retorna la primera opció que compleix la condició. */
    public static <T, C> MenuOption<T, C> findFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        int index = indexOfFirst(menu, selector, range);
        return index >= 0 ? currentOptions(menu).get(index) : null;
    }

    /** Retorna l'última opció que compleix la condició. */
    public static <T, C> MenuOption<T, C> findLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return findLast(menu, selector, Range.all());
    }

    /** Retorna l'última opció que compleix la condició. */
    public static <T, C> MenuOption<T, C> findLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        int index = indexOfLast(menu, selector, range);
        return index >= 0 ? currentOptions(menu).get(index) : null;
    }

    /** Retorna la primera opció amb l'etiqueta indicada. */
    public static <T, C> MenuOption<T, C> findFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return findFirst(menu, exactLabelSelector(label));
    }

    /** Retorna l'última opció amb l'etiqueta indicada. */
    public static <T, C> MenuOption<T, C> findLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return findLast(menu, exactLabelSelector(label));
    }

    /** Recull els índexs coincidents dins d'un rang. */
    public static <T, C> Set<Integer> collectMatchingIndexes(
            List<MenuOption<T, C>> options,
            OptionSelector<T, C> selector,
            Range range,
            int limit,
            boolean reverse) {

        Objects.requireNonNull(options, "La llista d'opcions no pot ser nul·la");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        if (limit < 0) {
            throw new IllegalArgumentException("El límit no pot ser negatiu");
        }

        if (limit == 0) {
            return new LinkedHashSet<>();
        }

        validateRange(range, options.size());
        Range effectiveRange = range.clamp(options.size());

        int capacity = Math.min(limit, effectiveRange.toExclusive() - effectiveRange.fromInclusive());
        Set<Integer> matches = LinkedHashSet.newLinkedHashSet(capacity);

        int from = effectiveRange.fromInclusive();
        int to = effectiveRange.toExclusive();

        if (!reverse) {
            for (int i = from; i < to; i++) {
                if (selector.test(i, options.get(i))) {
                    matches.add(i);
                    if (matches.size() >= limit) {
                        break;
                    }
                }
            }
            return matches;
        }

        for (int i = to - 1; i >= from; i--) {
            if (selector.test(i, options.get(i))) {
                matches.add(i);
                if (matches.size() >= limit) {
                    break;
                }
            }
        }

        return matches;
    }

    /** Valida els arguments i obté les opcions actuals. */
    private static <T, C> List<MenuOption<T, C>> validatedOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        List<MenuOption<T, C>> options = currentOptions(menu);
        validateRange(range, options.size());
        return options;
    }
}
