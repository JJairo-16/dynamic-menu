package menu.editor.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import menu.editor.planning.operations.ShufflePlannedOperation;
import menu.editor.planning.operations.SortPlannedOperation;

public final class PlanOptimizer {

    private PlanOptimizer() {
    }

    public static <T, C> OperationPlan<T, C> optimize(OperationPlan<T, C> plan) {
        Objects.requireNonNull(plan, "El pla no pot ser nul");

        List<PlannedOperation<T, C>> optimized = new ArrayList<>();

        for (PlannedOperation<T, C> current : plan.operations()) {
            if (current == null || current.isNoOp()) {
                continue;
            }

            if (!optimized.isEmpty()) {
                PlannedOperation<T, C> previous = optimized.get(optimized.size() - 1);

                if (canDropPrevious(previous, current)) {
                    optimized.remove(optimized.size() - 1);
                }
            }

            optimized.add(current);
        }

        return OperationPlan.of(optimized);
    }

    private static <T, C> boolean canDropPrevious(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        if (!(previous instanceof ShufflePlannedOperation<T, C> shuffle)) {
            return false;
        }

        if (!(current instanceof SortPlannedOperation<T, C> sort)) {
            return false;
        }

        if (!Objects.equals(shuffle.range(), sort.range())) {
            return false;
        }

        if (shuffle.hasPinnedSelectors()) {
            return false;
        }

        return !sort.hasPinnedSelectors();
    }
}