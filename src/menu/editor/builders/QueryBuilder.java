package menu.editor.builders;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import menu.DynamicMenu;
import menu.editor.Range;
import menu.editor.core.QueryFamily;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;

/**
 * Builder fluent per a operacions de consulta.
 *
 * <p>Aquesta API actua com a façana pública sobre la família interna
 * {@link QueryFamily}, de manera que els casos simples es poden resoldre
 * amb wrappers estàtics i els casos avançats amb una construcció fluïda.
 *
 * <p>Les crides {@code thenX()} no executen cap canvi immediatament.
 * Cada builder acumula la seva operació pendent i només l'última crida
 * terminal executa tota la cadena en ordre.
 *
 * @param <T> tipus de retorn del menú
 * @param <C> tipus del context del menú
 */
public final class QueryBuilder<T, C> extends AbstractChainableMenuBuilder<T, C> {
    private OptionSelector<T, C> selector;
    private Range range = Range.all();

    public QueryBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    QueryBuilder(
            DynamicMenu<T, C> menu,
            List<Consumer<DynamicMenu<T, C>>> pendingOperations) {

        super(menu, pendingOperations);
    }

    /**
     * Defineix la condició de selecció.
     *
     * @param selector condició a aplicar
     * @return aquest builder
     */
    public QueryBuilder<T, C> where(OptionSelector<T, C> selector) {
        this.selector = Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        return this;
    }

    /**
     * Defineix que s'han de considerar totes les opcions.
     *
     * @return aquest builder
     */
    public QueryBuilder<T, C> whereAny() {
        this.selector = (index, option) -> true;
        return this;
    }

    /**
     * Defineix el rang d'actuació.
     *
     * @param range rang a aplicar
     * @return aquest builder
     */
    public QueryBuilder<T, C> range(Range range) {
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
    public QueryBuilder<T, C> range(int fromInclusive, int toExclusive) {
        return range(Range.of(fromInclusive, toExclusive));
    }

    private Consumer<DynamicMenu<T, C>> currentOperation() {
        OptionSelector<T, C> currentSelector = Objects.requireNonNull(
                selector,
                "La condició no pot ser nul·la");
        Range currentRange = Objects.requireNonNull(range, "El rang no pot ser nul");

        return currentMenu -> QueryFamily.matchingOptions(
                currentMenu,
                currentSelector,
                currentRange);
    }

    /**
     * Executa tota la cadena pendent i indica si existeix almenys una coincidència.
     *
     * @return {@code true} si existeix almenys una coincidència
     */
    public boolean exists() {
        applyPendingOperations();
        return QueryFamily.containsMatch(
                menu(),
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                Objects.requireNonNull(range, "El rang no pot ser nul"));
    }

    /**
     * Executa tota la cadena pendent i retorna el nombre de coincidències.
     *
     * @return nombre d'opcions coincidents
     */
    public int count() {
        applyPendingOperations();
        return QueryFamily.countMatches(
                menu(),
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                Objects.requireNonNull(range, "El rang no pot ser nul"));
    }

    /**
     * Executa tota la cadena pendent i retorna l'índex de la primera coincidència.
     *
     * @return índex de la primera coincidència, o {@code -1} si no n'hi ha cap
     */
    public int firstIndex() {
        applyPendingOperations();
        return QueryFamily.indexOfFirst(
                menu(),
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                Objects.requireNonNull(range, "El rang no pot ser nul"));
    }

    /**
     * Executa tota la cadena pendent i retorna l'índex de l'última coincidència.
     *
     * @return índex de l'última coincidència, o {@code -1} si no n'hi ha cap
     */
    public int lastIndex() {
        applyPendingOperations();
        return QueryFamily.indexOfLast(
                menu(),
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                Objects.requireNonNull(range, "El rang no pot ser nul"));
    }

    /**
     * Executa tota la cadena pendent i retorna totes les coincidències.
     *
     * @return llista d'opcions coincidents
     */
    public List<MenuOption<T, C>> options() {
        applyPendingOperations();
        return QueryFamily.matchingOptions(
                menu(),
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                Objects.requireNonNull(range, "El rang no pot ser nul"));
    }

    /**
     * Executa tota la cadena pendent i retorna tots els índexs coincidents.
     *
     * @return llista d'índexs coincidents
     */
    public List<Integer> indexes() {
        applyPendingOperations();
        return QueryFamily.indexesOf(
                menu(),
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                Objects.requireNonNull(range, "El rang no pot ser nul"));
    }

    /**
     * Executa tota la cadena pendent i retorna la primera opció coincident.
     *
     * @return primera opció coincident, o {@code null} si no n'hi ha cap
     */
    public MenuOption<T, C> first() {
        applyPendingOperations();
        return QueryFamily.findFirst(
                menu(),
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                Objects.requireNonNull(range, "El rang no pot ser nul"));
    }

    /**
     * Executa tota la cadena pendent i retorna l'última opció coincident.
     *
     * @return última opció coincident, o {@code null} si no n'hi ha cap
     */
    public MenuOption<T, C> last() {
        applyPendingOperations();
        return QueryFamily.findLast(
                menu(),
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                Objects.requireNonNull(range, "El rang no pot ser nul"));
    }

    /**
     * Continua amb una nova consulta sobre el mateix menú sense executar encara
     * la cadena.
     *
     * @return builder de consulta encadenat
     */
    public QueryBuilder<T, C> thenQuery() {
        return new QueryBuilder<>(menu(), pendingPlus(currentOperation()));
    }

    /**
     * Continua amb una eliminació sobre el mateix menú sense executar encara
     * la cadena.
     *
     * @return builder d'eliminació encadenat
     */
    public RemoveBuilder<T, C> thenRemove() {
        return new RemoveBuilder<>(menu(), pendingPlus(currentOperation()));
    }

    /**
     * Continua amb una substitució sobre el mateix menú sense executar encara
     * la cadena.
     *
     * @return builder de substitució encadenat
     */
    public ReplaceBuilder<T, C> thenReplace() {
        return new ReplaceBuilder<>(menu(), pendingPlus(currentOperation()));
    }

    /**
     * Continua amb una ordenació sobre el mateix menú sense executar encara
     * la cadena.
     *
     * @return builder d'ordenació encadenat
     */
    public SortBuilder<T, C> thenSort() {
        return new SortBuilder<>(menu(), pendingPlus(currentOperation()));
    }
}