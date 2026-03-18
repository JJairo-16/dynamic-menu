package menu.editor.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import menu.DynamicMenu;
import menu.editor.MenuEditor;
import menu.editor.Range;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;
import menu.snapshot.MenuSnapshot;

import static menu.editor.core.MenuEditorSupport.rebuildSnapshot;
import static menu.editor.core.MenuEditorSupport.rebuildSnapshotRange;
import static menu.editor.core.MenuEditorSupport.validateRange;

/** Família d'operacions estàtiques de barreja. */
public final class ShuffleFamily {

    /** Evita la instanciació. */
    private ShuffleFamily() {
        throw new AssertionError("No es pot instanciar ShuffleFamily");
    }

    /**
     * Barreja totes les opcions amb un {@link Random} nou.
     *
     * @param menu menú objectiu
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffle(DynamicMenu<T, C> menu) {
        return shuffleInternalWithoutPin(menu, new Random(), Range.all());
    }

    /**
     * Barreja totes les opcions amb el {@link Random} indicat.
     *
     * @param menu menú objectiu
     * @param random random a usar
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffle(
            DynamicMenu<T, C> menu,
            Random random) {

        return shuffleInternalWithoutPin(menu, random, Range.all());
    }

    /**
     * Barreja un rang amb un {@link Random} nou.
     *
     * @param menu menú objectiu
     * @param range rang a barrejar
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffle(
            DynamicMenu<T, C> menu,
            Range range) {

        return shuffleInternalWithoutPin(menu, new Random(), range);
    }

    /**
     * Barreja un rang amb el {@link Random} indicat.
     *
     * @param menu menú objectiu
     * @param random random a usar
     * @param range rang a barrejar
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffle(
            DynamicMenu<T, C> menu,
            Random random,
            Range range) {

        return shuffleInternalWithoutPin(menu, random, range);
    }

    /**
     * Barreja totes les opcions fixant-ne algunes al principi o al final.
     *
     * @param menu menú objectiu
     * @param firstSelector selector d'opcions inicials
     * @param lastSelector selector d'opcions finals
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffle(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        return shuffleInternal(
                menu,
                new Random(),
                firstSelector,
                lastSelector,
                Range.all());
    }

    /**
     * Barreja un rang fixant-ne opcions al principi o al final.
     *
     * @param menu menú objectiu
     * @param firstSelector selector d'opcions inicials
     * @param lastSelector selector d'opcions finals
     * @param range rang a barrejar
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffle(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        return shuffleInternal(
                menu,
                new Random(),
                firstSelector,
                lastSelector,
                range);
    }

    /**
     * Barreja totes les opcions fixant-ne algunes al principi o al final.
     *
     * @param menu menú objectiu
     * @param random random a usar
     * @param firstSelector selector d'opcions inicials
     * @param lastSelector selector d'opcions finals
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffle(
            DynamicMenu<T, C> menu,
            Random random,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        return shuffle(
                menu,
                random,
                firstSelector,
                lastSelector,
                Range.all());
    }

    /**
     * Barreja un rang fixant-ne opcions al principi o al final.
     *
     * @param menu menú objectiu
     * @param random random a usar
     * @param firstSelector selector d'opcions inicials
     * @param lastSelector selector d'opcions finals
     * @param range rang a barrejar
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffle(
            DynamicMenu<T, C> menu,
            Random random,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        OptionSelector<T, C> falseSelector = MenuEditor.alwaysFalseSelector();

        if (firstSelector == falseSelector && lastSelector == falseSelector) {
            return shuffleInternalWithoutPin(menu, random, range);
        }

        return shuffleInternal(menu, random, firstSelector, lastSelector, range);
    }

    /**
     * Barreja totes les opcions fixant índexs al principi o al final.
     *
     * @param menu menú objectiu
     * @param firstIndexes índexs inicials
     * @param lastIndexes índexs finals
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shufflePinnedIndexes(
            DynamicMenu<T, C> menu,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        return shufflePinnedIndexes(
                menu,
                new Random(),
                firstIndexes,
                lastIndexes,
                Range.all());
    }

    /**
     * Barreja un rang fixant índexs al principi o al final.
     *
     * @param menu menú objectiu
     * @param firstIndexes índexs inicials
     * @param lastIndexes índexs finals
     * @param range rang a barrejar
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shufflePinnedIndexes(
            DynamicMenu<T, C> menu,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes,
            Range range) {

        return shufflePinnedIndexes(
                menu,
                new Random(),
                firstIndexes,
                lastIndexes,
                range);
    }

    /**
     * Barreja totes les opcions fixant índexs al principi o al final.
     *
     * @param menu menú objectiu
     * @param random random a usar
     * @param firstIndexes índexs inicials
     * @param lastIndexes índexs finals
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shufflePinnedIndexes(
            DynamicMenu<T, C> menu,
            Random random,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        return shufflePinnedIndexes(
                menu,
                random,
                firstIndexes,
                lastIndexes,
                Range.all());
    }

    /**
     * Barreja un rang fixant índexs al principi o al final.
     *
     * @param menu menú objectiu
     * @param random random a usar
     * @param firstIndexes índexs inicials
     * @param lastIndexes índexs finals
     * @param range rang a barrejar
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shufflePinnedIndexes(
            DynamicMenu<T, C> menu,
            Random random,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes,
            Range range) {

        Set<Integer> first = firstIndexes == null ? Set.of() : new HashSet<>(firstIndexes);
        Set<Integer> last = lastIndexes == null ? Set.of() : new HashSet<>(lastIndexes);

        return shuffleInternal(
                menu,
                random,
                (index, option) -> first.contains(index),
                (index, option) -> last.contains(index),
                range);
    }

    /**
     * Barreja un rang sense opcions fixades.
     *
     * @param menu menú objectiu
     * @param random random a usar
     * @param range rang a barrejar
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    private static <T, C> DynamicMenu<T, C> shuffleInternalWithoutPin(
            DynamicMenu<T, C> menu,
            Random random,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(random, "El random no pot ser nul");
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

        Collections.shuffle(options.subList(from, to), random);

        rebuildSnapshotRange(snapshot, options, from, to);
        return menu.restoreSnapshot(snapshot);
    }

    /**
     * Barreja un rang mantenint opcions al principi o al final.
     *
     * @param menu menú objectiu
     * @param random random a usar
     * @param firstSelector selector d'opcions inicials
     * @param lastSelector selector d'opcions finals
     * @param range rang a barrejar
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return menú modificat
     */
    private static <T, C> DynamicMenu<T, C> shuffleInternal(
            DynamicMenu<T, C> menu,
            Random random,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(random, "El random no pot ser nul");
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

        List<MenuOption<T, C>> first = new ArrayList<>();
        List<MenuOption<T, C>> middle = new ArrayList<>(segmentSize);
        List<MenuOption<T, C>> last = new ArrayList<>();

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

        Collections.shuffle(middle, random);

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