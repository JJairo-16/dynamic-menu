package menu.editor.builders;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import menu.DynamicMenu;
import menu.editor.EditConfig;
import menu.editor.Range;
import menu.editor.core.RemoveFamily;
import menu.editor.helpers.OptionSelector;

/**
 * Builder fluent per a operacions d'eliminació.
 *
 * <p>
 * Aquesta API actua com a façana pública sobre la família interna
 * {@link RemoveFamily}, de manera que els casos simples es poden resoldre
 * amb wrappers estàtics i els casos avançats amb una construcció fluïda.
 *
 * <p>
 * Les crides {@code thenX()} no executen cap canvi immediatament.
 * Cada builder acumula la seva operació pendent i només l'última crida
 * terminal executa tota la cadena en ordre.
 *
 * @param <T> tipus de retorn del menú
 * @param <C> tipus del context del menú
 */
public final class RemoveBuilder<T, C> extends AbstractChainableMenuBuilder<T, C> {
    private OptionSelector<T, C> selector;
    private Range range = Range.all();
    private int limit = Integer.MAX_VALUE;
    private boolean reverse = false;

    public RemoveBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    RemoveBuilder(
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
    public RemoveBuilder<T, C> where(OptionSelector<T, C> selector) {
        this.selector = Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        return this;
    }

    /**
     * Defineix la condició de selecció sobre el label.
     *
     * @param predicate condició a aplicar
     * @return aquest builder
     */
    public RemoveBuilder<T, C> whereLabel(Predicate<String> predicate) {
        Objects.requireNonNull(predicate, "La condició no pot ser nul·la");
        this.selector = (i, opt) -> predicate.test(opt.label());
        return this;
    }

    /**
     * Defineix que s'han de considerar totes les opcions.
     *
     * @return aquest builder
     */
    public RemoveBuilder<T, C> whereAny() {
        this.selector = (index, option) -> true;
        return this;
    }

    /**
     * Defineix el rang d'actuació.
     *
     * @param range rang a aplicar
     * @return aquest builder
     */
    public RemoveBuilder<T, C> range(Range range) {
        this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
        return this;
    }

    /**
     * Defineix el rang d'actuació.
     *
     * @param fromInclusive inici inclòs
     * @param toExclusive   final exclòs
     * @return aquest builder
     */
    public RemoveBuilder<T, C> range(int fromInclusive, int toExclusive) {
        return range(Range.of(fromInclusive, toExclusive));
    }

    /**
     * Defineix el límit màxim d'eliminacions.
     *
     * @param limit límit a aplicar
     * @return aquest builder
     */
    public RemoveBuilder<T, C> limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Configura el recorregut en sentit invers.
     *
     * @return aquest builder
     */
    public RemoveBuilder<T, C> reverse() {
        this.reverse = true;
        return this;
    }

    /**
     * Defineix explícitament si s'ha de recórrer en sentit invers.
     *
     * @param reverse valor nou
     * @return aquest builder
     */
    public RemoveBuilder<T, C> reverse(boolean reverse) {
        this.reverse = reverse;
        return this;
    }

    /**
     * Limita l'operació a la primera coincidència.
     *
     * @return aquest builder
     */
    public RemoveBuilder<T, C> first() {
        this.limit = 1;
        this.reverse = false;
        return this;
    }

    /**
     * Limita l'operació a l'última coincidència.
     *
     * @return aquest builder
     */
    public RemoveBuilder<T, C> last() {
        this.limit = 1;
        this.reverse = true;
        return this;
    }

    /**
     * Defineix que s'han de considerar totes les coincidències.
     *
     * @return aquest builder
     */
    public RemoveBuilder<T, C> all() {
        this.limit = Integer.MAX_VALUE;
        return this;
    }

    /**
     * Aplica una configuració base.
     *
     * @param config configuració d'edició
     * @return aquest builder
     */
    public RemoveBuilder<T, C> config(EditConfig config) {
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");
        this.range = config.range();
        this.limit = config.limit();
        this.reverse = config.reverse();
        return this;
    }

    /**
     * Construeix la configuració actual.
     *
     * @return configuració equivalent a l'estat actual del builder
     */
    public EditConfig buildConfig() {
        return EditConfig.builder()
                .range(range)
                .limit(limit)
                .reverse(reverse)
                .build();
    }

    private Consumer<DynamicMenu<T, C>> currentOperation() {
        OptionSelector<T, C> currentSelector = Objects.requireNonNull(
                selector,
                "La condició no pot ser nul·la");
        EditConfig currentConfig = buildConfig();

        return currentMenu -> RemoveFamily.removeIf(
                currentMenu,
                currentSelector,
                currentConfig);
    }

    /**
     * Executa tota la cadena pendent i retorna el nombre d'elements eliminats
     * de l'última operació d'aquest builder.
     *
     * @return nombre d'elements eliminats per l'última operació
     */
    public int execute() {
        applyPendingOperations();
        return RemoveFamily.removeIf(
                menu(),
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                buildConfig());
    }

    /**
     * Executa tota la cadena pendent i indica si l'última operació ha eliminat
     * almenys un element.
     *
     * @return {@code true} si l'última operació ha eliminat almenys una opció
     */
    public boolean executeAny() {
        return execute() > 0;
    }

    /**
     * Continua amb una nova eliminació sobre el mateix menú sense executar encara
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

    /**
     * Continua amb una nova consulta sobre el mateix menú sense executar encara
     * cap canvi real. La condició i el rang actuals es transfereixen.
     *
     * @return builder de consulta encadenat
     */
    public QueryBuilder<T, C> thenQuery() {
        return new QueryBuilder<>(menu(), pendingPlus(currentOperation()));
    }
}
