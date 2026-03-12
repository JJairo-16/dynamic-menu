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

public final class QueryFamily {
    private QueryFamily() {
    }

    // -------------------------------------------------------------------------
    // Index queries
    // -------------------------------------------------------------------------

    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return indexOfFirst(menu, selector, Range.all());
    }

    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        List<MenuOption<T, C>> options = currentOptions(menu);
        validateRange(range, options.size());

        Range effectiveRange = range.clamp(options.size());
        for (int i = effectiveRange.fromInclusive(); i < effectiveRange.toExclusive(); i++) {
            if (selector.test(i, options.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return indexOfLast(menu, selector, Range.all());
    }

    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        List<MenuOption<T, C>> options = currentOptions(menu);
        validateRange(range, options.size());

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

    public static <T, C> boolean containsMatch(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return indexOfFirst(menu, selector) >= 0;
    }

    public static <T, C> boolean containsLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return indexOfFirstLabel(menu, label) >= 0;
    }

    // -------------------------------------------------------------------------
    // Count queries
    // -------------------------------------------------------------------------

    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return countMatches(menu, selector, Range.all());
    }

    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        List<MenuOption<T, C>> options = currentOptions(menu);
        validateRange(range, options.size());

        int count = 0;
        Range effectiveRange = range.clamp(options.size());
        for (int i = effectiveRange.fromInclusive(); i < effectiveRange.toExclusive(); i++) {
            if (selector.test(i, options.get(i))) {
                count++;
            }
        }

        return count;
    }

    public static <T, C> int countLabelMatches(
            DynamicMenu<T, C> menu,
            String label) {

        return countMatches(menu, exactLabelSelector(label));
    }

    // -------------------------------------------------------------------------
    // Bulk query results
    // -------------------------------------------------------------------------

    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return indexesOf(menu, selector, Range.all());
    }

    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        List<MenuOption<T, C>> options = currentOptions(menu);
        validateRange(range, options.size());

        List<Integer> matches = new ArrayList<>();
        Range effectiveRange = range.clamp(options.size());
        for (int i = effectiveRange.fromInclusive(); i < effectiveRange.toExclusive(); i++) {
            if (selector.test(i, options.get(i))) {
                matches.add(i);
            }
        }

        return List.copyOf(matches);
    }

    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return matchingOptions(menu, selector, Range.all());
    }

    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        List<MenuOption<T, C>> options = currentOptions(menu);
        validateRange(range, options.size());

        List<MenuOption<T, C>> matches = new ArrayList<>();
        Range effectiveRange = range.clamp(options.size());
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

    public static <T, C> int indexOfFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return indexOfFirst(menu, exactLabelSelector(label));
    }

    public static <T, C> int indexOfLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return indexOfLast(menu, exactLabelSelector(label));
    }

    // -------------------------------------------------------------------------
    // Find queries
    // -------------------------------------------------------------------------

    public static <T, C> MenuOption<T, C> findFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");

        List<MenuOption<T, C>> options = currentOptions(menu);
        for (int i = 0; i < options.size(); i++) {
            MenuOption<T, C> option = options.get(i);
            if (selector.test(i, option)) {
                return option;
            }
        }

        return null;
    }

    public static <T, C> MenuOption<T, C> findLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");

        List<MenuOption<T, C>> options = currentOptions(menu);
        for (int i = options.size() - 1; i >= 0; i--) {
            MenuOption<T, C> option = options.get(i);
            if (selector.test(i, option)) {
                return option;
            }
        }

        return null;
    }

    public static <T, C> MenuOption<T, C> findFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return findFirst(menu, exactLabelSelector(label));
    }

    public static <T, C> MenuOption<T, C> findLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return findLast(menu, exactLabelSelector(label));
    }

    public static <T, C> Set<Integer> collectMatchingIndexes(
            List<MenuOption<T, C>> options,
            OptionSelector<T, C> selector,
            Range range,
            int limit,
            boolean reverse) {

        if (limit < 0)
            throw new IllegalArgumentException("El límit no pot ser negatiu");

        if (limit == 0)
            return new LinkedHashSet<>();

        int capacity = Math.min(limit, range.toExclusive() - range.fromInclusive());
        Set<Integer> matches = LinkedHashSet.newLinkedHashSet(capacity);

        int from = range.fromInclusive();
        int to = range.toExclusive();

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
}