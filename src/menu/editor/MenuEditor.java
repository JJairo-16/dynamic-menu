package menu.editor;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import menu.DynamicMenu;
import menu.editor.helpers.*;
import menu.model.MenuOption;
import menu.editor.builders.RemoveBuilder;
import menu.editor.builders.ReplaceBuilder;
import menu.editor.core.*;

/** Utilitats avançades d'edició per a {@link DynamicMenu}. */
public final class MenuEditor {

    private MenuEditor() {
        throw new AssertionError("No es pot instanciar MenuEditor");
    }

    public static <T, C> OptionSelector<T, C> alwaysFalseSelector() {
        return MenuEditorSupport.alwaysFalseSelector();
    }

    public static <T, C> OptionSelector<T, C> alwaysTrueSelector() {
        return MenuEditorSupport.alwaysTrueSelector();
    }

    @SuppressWarnings({"SameReturnValue"})
    public static <T, C> boolean alwaysTrue(int index, MenuOption<T, C> option) {
        return true;
    }

    @SuppressWarnings({"SameReturnValue"})
    public static <T, C> boolean alwaysFalse(int index, MenuOption<T, C> option) {
        return false;
    }

    // -------------------------------------------------------------------------
    // Replace - API fluïda pública
    // -------------------------------------------------------------------------

    public static <T, C> ReplaceBuilder<T, C> replace(DynamicMenu<T, C> menu) {
        return new ReplaceBuilder<>(menu);
    }

    public static <T, C> ReplaceBuilder<T, C> replace(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return replace(menu).where(selector);
    }

    // -------------------------------------------------------------------------
    // Remove - API estàtica pública
    // -------------------------------------------------------------------------

