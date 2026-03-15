package menu.editor.builders;

import java.util.Objects;
import java.util.function.Consumer;

import menu.DynamicMenu;
/**
 * Pare mínim per a tots els builders encadenables.
 *
 * <p>Centralitza:
 * <ul>
 *   <li>menú objectiu</li>
 *   <li>pipeline pendent compost</li>
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
    private final Consumer<DynamicMenu<T, C>> pendingPipeline;
    private final boolean hasPendingOperations;

    protected AbstractChainableMenuBuilder(DynamicMenu<T, C> menu) {
        this(menu, target -> { }, false);
    }

    protected AbstractChainableMenuBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {
        this(menu, pendingPipeline, true);
    }

    protected AbstractChainableMenuBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline,
            boolean hasPendingOperations) {

        this.menu = Objects.requireNonNull(menu, "El menú no pot ser nul");
        this.pendingPipeline = Objects.requireNonNull(
                pendingPipeline,
                "La cadena d'operacions no pot ser nul·la");
        this.hasPendingOperations = hasPendingOperations;
    }

    /**
     * Retorna aquest builder amb el tipus concret.
     */
    protected abstract S self();

    /**
     * Hook de canvi d'estat perquè els fills invalidin cachés si ho necessiten.
     */
    protected void onStateChanged() {
    }

    /**
     * Retorna el menú objectiu.
     */
    public final DynamicMenu<T, C> menu() {
        return menu;
    }

    /**
     * Indica si hi ha operacions pendents.
     */
    protected final boolean hasPendingOperations() {
        return hasPendingOperations;
    }

    /**
     * Retorna el pipeline pendent actual.
     */
    protected final Consumer<DynamicMenu<T, C>> pendingPipeline() {
        return pendingPipeline;
    }

    /**
     * Aplica les operacions pendents sobre el menú real.
     */
    protected final void applyPendingOperations() {
        applyPendingOperationsOn(menu);
    }

    /**
     * Aplica les operacions pendents sobre un menú indicat.
     */
    protected final void applyPendingOperationsOn(DynamicMenu<T, C> target) {
        Objects.requireNonNull(target, "El menú objectiu no pot ser nul");
        if (!hasPendingOperations) {
            return;
        }
        pendingPipeline.accept(target);
    }

    /**
     * Retorna el pipeline pendent actual amb l'operació indicada al final.
     */
    protected final Consumer<DynamicMenu<T, C>> pendingPlus(
            Consumer<DynamicMenu<T, C>> currentOperation) {

        Objects.requireNonNull(currentOperation, "L'operació actual no pot ser nul·la");

        if (!hasPendingOperations) {
            return currentOperation;
        }

        return pendingPipeline.andThen(currentOperation);
    }

    /**
     * Helper per encadenar cap a RemoveBuilder.
     */
    protected final RemoveBuilder<T, C> chainToRemove(
            Consumer<DynamicMenu<T, C>> currentOperation) {

        return new RemoveBuilder<>(menu(), pendingPlus(currentOperation), true);
    }

    /**
     * Helper per encadenar cap a ReplaceBuilder.
     */
    protected final ReplaceBuilder<T, C> chainToReplace(
            Consumer<DynamicMenu<T, C>> currentOperation) {

        return new ReplaceBuilder<>(menu(), pendingPlus(currentOperation), true);
    }

    /**
     * Helper per encadenar cap a SortBuilder.
     */
    protected final SortBuilder<T, C> chainToSort(
            Consumer<DynamicMenu<T, C>> currentOperation) {

        return new SortBuilder<>(menu(), pendingPlus(currentOperation), true);
    }

    /**
     * Helper per encadenar cap a QueryBuilder.
     */
    protected final QueryBuilder<T, C> chainToQuery(
            Consumer<DynamicMenu<T, C>> currentOperation) {

        return new QueryBuilder<>(menu(), pendingPlus(currentOperation), true);
    }
}