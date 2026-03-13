package menu.editor.builders;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import menu.DynamicMenu;
import menu.editor.Range;
import menu.editor.core.MenuEditorSupport;
import menu.editor.core.SortFamily;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;

/**
 * Builder fluent per a operacions d'ordenació.
 *
 * <p>Aquesta API actua com a façana pública sobre la família interna
 * {@link SortFamily}, de manera que els casos simples es poden resoldre
 * amb wrappers estàtics i els casos avançats amb una construcció fluïda.
 *
 * <p>Permet definir el comparador, el rang, opcions fixades al principi
 * o al final, i també encadenar l'operació amb altres builders públics.
 *
 * <p>Les crides {@code thenX()} no executen cap canvi immediatament.
 * Cada builder acumula la seva operació pendent i només l'última crida
 * terminal executa tota la cadena en ordre.
 *
 * @param <T> tipus de retorn del menú
 * @param <C> tipus del context del menú
 */
public final class SortBuilder<T, C> extends AbstractChainableMenuBuilder<T, C> {
    private Comparator<MenuOption<T, C>> comparator = MenuEditorSupport.defaultLabelComparator();
    private Range range = Range.all();
    private OptionSelector<T, C> firstSelector = MenuEditorSupport.alwaysFalseSelector();
    private OptionSelector<T, C> lastSelector = MenuEditorSupport.alwaysFalseSelector();
    private boolean descending = false;

    public SortBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    SortBuilder(
            DynamicMenu<T, C> menu,
            List<Consumer<DynamicMenu<T, C>>> pendingOperations) {

        super(menu, pendingOperations);
    }

    /**
     * Defineix l'ordenació per etiqueta utilitzant el comparador per defecte.
     *
     * @return aquest builder
     */
    public SortBuilder<T, C> byLabel() {
        this.comparator = MenuEditorSupport.defaultLabelComparator();
        return this;
    }

