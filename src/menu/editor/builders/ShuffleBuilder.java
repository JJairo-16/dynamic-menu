package menu.editor.builders;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import menu.DynamicMenu;
import menu.editor.Range;
import menu.editor.builders.base.AbstractRangedBuilder;
import menu.editor.builders.base.InheritanceMode;
import menu.editor.core.MenuEditorSupport;
import menu.editor.core.ShuffleFamily;
import menu.editor.helpers.OptionSelector;
import menu.editor.planning.OperationPlan;
import menu.editor.planning.PlannedOperation;
import menu.editor.planning.operations.ShufflePlannedOperation;

/**
 * Builder fluent per a operacions de barreja.
 *
 * @param <T> tipus de retorn del menú
 * @param <C> tipus del context del menú
 */
public final class ShuffleBuilder<T, C>
        extends AbstractRangedBuilder<T, C, ShuffleBuilder<T, C>> {

    private Supplier<Random> randomSupplier = Random::new;
    private OptionSelector<T, C> firstSelector = MenuEditorSupport.alwaysFalseSelector();
    private OptionSelector<T, C> lastSelector = MenuEditorSupport.alwaysFalseSelector();
    private boolean hasPinnedFirst = false;
    private boolean hasPinnedLast = false;

    /**
     * Crea un builder sense operacions pendents.
     *
     * @param menu menú objectiu
     */
    public ShuffleBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    /**
     * Crea un builder amb pipeline pendent.
     *
     * @param menu            menú objectiu
     * @param pendingPipeline pipeline pendent
     */
    public ShuffleBuilder(
            DynamicMenu<T, C> menu,
            OperationPlan<T, C> pendingPlan) {

        super(menu, pendingPlan);
    }

    /**
     * Crea un builder amb control explícit d'estat pendent.
     *
     * @param menu                 menú objectiu
     * @param pendingPipeline      pipeline pendent
     * @param hasPendingOperations indica si hi ha operacions pendents
     */
    public ShuffleBuilder(
            DynamicMenu<T, C> menu,
            OperationPlan<T, C> pendingPlan,
            boolean hasPendingOperations) {

        super(menu, pendingPlan, hasPendingOperations);
    }

    /**
     * Retorna aquest builder.
     *
     * @return aquest builder
     */
    @Override
    protected ShuffleBuilder<T, C> self() {
        return this;
    }

    /**
     * Defineix el {@link Random} a utilitzar.
     *
     * @param random random a usar
     * @return aquest builder
     */
    public ShuffleBuilder<T, C> random(Random random) {
        Objects.requireNonNull(random, "El random no pot ser nul");
        this.randomSupplier = () -> random;
        return this;

    }

    /**
     * Defineix una llavor per a una barreja reproduïble.
     *
     * @param seed llavor a usar
     * @return aquest builder
     */
    public ShuffleBuilder<T, C> seed(long seed) {
        this.randomSupplier = () -> new Random(seed);
        return this;
    }

    /**
     * Fixa opcions al principi del segment barrejat.
     *
     * @param selector selector d'opcions fixes al principi
     * @return aquest builder
     */
    public ShuffleBuilder<T, C> pinFirst(OptionSelector<T, C> selector) {
        this.firstSelector = Objects.requireNonNull(selector, "El selector inicial no pot ser nul");
        this.hasPinnedFirst = true;
        return this;
    }

    /**
     * Fixa opcions al principi segons el label.
     *
     * @param predicate condició sobre el label
     * @return aquest builder
     */
    public ShuffleBuilder<T, C> pinLabelFirst(Predicate<String> predicate) {
        Objects.requireNonNull(predicate, "La condició no pot ser nul·la");
        this.firstSelector = (i, opt) -> predicate.test(opt.label());
        this.hasPinnedFirst = true;
        return this;
    }

    /**
     * Fixa opcions al final del segment barrejat.
     *
     * @param selector selector d'opcions fixes al final
     * @return aquest builder
     */
    public ShuffleBuilder<T, C> pinLast(OptionSelector<T, C> selector) {
        this.lastSelector = Objects.requireNonNull(selector, "El selector final no pot ser nul");
        this.hasPinnedLast = true;
        return this;
    }

    /**
     * Fixa opcions al final segons el label.
     *
     * @param predicate condició sobre el label
     * @return aquest builder
     */
    public ShuffleBuilder<T, C> pinLabelLast(Predicate<String> predicate) {
        Objects.requireNonNull(predicate, "La condició no pot ser nul·la");
        this.lastSelector = (i, opt) -> predicate.test(opt.label());
        this.hasPinnedLast = true;
        return this;
    }

    /**
     * Defineix selectors per a opcions fixes al principi i al final.
     *
     * @param firstSelector selector inicial
     * @param lastSelector  selector final
     * @return aquest builder
     */
    public ShuffleBuilder<T, C> pinned(
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        this.firstSelector = Objects.requireNonNull(firstSelector, "El selector inicial no pot ser nul");
        this.lastSelector = Objects.requireNonNull(lastSelector, "El selector final no pot ser nul");
        this.hasPinnedFirst = true;
        this.hasPinnedLast = true;
        return this;
    }

    /**
     * Defineix opcions fixes pels seus índexs.
     *
     * @param firstIndexes índexs a mantenir al principi
     * @param lastIndexes  índexs a mantenir al final
     * @return aquest builder
     */
    public ShuffleBuilder<T, C> pinnedIndexes(
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        Set<Integer> first = firstIndexes == null ? Set.of() : new LinkedHashSet<>(firstIndexes);
        Set<Integer> last = lastIndexes == null ? Set.of() : new LinkedHashSet<>(lastIndexes);

        this.firstSelector = (index, option) -> first.contains(index);
        this.lastSelector = (index, option) -> last.contains(index);
        this.hasPinnedFirst = !first.isEmpty();
        this.hasPinnedLast = !last.isEmpty();
        return this;
    }

    /**
     * Crea l'operació actual de barreja.
     *
     * @return operació actual
     */
    private PlannedOperation<T, C> currentOperation() {
        Range currentRange = requireRange();
        Random currentRandom = requireRandom();
        OptionSelector<T, C> first = requireFirstSelector();
        OptionSelector<T, C> last = requireLastSelector();

        return new ShufflePlannedOperation<>(
                currentRandom,
                first,
                last,
                currentRange,
                hasPinnedFirst,
                hasPinnedLast);
    }

    /**
     * Executa la cadena pendent i després la barreja final.
     *
     * @return menú modificat
     */
    public DynamicMenu<T, C> execute() {
        applyPendingOperations();

        return ShuffleFamily.shuffle(
                menu(),
                requireRandom(),
                requireFirstSelector(),
                requireLastSelector(),
                requireRange());
    }

    /**
     * Àlies semàntic d'{@link #execute()}.
     *
     * @return menú modificat
     */
    public DynamicMenu<T, C> apply() {
        return execute();
    }

    /**
     * Encadena una altra barreja sense herència.
     *
     * @return builder següent
     */
    public ShuffleBuilder<T, C> thenShuffle() {
        return thenShuffle(InheritanceMode.NONE);
    }

    /**
     * Encadena una altra barreja amb herència configurable.
     *
     * @param inheritanceMode mode d'herència
     * @return builder següent
     */
    public ShuffleBuilder<T, C> thenShuffle(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToShuffle(currentOperation()), inheritanceMode);
    }

    /**
     * Encadena una eliminació sense herència.
     *
     * @return builder següent
     */
    public RemoveBuilder<T, C> thenRemove() {
        return thenRemove(InheritanceMode.NONE);
    }

    /**
     * Encadena una eliminació amb herència configurable.
     *
     * @param inheritanceMode mode d'herència
     * @return builder següent
     */
    public RemoveBuilder<T, C> thenRemove(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToRemove(currentOperation()), inheritanceMode);
    }

    /**
     * Encadena un reemplaç sense herència.
     *
     * @return builder següent
     */
    public ReplaceBuilder<T, C> thenReplace() {
        return thenReplace(InheritanceMode.NONE);
    }

    /**
     * Encadena un reemplaç amb herència configurable.
     *
     * @param inheritanceMode mode d'herència
     * @return builder següent
     */
    public ReplaceBuilder<T, C> thenReplace(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToReplace(currentOperation()), inheritanceMode);
    }

    /**
     * Encadena una ordenació sense herència.
     *
     * @return builder següent
     */
    public SortBuilder<T, C> thenSort() {
        return thenSort(InheritanceMode.NONE);
    }

    /**
     * Encadena una ordenació amb herència configurable.
     *
     * @param inheritanceMode mode d'herència
     * @return builder següent
     */
    public SortBuilder<T, C> thenSort(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToSort(currentOperation()), inheritanceMode);
    }

    /**
     * Encadena una consulta sense herència.
     *
     * @return builder següent
     */
    public QueryBuilder<T, C> thenQuery() {
        return thenQuery(InheritanceMode.NONE);
    }

    /**
     * Encadena una consulta amb herència configurable.
     *
     * @param inheritanceMode mode d'herència
     * @return builder següent
     */
    public QueryBuilder<T, C> thenQuery(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToQuery(currentOperation()), inheritanceMode);
    }

    private Random requireRandom() {
        return Objects.requireNonNull(randomSupplier, "El random no pot ser nul").get();
    }

    private OptionSelector<T, C> requireFirstSelector() {
        return Objects.requireNonNull(firstSelector, "El selector inicial no pot ser nul");
    }

    private OptionSelector<T, C> requireLastSelector() {
        return Objects.requireNonNull(lastSelector, "El selector final no pot ser nul");
    }
}