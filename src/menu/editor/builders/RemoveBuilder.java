package menu.editor.builders;

import java.util.Objects;
import java.util.function.Consumer;

import menu.DynamicMenu;
import menu.editor.core.RemoveFamily;

/**
 * Builder fluent per a operacions d'eliminació.
 */
public final class RemoveBuilder<T, C>
        extends AbstractEditBuilder<T, C, RemoveBuilder<T, C>> {

    public RemoveBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    RemoveBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {

        super(menu, pendingPipeline);
    }

    RemoveBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline,
            boolean hasPendingOperations) {

        super(menu, pendingPipeline, hasPendingOperations);
    }

    @Override
    protected RemoveBuilder<T, C> self() {
        return this;
    }

    private Consumer<DynamicMenu<T, C>> currentOperation() {
        return currentMenu -> RemoveFamily.removeIf(
                currentMenu,
                requireSelector(),
                buildConfig());
    }

    /**
     * Executa tota la cadena pendent i després l'última eliminació.
     */
    public int execute() {
        applyPendingOperations();
        return RemoveFamily.removeIf(
                menu(),
                Objects.requireNonNull(requireSelector(), "La condició no pot ser nul·la"),
                buildConfig());
    }

    /**
     * Indica si s'ha eliminat almenys una opció.
     */
    public boolean executeAny() {
        return execute() > 0;
    }

    public RemoveBuilder<T, C> thenRemove() {
        return chainToRemove(currentOperation());
    }

    public ReplaceBuilder<T, C> thenReplace() {
        return chainToReplace(currentOperation());
    }

    public SortBuilder<T, C> thenSort() {
        return chainToSort(currentOperation());
    }

    public QueryBuilder<T, C> thenQuery() {
        return chainToQuery(currentOperation())
                .where(requireSelector())
                .range(requireRange());
    }
}