package menu.editor.planning;

import java.util.Objects;
import menu.DynamicMenu;

public final class PlanExecutor {
    private PlanExecutor() {}

    public static <T, C> void execute(
            OperationPlan<T, C> plan,
            DynamicMenu<T, C> menu) {

        Objects.requireNonNull(plan);
        Objects.requireNonNull(menu);

        OperationPlan<T, C> optimitzed = PlanOptimizer.optimize(plan);

        for (PlannedOperation<T, C> op : optimitzed.operations()) {
            op.apply(menu);
        }
    }
}