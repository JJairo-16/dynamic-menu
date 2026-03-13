package menu.editor.builders;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

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
 * <p>A diferència dels builders de modificació, aquest builder no aplica
 * canvis sobre el menú real quan resol una consulta. Si la cadena conté
 * operacions pendents prèvies, aquestes s'apliquen sobre una còpia temporal
 * del menú i la consulta es resol sobre aquest estat virtual.
 *
 * <p>Quan s'encadena cap a {@link RemoveBuilder} o {@link ReplaceBuilder},
 * la condició i el rang actuals es transfereixen al builder següent perquè
 * la query no es perdi. En canvi, {@link SortBuilder} només reutilitza el
 * rang, ja que no treballa amb un selector de coincidència equivalent.
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
            List<java.util.function.Consumer<DynamicMenu<T, C>>> pendingOperations) {

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
     * Defineix una condició de selecció basada únicament en el label de l'opció.
     *
     * @param predicate condició sobre el label
     * @return aquest builder
     */
    public QueryBuilder<T, C> whereLabel(Predicate<String> predicate) {
        Objects.requireNonNull(predicate, "La condició sobre el label no pot ser nul·la");
        this.selector = (index, option) -> predicate.test(option.label());
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

    private OptionSelector<T, C> requireSelector() {
        return Objects.requireNonNull(selector, "La condició no pot ser nul·la");
    }

    private Range requireRange() {
        return Objects.requireNonNull(range, "El rang no pot ser nul");
    }

    /**
     * Crea una còpia temporal del menú actual i hi aplica totes les operacions
     * pendents de la cadena, sense tocar el menú real.
     *
     * @return menú temporal amb l'estat virtual de la cadena
     */
    private DynamicMenu<T, C> buildPreviewMenu() {
        DynamicMenu<T, C> previewMenu = menu().createChildMenu(
                menu().getTitle(),
                menu().getContext());

        applyPendingOperationsOn(previewMenu);
        return previewMenu;
    }

    /**
     * Resol la query sobre una còpia temporal del menú i retorna les opcions
     * coincidents.
     *
     * @return llista immutable d'opcions coincidents
     */
    private List<MenuOption<T, C>> resolveOptions() {
        DynamicMenu<T, C> previewMenu = buildPreviewMenu();
        return QueryFamily.matchingOptions(
                previewMenu,
                requireSelector(),
                requireRange());
    }

    /**
     * Resol la query sobre una còpia temporal del menú i transforma el resultat
     * final amb el col·lector indicat.
     *
     * @param collector transformador del resultat
     * @param <R> tipus de sortida final
     * @return resultat transformat
     */
    public <R> R collect(Function<? super List<MenuOption<T, C>>, ? extends R> collector) {
        Objects.requireNonNull(collector, "El col·lector no pot ser nul");
        return collector.apply(resolveOptions());
    }

    /**
     * Indica si existeix almenys una coincidència.
     *
     * @return {@code true} si existeix almenys una coincidència
     */
    public boolean exists() {
        return !resolveOptions().isEmpty();
    }

    /**
     * Retorna el nombre de coincidències.
     *
     * @return nombre d'opcions coincidents
     */
    public int count() {
        return resolveOptions().size();
    }

    /**
     * Retorna el primer índex coincident dins del menú virtual.
     *
     * @return índex de la primera coincidència, o {@code -1} si no n'hi ha cap
     */
    public int firstIndex() {
        DynamicMenu<T, C> previewMenu = buildPreviewMenu();
        return QueryFamily.indexOfFirst(
                previewMenu,
                requireSelector(),
                requireRange());
    }

    /**
     * Retorna l'últim índex coincident dins del menú virtual.
     *
     * @return índex de l'última coincidència, o {@code -1} si no n'hi ha cap
     */
    public int lastIndex() {
        DynamicMenu<T, C> previewMenu = buildPreviewMenu();
        return QueryFamily.indexOfLast(
                previewMenu,
                requireSelector(),
                requireRange());
    }

    /**
     * Retorna tots els índexs coincidents dins del menú virtual.
     *
     * @return llista d'índexs coincidents
     */
    public List<Integer> indexes() {
        DynamicMenu<T, C> previewMenu = buildPreviewMenu();
        return QueryFamily.indexesOf(
                previewMenu,
                requireSelector(),
                requireRange());
    }

    /**
     * Retorna totes les opcions coincidents.
     *
     * @return llista d'opcions coincidents
     */
    public List<MenuOption<T, C>> options() {
        return resolveOptions();
    }

    /**
     * Retorna la primera opció coincident.
     *
     * @return primera opció coincident, o {@code null} si no n'hi ha cap
     */
    public MenuOption<T, C> first() {
        List<MenuOption<T, C>> options = resolveOptions();
        return options.isEmpty() ? null : options.get(0);
    }

    /**
     * Retorna l'última opció coincident.
     *
     * @return última opció coincident, o {@code null} si no n'hi ha cap
     */
    public MenuOption<T, C> last() {
        List<MenuOption<T, C>> options = resolveOptions();
        return options.isEmpty() ? null : options.get(options.size() - 1);
    }

    /**
     * Continua amb una nova consulta sobre el mateix menú sense executar encara
     * cap canvi real. La condició i el rang actuals es transfereixen.
     *
     * @return builder de consulta encadenat
     */
    public QueryBuilder<T, C> thenQuery() {
        return new QueryBuilder<>(menu(), pendingOperations())
                .where(requireSelector())
                .range(requireRange());
    }

    /**
     * Continua amb una eliminació sobre el mateix menú sense executar encara
     * la cadena. La condició i el rang actuals es transfereixen al builder següent.
     *
     * @return builder d'eliminació encadenat
     */
    public RemoveBuilder<T, C> thenRemove() {
        return new RemoveBuilder<>(menu(), pendingOperations())
                .where(requireSelector())
                .range(requireRange());
    }

    /**
     * Continua amb una substitució sobre el mateix menú sense executar encara
     * la cadena. La condició i el rang actuals es transfereixen al builder següent.
     *
     * @return builder de substitució encadenat
     */
    public ReplaceBuilder<T, C> thenReplace() {
        return new ReplaceBuilder<>(menu(), pendingOperations())
                .where(requireSelector())
                .range(requireRange());
    }

    /**
     * Continua amb una ordenació sobre el mateix menú sense executar encara
     * la cadena. El rang actual es transfereix al builder següent.
     *
     * @return builder d'ordenació encadenat
     */
    public SortBuilder<T, C> thenSort() {
        return new SortBuilder<>(menu(), pendingOperations())
                .range(requireRange());
    }
}