package menu.editor.builders;

import java.util.Objects;
import java.util.function.Consumer;

import menu.DynamicMenu;
import menu.editor.builders.base.AbstractEditBuilder;
import menu.editor.builders.base.InheritanceMode;
import menu.editor.core.RemoveFamily;

/** Builder fluent per a operacions d'eliminació. */
public final class RemoveBuilder<T, C>
        extends AbstractEditBuilder<T, C, RemoveBuilder<T, C>> {

    /** Crea un builder d'eliminació sobre un menú. */
    public RemoveBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    /** Crea un builder amb operacions pendents. */
    public RemoveBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {

        super(menu, pendingPipeline);
    }

    /** Crea un builder amb estat pendent explícit. */
    public RemoveBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline,
            boolean hasPendingOperations) {

        super(menu, pendingPipeline, hasPendingOperations);
    }

    /** Retorna aquesta instància tipada. */
    @Override
    protected RemoveBuilder<T, C> self() {
        return this;
    }

    /** Construeix l'operació actual d'eliminació. */
    private Consumer<DynamicMenu<T, C>> currentOperation() {
        return currentMenu -> RemoveFamily.removeIf(
                currentMenu,
                requireSelector(),
                buildConfig());
    }

    /** Executa tota la cadena pendent i després l'última eliminació. */
    public int execute() {
        applyPendingOperations();
        return RemoveFamily.removeIf(
                menu(),
                Objects.requireNonNull(requireSelector(), "La condició no pot ser nul·la"),
                buildConfig());
    }

    /** Indica si s'ha eliminat almenys una opció. */
    public boolean executeAny() {
        return execute() > 0;
    }

    /** Aplica l'herència d'edició. */
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

    /** Aplica l'herència per a consultes. */
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

    /** Encadena una altra eliminació sense herència. */
    public RemoveBuilder<T, C> thenRemove() {
        return thenRemove(InheritanceMode.NONE);
    }

    /** Encadena una altra eliminació. */
    public RemoveBuilder<T, C> thenRemove(InheritanceMode inheritanceMode) {
        return applyEditInheritance(chainToRemove(currentOperation()), inheritanceMode);
    }

    /** Encadena una substitució sense herència. */
    public ReplaceBuilder<T, C> thenReplace() {
        return thenReplace(InheritanceMode.NONE);
    }

    /** Encadena una substitució. */
    public ReplaceBuilder<T, C> thenReplace(InheritanceMode inheritanceMode) {
        return applyEditInheritance(chainToReplace(currentOperation()), inheritanceMode);
    }

    /** Encadena una ordenació sense herència. */
    public SortBuilder<T, C> thenSort() {
        return thenSort(InheritanceMode.NONE);
    }

    /** Encadena una ordenació. */
    public SortBuilder<T, C> thenSort(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToSort(currentOperation()), inheritanceMode);
    }

    /** Encadena una consulta sense herència. */
    public QueryBuilder<T, C> thenQuery() {
        return thenQuery(InheritanceMode.NONE);
    }

    /** Encadena una consulta. */
    public QueryBuilder<T, C> thenQuery(InheritanceMode inheritanceMode) {
        return applyQueryInheritance(chainToQuery(currentOperation()), inheritanceMode);
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
