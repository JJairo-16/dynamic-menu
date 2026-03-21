package menu.editor.planning;

import java.util.Objects;

import menu.DynamicMenu;
import menu.editor.planning.interfaces.PlannedOperation;

/** Executa un pla d'operacions sobre un menú. */
public final class PlanExecutor {

    private PlanExecutor() {
    }

    /** Executa el pla sobre el menú donat. */
    public static <T, C> void execute(
            OperationPlan<T, C> plan,
            DynamicMenu<T, C> menu) {

        Objects.requireNonNull(plan, "El pla no pot ser nul");
        Objects.requireNonNull(menu, "El menú no pot ser nul");

        OperationPlan<T, C> executable = selectPlan(plan);

        for (PlannedOperation<T, C> operation : executable.operations()) {
            operation.apply(menu);
        }
    }

    /** Selecciona el pla a executar, aplicant optimització global si cal. */
    private static <T, C> OperationPlan<T, C> selectPlan(OperationPlan<T, C> plan) {
        if (plan.size() < 3 || plan.isGloballyOptimized())
            return plan;

        return PlanOptimizer.optimize(plan);
    }
}