package menu.editor.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class OperationPlan<T, C> {

    private static final OperationPlan<?, ?> EMPTY = new OperationPlan<>(List.of());

    private final List<PlannedOperation<T, C>> operations;

    public OperationPlan() {
        this(List.of());
    }

    private OperationPlan(List<PlannedOperation<T, C>> operations) {
        this.operations = List.copyOf(operations);
    }

    @SuppressWarnings("unchecked")
    public static <T, C> OperationPlan<T, C> empty() {
        return (OperationPlan<T, C>) EMPTY;
    }

    public static <T, C> OperationPlan<T, C> of(List<PlannedOperation<T, C>> operations) {
        Objects.requireNonNull(operations, "La llista d'operacions no pot ser nul·la");
        return operations.isEmpty() ? empty() : new OperationPlan<>(operations);
    }

    public boolean isEmpty() {
        return operations.isEmpty();
    }

    public List<PlannedOperation<T, C>> operations() {
        return operations;
    }

    public OperationPlan<T, C> append(PlannedOperation<T, C> operation) {
        Objects.requireNonNull(operation, "L'operació no pot ser nul·la");

        if (operation.isNoOp()) {
            return this;
        }

        List<PlannedOperation<T, C>> copy = new ArrayList<>(operations);
        copy.add(operation);
        return new OperationPlan<>(copy);
    }
}