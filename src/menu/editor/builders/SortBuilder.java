package menu.editor.builders;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import menu.DynamicMenu;
import menu.editor.Range;
import menu.editor.builders.base.AbstractRangedBuilder;
import menu.editor.builders.base.InheritanceMode;
import menu.editor.core.MenuEditorSupport;
import menu.editor.core.SortFamily;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;

/** Builder fluent per a operacions d'ordenació. */
public final class SortBuilder<T, C>
        extends AbstractRangedBuilder<T, C, SortBuilder<T, C>> {

    private Comparator<MenuOption<T, C>> comparator = MenuEditorSupport.defaultLabelComparator();
    private OptionSelector<T, C> firstSelector = MenuEditorSupport.alwaysFalseSelector();
    private OptionSelector<T, C> lastSelector = MenuEditorSupport.alwaysFalseSelector();
    private boolean descending = false;

    /** Crea un builder d'ordenació sobre un menú. */
    public SortBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    /** Crea un builder amb operacions pendents. */
    public SortBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {

        super(menu, pendingPipeline);
    }

    /** Crea un builder amb estat pendent explícit. */
    public SortBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline,
            boolean hasPendingOperations) {

        super(menu, pendingPipeline, hasPendingOperations);
    }

    /** Retorna aquesta instància tipada. */
    @Override
    protected SortBuilder<T, C> self() {
        return this;
    }

    /** Defineix l'ordenació per etiqueta amb el comparador per defecte. */
    public SortBuilder<T, C> byLabel() {
        this.comparator = MenuEditorSupport.defaultLabelComparator();
        return this;
    }

    /** Defineix el comparador base. */
    public SortBuilder<T, C> comparator(Comparator<MenuOption<T, C>> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "El comparador no pot ser nul");
        return this;
    }

    /** Fixa opcions al principi del segment ordenat. */
    public SortBuilder<T, C> pinFirst(OptionSelector<T, C> selector) {
        this.firstSelector = Objects.requireNonNull(selector, "El selector inicial no pot ser nul");
        return this;
    }

    /** Fixa opcions al principi del segment ordenat segons el label. */
    public SortBuilder<T, C> pinLabelFirst(Predicate<String> predicate) {
        Objects.requireNonNull(predicate, "La condició no pot ser nul·la");
        this.firstSelector = (i, opt) -> predicate.test(opt.label());
        return this;
    }

    /** Fixa opcions al final del segment ordenat. */
    public SortBuilder<T, C> pinLast(OptionSelector<T, C> selector) {
        this.lastSelector = Objects.requireNonNull(selector, "El selector final no pot ser nul");
        return this;
    }

    /** Fixa opcions al final del segment ordenat segons el label. */
    public SortBuilder<T, C> pinLabelLast(Predicate<String> predicate) {
        Objects.requireNonNull(predicate, "La condició no pot ser nul·la");
        this.lastSelector = (i, opt) -> predicate.test(opt.label());
        return this;
    }

    /** Defineix selectors per opcions fixades al principi i al final. */
    public SortBuilder<T, C> pinned(
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        this.firstSelector = Objects.requireNonNull(firstSelector, "El selector inicial no pot ser nul");
        this.lastSelector = Objects.requireNonNull(lastSelector, "El selector final no pot ser nul");
        return this;
    }

    /** Defineix opcions fixades al principi i al final pels seus índexs. */
    public SortBuilder<T, C> pinnedIndexes(
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        Set<Integer> first = firstIndexes == null ? Set.of() : new LinkedHashSet<>(firstIndexes);
        Set<Integer> last = lastIndexes == null ? Set.of() : new LinkedHashSet<>(lastIndexes);

        this.firstSelector = (index, option) -> first.contains(index);
        this.lastSelector = (index, option) -> last.contains(index);
        return this;
    }

    /** Defineix l'ordenació ascendent. */
    public SortBuilder<T, C> ascending() {
        this.descending = false;
        return this;
    }

    /** Defineix l'ordenació descendent. */
    public SortBuilder<T, C> descending() {
        this.descending = true;
        return this;
    }

    /** Defineix explícitament si l'ordenació és descendent. */
    public SortBuilder<T, C> descending(boolean descending) {
        this.descending = descending;
        return this;
    }

    /** Retorna el comparador efectiu. */
    private Comparator<MenuOption<T, C>> effectiveComparator() {
        Comparator<MenuOption<T, C>> currentComparator = Objects.requireNonNull(
                comparator,
                "El comparador no pot ser nul");
        return descending ? currentComparator.reversed() : currentComparator;
    }

    /** Construeix l'operació actual d'ordenació. */
    private Consumer<DynamicMenu<T, C>> currentOperation() {
        Comparator<MenuOption<T, C>> currentComparator = effectiveComparator();
        OptionSelector<T, C> currentFirstSelector = Objects.requireNonNull(
                firstSelector,
                "El selector inicial no pot ser nul");
        OptionSelector<T, C> currentLastSelector = Objects.requireNonNull(
                lastSelector,
                "El selector final no pot ser nul");
        Range currentRange = requireRange();

        return currentMenu -> SortFamily.sortByLabel(
                currentMenu,
                currentComparator,
                currentFirstSelector,
                currentLastSelector,
                currentRange);
    }

    /** Executa tota la cadena pendent i després l'ordenació final. */
    public DynamicMenu<T, C> execute() {
        applyPendingOperations();
        return SortFamily.sortByLabel(
                menu(),
                effectiveComparator(),
                Objects.requireNonNull(firstSelector, "El selector inicial no pot ser nul"),
                Objects.requireNonNull(lastSelector, "El selector final no pot ser nul"),
                requireRange());
    }

    /** Alias semàntic d'execute(). */
    public DynamicMenu<T, C> apply() {
        return execute();
    }

    /** Encadena una altra ordenació sense herència. */
    public SortBuilder<T, C> thenSort() {
        return thenSort(InheritanceMode.NONE);
    }

    /** Encadena una altra ordenació. */
    public SortBuilder<T, C> thenSort(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToSort(currentOperation()), inheritanceMode);
    }

    /** Encadena una eliminació sense herència. */
    public RemoveBuilder<T, C> thenRemove() {
        return thenRemove(InheritanceMode.NONE);
    }

    /** Encadena una eliminació. */
    public RemoveBuilder<T, C> thenRemove(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToRemove(currentOperation()), inheritanceMode);
    }

    /** Encadena una substitució sense herència. */
    public ReplaceBuilder<T, C> thenReplace() {
        return thenReplace(InheritanceMode.NONE);
    }

    /** Encadena una substitució. */
    public ReplaceBuilder<T, C> thenReplace(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToReplace(currentOperation()), inheritanceMode);
    }

    /** Encadena una consulta sense herència. */
    public QueryBuilder<T, C> thenQuery() {
        return thenQuery(InheritanceMode.NONE);
    }

    /** Encadena una consulta. */
    public QueryBuilder<T, C> thenQuery(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToQuery(currentOperation()), inheritanceMode);
    }

    /** Encadena una barreja amb herència de rang. */
    public ShuffleBuilder<T, C> thenShuffle() {
        return thenShuffle(InheritanceMode.RANGE);
    }

    /** Encadena una barreja. */
    public ShuffleBuilder<T, C> thenShuffle(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToShuffle(target -> {
        }), inheritanceMode);
    }
}
