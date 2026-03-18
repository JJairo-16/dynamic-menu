package menu.editor.builders;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import menu.DynamicMenu;
import menu.editor.builders.base.AbstractSelectableRangedBuilder;
import menu.editor.builders.base.InheritanceMode;
import menu.editor.core.QueryFamily;
import menu.model.MenuOption;

/**
 * Builder fluent per a operacions de consulta.
 *
 * <p>
 * No modifica el menú real quan resol una consulta. Si hi ha
 * operacions pendents, es resol sobre un estat virtual temporal.
 */
public final class QueryBuilder<T, C>
        extends AbstractSelectableRangedBuilder<T, C, QueryBuilder<T, C>> {

    private boolean previewResolved = false;
    private DynamicMenu<T, C> cachedPreviewMenu;

    private boolean optionsResolved = false;
    private List<MenuOption<T, C>> cachedOptions;

    private boolean indexesResolved = false;
    private List<Integer> cachedIndexes;
    private Integer cachedFirstIndex;
    private Integer cachedLastIndex;

    /** Crea un builder de consulta sobre un menú. */
    public QueryBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    /** Crea un builder amb operacions pendents. */
    public QueryBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {

        super(menu, pendingPipeline);
    }

    /** Crea un builder amb estat pendent explícit. */
    public QueryBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline,
            boolean hasPendingOperations) {

        super(menu, pendingPipeline, hasPendingOperations);
    }

    /** Retorna aquesta instància tipada. */
    @Override
    protected QueryBuilder<T, C> self() {
        return this;
    }

    /** Invalida la caché de resultats. */
    @Override
    protected void onStateChanged() {
        this.optionsResolved = false;
        this.cachedOptions = null;

        this.indexesResolved = false;
        this.cachedIndexes = null;
        this.cachedFirstIndex = null;
        this.cachedLastIndex = null;
    }

    /** Crea el menú virtual sobre el qual es resol la query. */
    private DynamicMenu<T, C> buildPreviewMenu() {
        if (previewResolved) {
            return cachedPreviewMenu;
        }

        if (!hasPendingOperations()) {
            cachedPreviewMenu = menu();
            previewResolved = true;
            return cachedPreviewMenu;
        }

        DynamicMenu<T, C> previewMenu = menu().createChildMenu(
                menu().getTitle(),
                menu().getContext());

        applyPendingOperationsOn(previewMenu);

        cachedPreviewMenu = previewMenu;
        previewResolved = true;
        return cachedPreviewMenu;
    }

    /** Resol totes les opcions coincidents. */
    private List<MenuOption<T, C>> resolveOptions() {
        if (optionsResolved) {
            return cachedOptions;
        }

        cachedOptions = QueryFamily.matchingOptions(
                buildPreviewMenu(),
                requireSelector(),
                requireRange());

        optionsResolved = true;
        return cachedOptions;
    }

    /** Resol els índexs si encara no s'han calculat. */
    private void resolveIndexesIfNeeded() {
        if (indexesResolved) {
            return;
        }

        DynamicMenu<T, C> previewMenu = buildPreviewMenu();
        cachedIndexes = QueryFamily.indexesOf(previewMenu, requireSelector(), requireRange());
        cachedFirstIndex = cachedIndexes.isEmpty() ? -1 : cachedIndexes.get(0);
        cachedLastIndex = cachedIndexes.isEmpty() ? -1 : cachedIndexes.get(cachedIndexes.size() - 1);
        indexesResolved = true;
    }

    /** Resol la query i transforma el resultat amb un col·lector. */
    public <R> R collect(Function<? super List<MenuOption<T, C>>, ? extends R> collector) {
        Objects.requireNonNull(collector, "El col·lector no pot ser nul");
        return collector.apply(resolveOptions());
    }

    /** Resol la query i transforma el resultat en una llista. */
    public List<MenuOption<T, C>> toList() {
        return List.copyOf(resolveOptions());
    }

    /** Resol la query i transforma el resultat en un stream. */
    public Stream<MenuOption<T, C>> stream() {
        return toList().stream();
    }

    /** Alias semàntic d'options(). */
    public List<MenuOption<T, C>> resolve() {
        return resolveOptions();
    }

    /** Indica si hi ha coincidències. */
    public boolean exists() {
        return !resolveOptions().isEmpty();
    }

    /** Retorna el nombre de coincidències. */
    public int count() {
        return resolveOptions().size();
    }

    /** Retorna el primer índex coincident o -1. */
    public int firstIndex() {
        resolveIndexesIfNeeded();
        return cachedFirstIndex;
    }

    /** Retorna l'últim índex coincident o -1. */
    public int lastIndex() {
        resolveIndexesIfNeeded();
        return cachedLastIndex;
    }

    /** Retorna tots els índexs coincidents. */
    public List<Integer> indexes() {
        resolveIndexesIfNeeded();
        return cachedIndexes;
    }

    /** Retorna totes les opcions coincidents. */
    public List<MenuOption<T, C>> options() {
        return resolveOptions();
    }

    /** Retorna la primera opció coincident o null. */
    public MenuOption<T, C> first() {
        List<MenuOption<T, C>> options = resolveOptions();
        return options.isEmpty() ? null : options.get(0);
    }

    /** Retorna l'última opció coincident o null. */
    public MenuOption<T, C> last() {
        List<MenuOption<T, C>> options = resolveOptions();
        return options.isEmpty() ? null : options.get(options.size() - 1);
    }

    /** Aplica l'herència de selecció o rang. */
    private <B extends AbstractSelectableRangedBuilder<T, C, B>> B applySelectableInheritance(
            B target,
            InheritanceMode inheritanceMode) {

        Objects.requireNonNull(inheritanceMode, "El mode d'herència no pot ser nul");

        switch (inheritanceMode) {
            case NONE:
                return target;
            case RANGE:
                return inheritRangeTo(target);
            case SELECTION:
                return inheritSelectionTo(target);
            case ALL:
                return inheritSelectionTo(target);
            default:
                throw new IllegalArgumentException("Mode d'herència no suportat: " + inheritanceMode);
        }
    }

    /** Encadena una altra consulta amb herència total. */
    public QueryBuilder<T, C> thenQuery() {
        return thenQuery(InheritanceMode.ALL);
    }

    /** Encadena una altra consulta. */
    public QueryBuilder<T, C> thenQuery(InheritanceMode inheritanceMode) {
        return applySelectableInheritance(chainToQuery(target -> {
        }), inheritanceMode);
    }

    /** Encadena una eliminació amb herència total. */
    public RemoveBuilder<T, C> thenRemove() {
        return thenRemove(InheritanceMode.ALL);
    }

    /** Encadena una eliminació. */
    public RemoveBuilder<T, C> thenRemove(InheritanceMode inheritanceMode) {
        return applySelectableInheritance(chainToRemove(target -> {
        }), inheritanceMode);
    }

    /** Encadena una substitució amb herència total. */
    public ReplaceBuilder<T, C> thenReplace() {
        return thenReplace(InheritanceMode.ALL);
    }

    /** Encadena una substitució. */
    public ReplaceBuilder<T, C> thenReplace(InheritanceMode inheritanceMode) {
        return applySelectableInheritance(chainToReplace(target -> {
        }), inheritanceMode);
    }

    /** Encadena una ordenació amb herència total. */
    public SortBuilder<T, C> thenSort() {
        return thenSort(InheritanceMode.ALL);
    }

    /** Encadena una ordenació. */
    public SortBuilder<T, C> thenSort(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToSort(target -> {
        }), inheritanceMode);
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
