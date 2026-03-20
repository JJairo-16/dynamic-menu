package menu.editor.planning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class OperationPlan<T, C> {

    private static final OperationPlan<?, ?> EMPTY = new OperationPlan<>(null);

    private final PlanNode<T, C> head;
    private List<PlannedOperation<T, C>> materialized;

    private OperationPlan(PlanNode<T, C> head) {
        this.head = head;
    }

    @SuppressWarnings("unchecked")
    public static <T, C> OperationPlan<T, C> empty() {
        return (OperationPlan<T, C>) EMPTY;
    }

    public static <T, C> OperationPlan<T, C> of(List<PlannedOperation<T, C>> operations) {
        Objects.requireNonNull(operations, "La llista d'operacions no pot ser nul·la");

        OperationPlan<T, C> plan = empty();
        for (PlannedOperation<T, C> operation : operations) {
            plan = plan.append(operation);
        }
        return plan;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return head == null ? 0 : head.size();
    }

    public List<PlannedOperation<T, C>> operations() {
        if (head == null)
            return List.of();

        if (materialized != null)
            return materialized;

        ArrayList<PlannedOperation<T, C>> list = new ArrayList<>(head.size());

        for (PlanNode<T, C> current = head; current != null; current = current.previous()) {
            list.add(current.operation());
        }

        Collections.reverse(list);
        materialized = List.copyOf(list);
        return materialized;
    }

    public OperationPlan<T, C> append(PlannedOperation<T, C> operation) {
        Objects.requireNonNull(operation, "L'operació no pot ser nul·la");

        if (operation.isNoOp()) {
            return this;
        }

        return new OperationPlan<>(new PlanNode<>(head, operation));
    }
}