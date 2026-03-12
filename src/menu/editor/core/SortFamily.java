package menu.editor.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import menu.DynamicMenu;
import menu.editor.Range;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;
import menu.snapshot.MenuSnapshot;

import static menu.editor.core.MenuEditorSupport.*;

public final class SortFamily {
    private SortFamily() {
    }

    // -------------------------------------------------------------------------
    // Sorting
    // -------------------------------------------------------------------------

    public static <T, C> DynamicMenu<T, C> sortByLabel(DynamicMenu<T, C> menu) {
        return sortByLabelInternal(
                menu,
                defaultLabelComparator(),
                alwaysFalseSelector(),
                alwaysFalseSelector(),
                Range.all());
    }

    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator) {

        return sortByLabelInternal(
                menu,
                comparator,
                alwaysFalseSelector(),
                alwaysFalseSelector(),
                Range.all());
    }

    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Range range) {

        return sortByLabelInternal(
                menu,
                defaultLabelComparator(),
                alwaysFalseSelector(),
                alwaysFalseSelector(),
                range);
    }

    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Range range) {

        return sortByLabelInternal(
                menu,
                comparator,
                alwaysFalseSelector(),
                alwaysFalseSelector(),
                range);
    }

    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        return sortByLabelInternal(
                menu,
                defaultLabelComparator(),
                firstSelector,
                lastSelector,
                Range.all());
    }

    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        return sortByLabelInternal(
                menu,
                defaultLabelComparator(),
                firstSelector,
                lastSelector,
                range);
    }

    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        return sortByLabelInternal(
                menu,
                comparator,
                firstSelector,
                lastSelector,
                Range.all());
    }

    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        return sortByLabelInternal(menu, comparator, firstSelector, lastSelector, range);
    }

    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        return sortByLabelPinnedIndexes(
                menu,
                defaultLabelComparator(),
                firstIndexes,
                lastIndexes,
                Range.all());
    }

    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes,
            Range range) {

        return sortByLabelPinnedIndexes(
                menu,
                defaultLabelComparator(),
                firstIndexes,
                lastIndexes,
                range);
    }

    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        return sortByLabelPinnedIndexes(
                menu,
                comparator,
                firstIndexes,
                lastIndexes,
                Range.all());
    }

    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes,
            Range range) {

        Set<Integer> first = firstIndexes == null ? Set.of() : new LinkedHashSet<>(firstIndexes);
        Set<Integer> last = lastIndexes == null ? Set.of() : new LinkedHashSet<>(lastIndexes);

        return sortByLabelInternal(
                menu,
                comparator,
                (index, option) -> first.contains(index),
                (index, option) -> last.contains(index),
                range);
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private static <T, C> DynamicMenu<T, C> sortByLabelInternal(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(comparator, "El comparador no pot ser nul");
        Objects.requireNonNull(firstSelector, "El selector inicial no pot ser nul");
        Objects.requireNonNull(lastSelector, "El selector final no pot ser nul");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        MenuSnapshot<T, C> snapshot = menu.createSnapshot();
        List<MenuOption<T, C>> options = new ArrayList<>(snapshot.getOptionSnapshot());

        validateRange(range, options.size());

        Range effectiveRange = range.clamp(options.size());
        int from = effectiveRange.fromInclusive();
        int to = effectiveRange.toExclusive();
        int segmentSize = to - from;

        if (segmentSize < 2) {
            return menu;
        }

        List<MenuOption<T, C>> first = new ArrayList<>(segmentSize);
        List<MenuOption<T, C>> middle = new ArrayList<>(segmentSize);
        List<MenuOption<T, C>> last = new ArrayList<>(segmentSize);

        for (int index = from; index < to; index++) {
            MenuOption<T, C> option = options.get(index);

            boolean goesFirst = firstSelector.test(index, option);
            boolean goesLast = lastSelector.test(index, option);

            if (goesFirst && goesLast) {
                throw new IllegalArgumentException(
                        "Una mateixa opció no pot anar al principi i al final alhora. Índex: " + index);
            }

            if (goesFirst) {
                first.add(option);
            } else if (goesLast) {
                last.add(option);
            } else {
                middle.add(option);
            }
        }

        middle.sort(comparator);

        int writeIndex = from;
        for (MenuOption<T, C> option : first) {
            options.set(writeIndex++, option);
        }
        for (MenuOption<T, C> option : middle) {
            options.set(writeIndex++, option);
        }
        for (MenuOption<T, C> option : last) {
            options.set(writeIndex++, option);
        }

        rebuildSnapshot(snapshot, options);
        return menu.restoreSnapshot(snapshot);
    }
}