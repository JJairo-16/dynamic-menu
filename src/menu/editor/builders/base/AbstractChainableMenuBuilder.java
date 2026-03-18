package menu.editor.builders.base;

import java.util.Objects;
import java.util.function.Consumer;

import menu.DynamicMenu;
import menu.editor.builders.QueryBuilder;
import menu.editor.builders.RemoveBuilder;
import menu.editor.builders.ReplaceBuilder;
import menu.editor.builders.ShuffleBuilder;
import menu.editor.builders.SortBuilder;

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

    /**
     * Crea un builder sense operacions pendents.
     *
     * @param menu menú objectiu
     */
    protected AbstractChainableMenuBuilder(DynamicMenu<T, C> menu) {
        this(menu, target -> { }, false);
    }

    /**
     * Crea un builder amb pipeline pendent.
     *
     * @param menu menú objectiu
     * @param pendingPipeline pipeline pendent
     */
    protected AbstractChainableMenuBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {
        this(menu, pendingPipeline, true);
    }

    /**
     * Crea un builder amb control explícit d'estat pendent.
     *
     * @param menu menú objectiu
     * @param pendingPipeline pipeline pendent
     * @param hasPendingOperations indica si hi ha operacions pendents
     */
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
     * Retorna el pipeline pendent actual.
     *
     * @return pipeline pendent
     */
    protected final Consumer<DynamicMenu<T, C>> pendingPipeline() {
        return pendingPipeline;
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
        pendingPipeline.accept(target);
    }

    /**
     * Afegeix una operació al final del pipeline pendent.
     *
     * @param currentOperation operació actual
     * @return pipeline compost
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
     * Encadena cap a {@link RemoveBuilder}.
     *
     * @param currentOperation operació actual
     * @return nou builder
     */
    protected final RemoveBuilder<T, C> chainToRemove(
            Consumer<DynamicMenu<T, C>> currentOperation) {

        return new RemoveBuilder<>(menu(), pendingPlus(currentOperation), true);
    }

    /**
     * Encadena cap a {@link ReplaceBuilder}.
     *
     * @param currentOperation operació actual
     * @return nou builder
     */
    protected final ReplaceBuilder<T, C> chainToReplace(
            Consumer<DynamicMenu<T, C>> currentOperation) {

        return new ReplaceBuilder<>(menu(), pendingPlus(currentOperation), true);
    }

    /**
     * Encadena cap a {@link SortBuilder}.
     *
     * @param currentOperation operació actual
     * @return nou builder
     */
    protected final SortBuilder<T, C> chainToSort(
            Consumer<DynamicMenu<T, C>> currentOperation) {

        return new SortBuilder<>(menu(), pendingPlus(currentOperation), true);
    }

    /**
     * Encadena cap a {@link ShuffleBuilder}.
     *
     * @param currentOperation operació actual
     * @return nou builder
     */
    protected final ShuffleBuilder<T, C> chainToShuffle(
            Consumer<DynamicMenu<T, C>> currentOperation) {

        return new ShuffleBuilder<>(menu(), pendingPlus(currentOperation), true);
    }

    /**
     * Encadena cap a {@link QueryBuilder}.
     *
     * @param currentOperation operació actual
     * @return nou builder
     */
    protected final QueryBuilder<T, C> chainToQuery(
            Consumer<DynamicMenu<T, C>> currentOperation) {

        return new QueryBuilder<>(menu(), pendingPlus(currentOperation), true);
    }
}