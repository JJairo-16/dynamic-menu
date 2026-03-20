package menu.editor.planning;

import java.util.Objects;

final class PlanNode<T, C> {

    private final PlanNode<T, C> previous;
    private final PlannedOperation<T, C> operation;
    private final int size;

    PlanNode(PlanNode<T, C> previous, PlannedOperation<T, C> operation) {
        this.previous = previous;
        this.operation = Objects.requireNonNull(operation, "L'operació no pot ser nul·la");
        this.size = previous == null ? 1 : previous.size + 1;
    }

    PlanNode<T, C> previous() {
        return previous;
    }

    PlannedOperation<T, C> operation() {
        return operation;
    }

    int size() {
        return size;
    }
}