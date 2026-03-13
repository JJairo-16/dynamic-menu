package menu.editor.builders;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import menu.DynamicMenu;
import menu.action.MenuAction;
import menu.action.MenuRuntimeAction;
import menu.action.SimpleMenuAction;
import menu.editor.EditConfig;
import menu.editor.Range;
import menu.editor.core.MenuEditorSupport;
import menu.editor.core.ReplaceFamily;
import menu.editor.helpers.ActionMapper;
import menu.editor.helpers.LabelMapper;
import menu.editor.helpers.OptionMapper;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;

/**
 * Builder fluent per a operacions de substitució.
 *
 * <p>Aquesta API actua com a façana pública sobre la família interna
 * {@link ReplaceFamily}, de manera que els casos simples es poden resoldre
 * amb wrappers estàtics i els casos avançats amb una construcció fluïda.
 *
 * <p>Les crides {@code thenX()} no executen cap canvi immediatament.
 * Cada builder acumula la seva operació pendent i només l'última crida
 * terminal executa tota la cadena en ordre.
 *
 * @param <T> tipus de retorn del menú
 * @param <C> tipus del context del menú
 */
public final class ReplaceBuilder<T, C> extends AbstractChainableMenuBuilder<T, C> {
    private OptionSelector<T, C> selector;
    private OptionMapper<T, C> mapper;
    private Range range = Range.all();
    private int limit = Integer.MAX_VALUE;
    private boolean reverse = false;

    public ReplaceBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    ReplaceBuilder(
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
    public ReplaceBuilder<T, C> where(OptionSelector<T, C> selector) {
        this.selector = Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        return this;
    }

    /**
     * Defineix la condició de selecció sobre el label.
     *
     * @param predicate condició a aplicar
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> whereLabel(Predicate<String> predicate) {
        Objects.requireNonNull(predicate, "La condició no pot ser nul·la");
        this.selector = (i, opt) -> predicate.test(opt.label());
        return this;
    }

    /**
     * Defineix el transformador general d'opcions.
     *
     * @param mapper transformador a aplicar
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> map(OptionMapper<T, C> mapper) {
        this.mapper = Objects.requireNonNull(mapper, "El transformador no pot ser nul");
        return this;
    }

    /**
     * Substitueix només el label.
     *
     * @param newLabel nou label
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> label(String newLabel) {
        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(newLabel, option.action());
        return this;
    }

    /**
     * Substitueix només el label amb un transformador.
     *
     * @param mapper transformador de labels
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> label(LabelMapper<T, C> mapper) {
        Objects.requireNonNull(mapper, "El transformador de labels no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(
                Objects.requireNonNull(
                        mapper.map(index, option),
                        "El transformador de labels no pot retornar nul"),
                option.action());
        return this;
    }

    /**
     * Substitueix només el comportament.
     *
     * @param newAction nou comportament
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> action(MenuRuntimeAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(option.label(), newAction);
        return this;
    }

    /**
     * Substitueix només el comportament.
     *
     * @param newAction nou comportament
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> action(MenuAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return action(MenuEditorSupport.runtimeOf(newAction));
    }

    /**
     * Substitueix només el comportament.
     *
     * @param newAction nou comportament
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> action(SimpleMenuAction<T> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return action(MenuEditorSupport.runtimeOf(newAction));
    }

    /**
     * Substitueix només el comportament amb un transformador.
     *
     * @param mapper transformador de comportaments
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> action(ActionMapper<T, C> mapper) {
        Objects.requireNonNull(mapper, "El transformador de comportaments no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(
                option.label(),
                Objects.requireNonNull(
                        mapper.map(index, option),
                        "El transformador de comportaments no pot retornar nul"));
        return this;
    }

    /**
     * Substitueix tota l'opció per label i comportament.
     *
     * @param newLabel nou label
     * @param newAction nou comportament
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> option(String newLabel, MenuRuntimeAction<T, C> newAction) {
        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(newLabel, newAction);
        return this;
    }

    /**
     * Substitueix tota l'opció per label i comportament.
     *
     * @param newLabel nou label
     * @param newAction nou comportament
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> option(String newLabel, MenuAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return option(newLabel, MenuEditorSupport.runtimeOf(newAction));
    }

    /**
     * Substitueix tota l'opció per label i comportament.
     *
     * @param newLabel nou label
     * @param newAction nou comportament
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> option(String newLabel, SimpleMenuAction<T> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return option(newLabel, MenuEditorSupport.runtimeOf(newAction));
    }

    /**
     * Substitueix tota l'opció per una altra opció ja construïda.
     *
     * @param newOption nova opció
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> option(MenuOption<T, C> newOption) {
        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        this.mapper = (index, option) -> newOption;
        return this;
    }

    /**
     * Defineix el rang d'actuació.
     *
     * @param range rang a aplicar
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> range(Range range) {
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
    public ReplaceBuilder<T, C> range(int fromInclusive, int toExclusive) {
        return range(Range.of(fromInclusive, toExclusive));
    }

    /**
     * Defineix el límit màxim de substitucions.
     *
     * @param limit límit a aplicar
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Configura el recorregut en sentit invers.
     *
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> reverse() {
        this.reverse = true;
        return this;
    }

    /**
     * Defineix explícitament si s'ha de recórrer en sentit invers.
     *
     * @param reverse valor nou
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> reverse(boolean reverse) {
        this.reverse = reverse;
        return this;
    }

    /**
     * Limita l'operació a la primera coincidència.
     *
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> first() {
        this.limit = 1;
        this.reverse = false;
        return this;
    }

    /**
     * Limita l'operació a l'última coincidència.
     *
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> last() {
        this.limit = 1;
        this.reverse = true;
        return this;
    }

    /**
     * Defineix que s'han de considerar totes les coincidències.
     *
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> all() {
        this.limit = Integer.MAX_VALUE;
        return this;
    }

    /**
     * Aplica una configuració base.
     *
     * @param config configuració d'edició
     * @return aquest builder
     */
    public ReplaceBuilder<T, C> config(EditConfig config) {
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
        OptionMapper<T, C> currentMapper = Objects.requireNonNull(
                mapper,
                "El transformador no pot ser nul");
        EditConfig currentConfig = buildConfig();

        return currentMenu -> ReplaceFamily.replaceIf(
                currentMenu,
                currentSelector,
                currentMapper,
                currentConfig);
    }

    /**
     * Executa tota la cadena pendent i retorna el nombre d'elements substituïts
     * de l'última operació d'aquest builder.
     *
     * @return nombre d'elements substituïts per l'última operació
     */
    public int execute() {
        applyPendingOperations();
        return ReplaceFamily.replaceIf(
                menu(),
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                Objects.requireNonNull(mapper, "El transformador no pot ser nul"),
                buildConfig());
    }

    /**
     * Executa tota la cadena pendent i indica si l'última operació ha substituït
     * almenys un element.
     *
     * @return {@code true} si l'última operació ha substituït almenys una opció
     */
    public boolean executeAny() {
        return execute() > 0;
    }

    /**
     * Continua amb una nova substitució sense executar encara la cadena.
     *
     * @return builder de substitució encadenat
     */
    public ReplaceBuilder<T, C> thenReplace() {
        return new ReplaceBuilder<>(menu(), pendingPlus(currentOperation()));
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
     * Continua amb una ordenació sense executar encara la cadena.
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