    /**
     * Elimina totes les opcions que compleixen la condició.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return RemoveFamily.removeIf(menu, selector);
    }

    /**
     * Elimina totes les opcions que compleixen la condició dins d'un rang.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang a aplicar
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return RemoveFamily.removeIf(menu, selector, range);
    }

    /**
     * Elimina opcions que compleixen la condició dins d'un rang i amb un límit.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang a aplicar
     * @param limit    límit màxim d'eliminacions
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range,
            int limit) {

        return RemoveFamily.removeIf(menu, selector, range, limit);
    }

    /**
     * Elimina opcions segons una configuració completa.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param config   configuració d'edició
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            EditConfig config) {

        return RemoveFamily.removeIf(menu, selector, config);
    }

    /**
     * Elimina totes les opcions coincidents recorrent en sentit invers.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return RemoveFamily.removeAllIfReverse(menu, selector);
    }

    /**
     * Elimina totes les opcions coincidents dins d'un rang, recorrent en sentit
     * invers.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang a aplicar
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return RemoveFamily.removeAllIfReverse(menu, selector, range);
    }

    /**
     * Elimina opcions coincidents dins d'un rang, amb límit i recorrent en sentit
     * invers.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang a aplicar
     * @param limit    límit màxim d'eliminacions
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range,
            int limit) {

        return RemoveFamily.removeAllIfReverse(menu, selector, range, limit);
    }

    /**
     * Elimina opcions recorrent en sentit invers a partir d'una configuració
     * existent.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param config   configuració base
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            EditConfig config) {

        return RemoveFamily.removeAllIfReverse(menu, selector, config);
    }

    /**
     * Elimina la primera opció que compleix la condició.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return {@code true} si s'ha eliminat una opció
     */
    public static <T, C> boolean removeFirstIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return RemoveFamily.removeFirstIf(menu, selector);
    }

    /**
     * Elimina l'última opció que compleix la condició.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return {@code true} si s'ha eliminat una opció
     */
    public static <T, C> boolean removeLastIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return RemoveFamily.removeLastIf(menu, selector);
    }

    /**
     * Elimina la primera opció amb l'etiqueta exacta indicada.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus del context principal
     * @param <C>   tipus del context secundari
     * @return {@code true} si s'ha eliminat una opció
     */
    public static <T, C> boolean removeFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return RemoveFamily.removeFirstLabel(menu, label);
    }

    /**
     * Elimina l'última opció amb l'etiqueta exacta indicada.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus del context principal
     * @param <C>   tipus del context secundari
     * @return {@code true} si s'ha eliminat una opció
     */
    public static <T, C> boolean removeLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return RemoveFamily.removeLastLabel(menu, label);
    }

    /**
     * Elimina totes les opcions amb l'etiqueta exacta indicada.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus del context principal
     * @param <C>   tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeAllLabels(
            DynamicMenu<T, C> menu,
            String label) {

        return RemoveFamily.removeAllLabels(menu, label);
    }

    // -------------------------------------------------------------------------
    // Remove - API fluïda pública
    // -------------------------------------------------------------------------

    /**
     * Inicia una operació fluïda d'eliminació sobre el menú indicat.
     *
     * @param menu menú objectiu
     * @param <T>  tipus del context principal
     * @param <C>  tipus del context secundari
     * @return operador fluent d'eliminació
     */
    public static <T, C> RemoveBuilder<T, C> remove(DynamicMenu<T, C> menu) {
        return new RemoveBuilder<>(menu);
    }

    /**
     * Inicia una operació fluïda d'eliminació amb una condició inicial.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return operador fluent d'eliminació
     */
    public static <T, C> RemoveBuilder<T, C> remove(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return remove(menu).where(selector);
    }

    // -------------------------------------------------------------------------
    // Sorting
    // -------------------------------------------------------------------------

    /** Ordena totes les opcions per label. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(DynamicMenu<T, C> menu) {
        return SortFamily.sortByLabel(menu);
    }

    /** Ordena totes les opcions per label amb un comparador. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator) {

        return SortFamily.sortByLabel(menu, comparator);
    }

    /** Ordena les opcions per label dins d'un rang. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Range range) {

        return SortFamily.sortByLabel(menu, range);
    }

    /** Ordena les opcions per label dins d'un rang amb comparador. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Range range) {

        return SortFamily.sortByLabel(menu, comparator, range);
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        return SortFamily.sortByLabel(menu, firstSelector, lastSelector);
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        return SortFamily.sortByLabel(menu, firstSelector, lastSelector, range);
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        return SortFamily.sortByLabel(menu, comparator, firstSelector, lastSelector);
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        return SortFamily.sortByLabel(menu, comparator, firstSelector, lastSelector, range);
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        return SortFamily.sortByLabelPinnedIndexes(menu, firstIndexes, lastIndexes);
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes,
            Range range) {

        return SortFamily.sortByLabelPinnedIndexes(menu, firstIndexes, lastIndexes, range);
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        return SortFamily.sortByLabelPinnedIndexes(menu, comparator, firstIndexes, lastIndexes);
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes,
            Range range) {

        return SortFamily.sortByLabelPinnedIndexes(menu, comparator, firstIndexes, lastIndexes, range);
    }

    // -------------------------------------------------------------------------
    // Query helpers
    // -------------------------------------------------------------------------

    /** Retorna l'índex de la primera coincidència. */
    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.indexOfFirst(menu, selector);
    }

    /** Retorna l'índex de la primera coincidència dins d'un rang. */
    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.indexOfFirst(menu, selector, range);
    }

    /** Retorna l'índex de l'última coincidència. */
    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.indexOfLast(menu, selector);
    }

    /** Retorna l'índex de l'última coincidència dins d'un rang. */
    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.indexOfLast(menu, selector, range);
    }

    /** Indica si existeix alguna coincidència. */
    public static <T, C> boolean containsMatch(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.containsMatch(menu, selector);
    }

    /** Compta quantes opcions compleixen una condició. */
    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.countMatches(menu, selector);
    }

    /** Compta quantes opcions compleixen una condició dins d'un rang. */
    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.countMatches(menu, selector, range);
    }

    /** Retorna tots els índexs que compleixen una condició. */
    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.indexesOf(menu, selector);
    }

    /** Retorna tots els índexs que compleixen una condició dins d'un rang. */
    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.indexesOf(menu, selector, range);
    }

    /** Retorna totes les opcions que compleixen una condició. */
    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.matchingOptions(menu, selector);
    }

    /** Retorna totes les opcions que compleixen una condició dins d'un rang. */
    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.matchingOptions(menu, selector, range);
    }

    /** Retorna l'índex de la primera coincidència exacta. */
    public static <T, C> int indexOfFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.indexOfFirstLabel(menu, label);
    }

    /** Retorna l'índex de l'última coincidència exacta. */
    public static <T, C> int indexOfLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.indexOfLastLabel(menu, label);
    }

    /** Indica si existeix alguna coincidència exacta. */
    public static <T, C> boolean containsLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.containsLabel(menu, label);
    }

    /** Compta quantes coincidències exactes hi ha. */
    public static <T, C> int countLabelMatches(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.countLabelMatches(menu, label);
    }

    /** Retorna la primera opció que compleix una condició o {@code null}. */
    public static <T, C> MenuOption<T, C> findFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.findFirst(menu, selector);
    }

    /** Retorna l'última opció que compleix una condició o {@code null}. */
    public static <T, C> MenuOption<T, C> findLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.findLast(menu, selector);
    }

    /** Retorna la primera coincidència exacta o {@code null}. */
    public static <T, C> MenuOption<T, C> findFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.findFirstLabel(menu, label);
    }

    /** Retorna l'última coincidència exacta o {@code null}. */
    public static <T, C> MenuOption<T, C> findLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.findLastLabel(menu, label);
    }

}