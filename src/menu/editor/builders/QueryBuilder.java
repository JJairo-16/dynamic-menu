package menu.editor.builders;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import menu.DynamicMenu;
import menu.editor.core.QueryFamily;
import menu.model.MenuOption;

/**
 * Builder fluent per a operacions de consulta.
 *
 * <p>No modifica el menú real quan resol una consulta. Si hi ha
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

    public QueryBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    QueryBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {

        super(menu, pendingPipeline);
    }

    QueryBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline,
            boolean hasPendingOperations) {

        super(menu, pendingPipeline, hasPendingOperations);
    }

    @Override
    protected QueryBuilder<T, C> self() {
        return this;
    }

    @Override
    protected void onStateChanged() {
        this.optionsResolved = false;
        this.cachedOptions = null;

        this.indexesResolved = false;
        this.cachedIndexes = null;
        this.cachedFirstIndex = null;
        this.cachedLastIndex = null;
    }

    /**
     * Crea el menú virtual sobre el qual es resol la query.
     */
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

    /**
     * Resol totes les opcions coincidents.
     */
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

    /**
     * Resol la query i transforma el resultat amb un col·lector.
     */
    public <R> R collect(Function<? super List<MenuOption<T, C>>, ? extends R> collector) {
        Objects.requireNonNull(collector, "El col·lector no pot ser nul");
        return collector.apply(resolveOptions());
    }

    /**
     * Alias semàntic d'options().
     */
    public List<MenuOption<T, C>> resolve() {
        return resolveOptions();
    }

    public boolean exists() {
        return !resolveOptions().isEmpty();
    }

    public int count() {
        return resolveOptions().size();
    }

    public int firstIndex() {
        resolveIndexesIfNeeded();
        return cachedFirstIndex;
    }

    public int lastIndex() {
        resolveIndexesIfNeeded();
        return cachedLastIndex;
    }

    public List<Integer> indexes() {
        resolveIndexesIfNeeded();
        return cachedIndexes;
    }

    public List<MenuOption<T, C>> options() {
        return resolveOptions();
    }

    public MenuOption<T, C> first() {
        List<MenuOption<T, C>> options = resolveOptions();
        return options.isEmpty() ? null : options.get(0);
    }

    public MenuOption<T, C> last() {
        List<MenuOption<T, C>> options = resolveOptions();
        return options.isEmpty() ? null : options.get(options.size() - 1);
    }

    /**
     * Continua amb una nova query mantenint selector i rang.
     */
    public QueryBuilder<T, C> thenQuery() {
        return chainToQuery(target -> { })
                .where(requireSelector())
                .range(requireRange());
    }

    /**
     * Continua amb remove mantenint selector i rang.
     */
    public RemoveBuilder<T, C> thenRemove() {
        return chainToRemove(target -> { })
                .where(requireSelector())
                .range(requireRange());
    }

    /**
     * Continua amb replace mantenint selector i rang.
     */
    public ReplaceBuilder<T, C> thenReplace() {
        return chainToReplace(target -> { })
                .where(requireSelector())
                .range(requireRange());
    }

    /**
     * Continua amb sort mantenint només el rang.
     */
    public SortBuilder<T, C> thenSort() {
        return chainToSort(target -> { })
                .range(requireRange());
    }
}