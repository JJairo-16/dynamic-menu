package menu.editor.builders.base;

import java.util.Objects;

import menu.DynamicMenu;
import menu.editor.builders.QueryBuilder;
import menu.editor.builders.RemoveBuilder;
import menu.editor.builders.ReplaceBuilder;
import menu.editor.builders.ShuffleBuilder;
import menu.editor.builders.SortBuilder;
import menu.editor.planning.OperationPlan;
import menu.editor.planning.PlanExecutor;
import menu.editor.planning.interfaces.PlannedOperation;

/**
 * Pare mínim per a tots els builders encadenables.
 *
 * <p>Centralitza:
 * <ul>
 *   <li>menú objectiu</li>
 *   <li>pla pendent compost</li>
 *   <li>helpers de chaining</li>
 *   <li>hook de canvi d'estat</li>
 * </ul>
 *
 * @param <T> tipus de retorn del menú
 * @param <C> tipus del context del menú
 * @param <S> tipus concret del builder fluent
 */
public abstract class AbstractChainableMenuBuilder<
        T, C,
        S extends AbstractChainableMenuBuilder<T, C, S>> {

    private final DynamicMenu<T, C> menu;
    private final OperationPlan<T, C> pendingPlan;
    private final boolean hasPendingOperations;

    /**
     * Crea un builder sense operacions pendents.
     *
     * @param menu menú objectiu
     */
    protected AbstractChainableMenuBuilder(DynamicMenu<T, C> menu) {
        this(menu, OperationPlan.empty(), false);
    }

    /**
     * Crea un builder amb pla pendent.
     *
     * @param menu menú objectiu
     * @param pendingPlan pla pendent
     */
    protected AbstractChainableMenuBuilder(
            DynamicMenu<T, C> menu,
            OperationPlan<T, C> pendingPlan) {

        this(menu, pendingPlan, pendingPlan != null && !pendingPlan.isEmpty());
    }

    /**
     * Crea un builder amb control explícit d'estat pendent.
     *
     * @param menu menú objectiu
     * @param pendingPlan pla pendent
     * @param hasPendingOperations indica si hi ha operacions pendents
     */
    protected AbstractChainableMenuBuilder(
            DynamicMenu<T, C> menu,
            OperationPlan<T, C> pendingPlan,
            boolean hasPendingOperations) {

        this.menu = Objects.requireNonNull(menu, "El menú no pot ser nul");
        this.pendingPlan = Objects.requireNonNull(
                pendingPlan,
                "El pla d'operacions no pot ser nul");
        this.hasPendingOperations = hasPendingOperations;
    }

    /**
     * Retorna aquest builder amb el tipus concret.
     *
     * @return aquest builder
     */
    protected abstract S self();

    /** Hook perquè els fills invalidin estat intern si cal. */
    protected void onStateChanged() {
    }

    /**
     * Retorna el menú objectiu.
     *
     * @return menú objectiu
     */
    public final DynamicMenu<T, C> menu() {
        return menu;
    }

    /**
     * Indica si hi ha operacions pendents.
     *
     * @return {@code true} si n'hi ha
     */
    protected final boolean hasPendingOperations() {
        return hasPendingOperations;
    }

    /**
     * Retorna el pla pendent actual.
     *
     * @return pla pendent
     */
    protected final OperationPlan<T, C> pendingPlan() {
        return pendingPlan;
    }

    /** Aplica les operacions pendents sobre el menú real. */
    protected final void applyPendingOperations() {
        applyPendingOperationsOn(menu);
    }

    /**
     * Aplica les operacions pendents sobre un menú indicat.
     *
     * @param target menú on aplicar-les
     */
    protected final void applyPendingOperationsOn(DynamicMenu<T, C> target) {
        Objects.requireNonNull(target, "El menú objectiu no pot ser nul");
        if (!hasPendingOperations) {
            return;
        }
        PlanExecutor.execute(pendingPlan, target);
    }

    /**
     * Afegeix una operació al final del pla pendent.
     *
     * @param currentOperation operació actual
     * @return pla compost
     */
    protected final OperationPlan<T, C> pendingPlus(
            PlannedOperation<T, C> currentOperation) {

        Objects.requireNonNull(currentOperation, "L'operació actual no pot ser nul·la");

        if (!hasPendingOperations) {
            return OperationPlan.<T, C>empty().appendOptimized(currentOperation);
        }

        return pendingPlan.appendOptimized(currentOperation);
    }

    /**
     * Encadena cap a {@link RemoveBuilder}.
     *
     * @param currentOperation operació actual
     * @return nou builder
     */
    protected final RemoveBuilder<T, C> chainToRemove(
            PlannedOperation<T, C> currentOperation) {

        return new RemoveBuilder<>(menu(), pendingPlus(currentOperation), true);
    }

    /**
     * Encadena cap a {@link ReplaceBuilder}.
     *
     * @param currentOperation operació actual
     * @return nou builder
     */
    protected final ReplaceBuilder<T, C> chainToReplace(
            PlannedOperation<T, C> currentOperation) {

        return new ReplaceBuilder<>(menu(), pendingPlus(currentOperation), true);
    }

    /**
     * Encadena cap a {@link SortBuilder}.
     *
     * @param currentOperation operació actual
     * @return nou builder
     */
    protected final SortBuilder<T, C> chainToSort(
            PlannedOperation<T, C> currentOperation) {

        return new SortBuilder<>(menu(), pendingPlus(currentOperation), true);
    }

    /**
     * Encadena cap a {@link ShuffleBuilder}.
     *
     * @param currentOperation operació actual
     * @return nou builder
     */
    protected final ShuffleBuilder<T, C> chainToShuffle(
            PlannedOperation<T, C> currentOperation) {

        return new ShuffleBuilder<>(menu(), pendingPlus(currentOperation), true);
    }

    /**
     * Encadena cap a {@link QueryBuilder}.
     *
     * @param currentOperation operació actual
     * @return nou builder
     */
    protected final QueryBuilder<T, C> chainToQuery(
            PlannedOperation<T, C> currentOperation) {

        return new QueryBuilder<>(menu(), pendingPlus(currentOperation), true);
    }
}