package menu.editor.builders;

import java.util.Objects;
import java.util.function.Consumer;

import menu.DynamicMenu;
import menu.editor.builders.base.AbstractEditBuilder;
import menu.editor.builders.base.InheritanceMode;
import menu.editor.core.RemoveFamily;

/**
 * Builder fluent per a operacions d'eliminació.
 */
public final class RemoveBuilder<T, C>
        extends AbstractEditBuilder<T, C, RemoveBuilder<T, C>> {

    public RemoveBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    public RemoveBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {

        super(menu, pendingPipeline);
    }

    public RemoveBuilder(
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

    private <B extends AbstractEditBuilder<T, C, B>> B applyEditInheritance(
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
                return inheritEditStateTo(target);
            default:
                throw new IllegalArgumentException("Mode d'herència no suportat: " + inheritanceMode);
        }
    }

    private QueryBuilder<T, C> applyQueryInheritance(
            QueryBuilder<T, C> target,
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

    private SortBuilder<T, C> applySortInheritance(
            SortBuilder<T, C> target,
            InheritanceMode inheritanceMode) {

        Objects.requireNonNull(inheritanceMode, "El mode d'herència no pot ser nul");

        switch (inheritanceMode) {
            case NONE:
                return target;
            case RANGE:
                return inheritRangeTo(target);
            case ALL:
                return inheritRangeTo(target);
            case SELECTION:
                throw new IllegalArgumentException(
                        "SortBuilder no admet herència de selector; usa RANGE o ALL");
            default:
                throw new IllegalArgumentException("Mode d'herència no suportat: " + inheritanceMode);
        }
    }

    public RemoveBuilder<T, C> thenRemove() {
        return thenRemove(InheritanceMode.NONE);
    }

    public RemoveBuilder<T, C> thenRemove(InheritanceMode inheritanceMode) {
        return applyEditInheritance(chainToRemove(currentOperation()), inheritanceMode);
    }

    public ReplaceBuilder<T, C> thenReplace() {
        return thenReplace(InheritanceMode.NONE);
    }

    public ReplaceBuilder<T, C> thenReplace(InheritanceMode inheritanceMode) {
        return applyEditInheritance(chainToReplace(currentOperation()), inheritanceMode);
    }

    public SortBuilder<T, C> thenSort() {
        return thenSort(InheritanceMode.NONE);
    }

    public SortBuilder<T, C> thenSort(InheritanceMode inheritanceMode) {
        return applySortInheritance(chainToSort(currentOperation()), inheritanceMode);
    }

    public QueryBuilder<T, C> thenQuery() {
        return thenQuery(InheritanceMode.NONE);
    }

    public QueryBuilder<T, C> thenQuery(InheritanceMode inheritanceMode) {
        return applyQueryInheritance(chainToQuery(currentOperation()), inheritanceMode);
    }
}