package menu.editor.planning;

import java.util.Objects;

import menu.editor.planning.interfaces.PlannedOperation;

/**
 * Node intern d'un pla d'operacions.
 *
 * <p>Representa una llista enllaçada immutable.
 */
final class PlanNode<T, C> {

    private final PlanNode<T, C> previous;
    private final PlannedOperation<T, C> operation;
    private final int size;

    /** Crea un node amb referència al node anterior. */
    PlanNode(PlanNode<T, C> previous, PlannedOperation<T, C> operation) {
        this.previous = previous;
        this.operation = Objects.requireNonNull(operation, "L'operació no pot ser nul·la");
        this.size = previous == null ? 1 : previous.size + 1;
    }

    /** Retorna el node anterior. */
    PlanNode<T, C> previous() {
        return previous;
    }

    /** Retorna l'operació associada. */
    PlannedOperation<T, C> operation() {
        return operation;
    }

    /** Retorna la mida acumulada fins a aquest node. */
    int size() {
        return size;
    }
}