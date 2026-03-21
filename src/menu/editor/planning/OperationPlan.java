package menu.editor.planning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import menu.editor.planning.config.PlanNormalizationState;
import menu.editor.planning.interfaces.PlannedOperation;

/** Representa un pipeline immutable d'operacions planificades. */
public final class OperationPlan<T, C> {

    private static final OperationPlan<?, ?> EMPTY_RAW =
            new OperationPlan<>(null, PlanNormalizationState.RAW);

    private static final OperationPlan<?, ?> EMPTY_GLOBAL =
            new OperationPlan<>(null, PlanNormalizationState.GLOBALLY_OPTIMIZED);

    private final PlanNode<T, C> head;
    private final PlanNormalizationState normalizationState;
    private List<PlannedOperation<T, C>> materialized;

    /** Crea un pla a partir d'un node inicial i un estat de normalització. */
    private OperationPlan(
            PlanNode<T, C> head,
            PlanNormalizationState normalizationState) {

        this.head = head;
        this.normalizationState = Objects.requireNonNull(
                normalizationState,
                "L'estat de normalització no pot ser nul");
    }

    /** Retorna un pla buit no optimitzat. */
    @SuppressWarnings("unchecked")
    public static <T, C> OperationPlan<T, C> empty() {
        return (OperationPlan<T, C>) EMPTY_RAW;
    }

    /** Retorna un pla buit ja optimitzat globalment. */
    @SuppressWarnings("unchecked")
    public static <T, C> OperationPlan<T, C> emptyGloballyOptimized() {
        return (OperationPlan<T, C>) EMPTY_GLOBAL;
    }

    /** Crea un pla a partir d'una llista d'operacions. */
    public static <T, C> OperationPlan<T, C> of(List<PlannedOperation<T, C>> operations) {
        Objects.requireNonNull(operations, "La llista d'operacions no pot ser nul·la");

        OperationPlan<T, C> plan = empty();
        for (PlannedOperation<T, C> operation : operations) {
            plan = plan.append(operation);
        }
        return plan;
    }

    /** Crea un pla ja optimitzat a partir d'una llista d'operacions. */
    public static <T, C> OperationPlan<T, C> ofOptimized(List<PlannedOperation<T, C>> operations) {
        Objects.requireNonNull(operations, "La llista d'operacions no pot ser nul·la");

        OperationPlan<T, C> plan = emptyGloballyOptimized();
        for (PlannedOperation<T, C> operation : operations) {
            if (operation == null || operation.isNoOp()) {
                continue;
            }
            plan = new OperationPlan<>(
                    new PlanNode<>(plan.head, operation),
                    PlanNormalizationState.GLOBALLY_OPTIMIZED);
        }
        return plan;
    }

    /** Indica si el pla és buit. */
    public boolean isEmpty() {
        return head == null;
    }

    /** Indica si el pla està optimitzat globalment. */
    public boolean isGloballyOptimized() {
        return normalizationState == PlanNormalizationState.GLOBALLY_OPTIMIZED;
    }

    /** Indica si el pla està optimitzat localment. */
    public boolean isLocallyOptimized() {
        return normalizationState == PlanNormalizationState.LOCALLY_OPTIMIZED;
    }

    /** Retorna el nombre d'operacions del pla. */
    public int size() {
        return head == null ? 0 : head.size();
    }

    /** Retorna les operacions materialitzades en ordre d'execució. */
    public List<PlannedOperation<T, C>> operations() {
        if (head == null) {
            return List.of();
        }

        if (materialized != null) {
            return materialized;
        }

        ArrayList<PlannedOperation<T, C>> list = new ArrayList<>(head.size());

        for (PlanNode<T, C> current = head; current != null; current = current.previous()) {
            list.add(current.operation());
        }

        Collections.reverse(list);
        materialized = List.copyOf(list);
        return materialized;
    }

    /** Afegeix una operació al pla sense optimització. */
    public OperationPlan<T, C> append(PlannedOperation<T, C> operation) {
        Objects.requireNonNull(operation, "L'operació no pot ser nul·la");

        if (operation.isNoOp()) {
            return this;
        }

        return new OperationPlan<>(
                new PlanNode<>(head, operation),
                PlanNormalizationState.RAW);
    }

    /**
     * Afegeix una operació amb optimització incremental.
     *
     * <p>Només aplica regles locals ràpides. L'optimització global es deixa
     * per abans de l'execució.
     */
    public OperationPlan<T, C> appendOptimized(PlannedOperation<T, C> operation) {
        Objects.requireNonNull(operation, "L'operació no pot ser nul·la");

        if (operation.isNoOp()) {
            return this;
        }

        if (head == null) {
            return new OperationPlan<>(
                    new PlanNode<>(null, operation),
                    PlanNormalizationState.LOCALLY_OPTIMIZED);
        }

        PlannedOperation<T, C> previous = head.operation();
        PlanOptimizer.RewriteResult<T, C> rewrite =
                PlanOptimizer.rewriteAdjacentFast(previous, operation);

        return switch (rewrite.kind()) {
            case KEEP_BOTH -> new OperationPlan<>(
                    new PlanNode<>(head, operation),
                    PlanNormalizationState.LOCALLY_OPTIMIZED);

            case DROP_PREVIOUS -> new OperationPlan<>(
                    new PlanNode<>(head.previous(), operation),
                    PlanNormalizationState.LOCALLY_OPTIMIZED);

            case DROP_CURRENT -> this;

            case REPLACE_BOTH -> new OperationPlan<>(
                    new PlanNode<>(
                            head.previous(),
                            Objects.requireNonNull(
                                    rewrite.replacement(),
                                    "La reescriptura ha retornat una operació nul·la")),
                    PlanNormalizationState.LOCALLY_OPTIMIZED);
        };
    }
}