    /**
     * Defineix el comparador a utilitzar per a l'ordenació.
     *
     * @param comparator comparador base
     * @return aquest builder
     */
    public SortBuilder<T, C> comparator(Comparator<MenuOption<T, C>> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "El comparador no pot ser nul");
        return this;
    }

    /**
     * Defineix el rang d'actuació.
     *
     * @param range rang a aplicar
     * @return aquest builder
     */
    public SortBuilder<T, C> range(Range range) {
        this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
        return this;
    }

    /**
     * Defineix el rang d'actuació.
     *
     * @param fromInclusive inici inclòs
     * @param toExclusive final exclòs
     * @return aquest builder
     */
    public SortBuilder<T, C> range(int fromInclusive, int toExclusive) {
        return range(Range.of(fromInclusive, toExclusive));
    }

    /**
     * Fixa les opcions seleccionades al principi del segment ordenat.
     *
     * @param selector selector de les opcions a fixar al principi
     * @return aquest builder
     */
    public SortBuilder<T, C> pinFirst(OptionSelector<T, C> selector) {
        this.firstSelector = Objects.requireNonNull(selector, "El selector inicial no pot ser nul");
        return this;
    }

    /**
     * Fixa les opcions seleccionades al final del segment ordenat.
     *
     * @param selector selector de les opcions a fixar al final
     * @return aquest builder
     */
    public SortBuilder<T, C> pinLast(OptionSelector<T, C> selector) {
        this.lastSelector = Objects.requireNonNull(selector, "El selector final no pot ser nul");
        return this;
    }

    /**
     * Defineix simultàniament els selectors d'opcions fixades al principi i al final.
     *
     * @param firstSelector selector de les opcions a fixar al principi
     * @param lastSelector selector de les opcions a fixar al final
     * @return aquest builder
     */
    public SortBuilder<T, C> pinned(
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        this.firstSelector = Objects.requireNonNull(firstSelector, "El selector inicial no pot ser nul");
        this.lastSelector = Objects.requireNonNull(lastSelector, "El selector final no pot ser nul");
        return this;
    }

    /**
     * Defineix opcions fixades al principi i al final a partir dels seus índexs.
     *
     * <p>Els índexs es valoren respecte del menú original, igual que a
     * {@link SortFamily#sortByLabelPinnedIndexes(menu.DynamicMenu, Collection, Collection)}.
     *
     * @param firstIndexes índexs que s'han de mantenir al principi
     * @param lastIndexes índexs que s'han de mantenir al final
     * @return aquest builder
     */
    public SortBuilder<T, C> pinnedIndexes(
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        Set<Integer> first = firstIndexes == null ? Set.of() : new LinkedHashSet<>(firstIndexes);
        Set<Integer> last = lastIndexes == null ? Set.of() : new LinkedHashSet<>(lastIndexes);

        this.firstSelector = (index, option) -> first.contains(index);
        this.lastSelector = (index, option) -> last.contains(index);
        return this;
    }

    /**
     * Indica que l'ordenació s'ha de fer en sentit ascendent.
     *
     * @return aquest builder
     */
    public SortBuilder<T, C> ascending() {
        this.descending = false;
        return this;
    }

    /**
     * Indica que l'ordenació s'ha de fer en sentit descendent.
     *
     * <p>Aquesta és una petita extensió funcional respecte de la família estàtica,
     * ja que permet invertir qualsevol comparador de manera fluïda.
     *
     * @return aquest builder
     */
    public SortBuilder<T, C> descending() {
        this.descending = true;
        return this;
    }

    /**
     * Defineix explícitament si l'ordenació s'ha de fer en sentit descendent.
     *
     * @param descending valor nou
     * @return aquest builder
     */
    public SortBuilder<T, C> descending(boolean descending) {
        this.descending = descending;
        return this;
    }

    private Comparator<MenuOption<T, C>> effectiveComparator() {
        Comparator<MenuOption<T, C>> currentComparator = Objects.requireNonNull(
                comparator,
                "El comparador no pot ser nul");
        return descending ? currentComparator.reversed() : currentComparator;
    }

    private Consumer<DynamicMenu<T, C>> currentOperation() {
        Comparator<MenuOption<T, C>> currentComparator = effectiveComparator();
        OptionSelector<T, C> currentFirstSelector = Objects.requireNonNull(
                firstSelector,
                "El selector inicial no pot ser nul");
        OptionSelector<T, C> currentLastSelector = Objects.requireNonNull(
                lastSelector,
                "El selector final no pot ser nul");
        Range currentRange = Objects.requireNonNull(range, "El rang no pot ser nul");

        return currentMenu -> SortFamily.sortByLabel(
                currentMenu,
                currentComparator,
                currentFirstSelector,
                currentLastSelector,
                currentRange);
    }

    /**
     * Executa tota la cadena pendent i aplica l'ordenació final d'aquest builder.
     *
     * @return el mateix menú, ja modificat
     */
    public DynamicMenu<T, C> execute() {
        applyPendingOperations();
        return SortFamily.sortByLabel(
                menu(),
                effectiveComparator(),
                Objects.requireNonNull(firstSelector, "El selector inicial no pot ser nul"),
                Objects.requireNonNull(lastSelector, "El selector final no pot ser nul"),
                Objects.requireNonNull(range, "El rang no pot ser nul"));
    }

    /**
     * Alias semàntic de {@link #execute()}.
     *
     * @return el mateix menú, ja modificat
     */
    public DynamicMenu<T, C> apply() {
        return execute();
    }

    /**
     * Continua amb una nova ordenació sense executar encara la cadena.
     *
     * @return builder d'ordenació encadenat
     */
    public SortBuilder<T, C> thenSort() {
        return new SortBuilder<>(menu(), pendingPlus(currentOperation()));
    }

    /**
     * Continua amb una eliminació sense executar encara la cadena.
     *
     * @return builder d'eliminació encadenat
     */
    public RemoveBuilder<T, C> thenRemove() {
        return new RemoveBuilder<>(menu(), pendingPlus(currentOperation()));
    }

    /**
     * Continua amb una substitució sense executar encara la cadena.
     *
     * @return builder de substitució encadenat
     */
    public ReplaceBuilder<T, C> thenReplace() {
        return new ReplaceBuilder<>(menu(), pendingPlus(currentOperation()));
    }
}
