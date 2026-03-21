package menu.editor.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import menu.editor.planning.config.OptimizationFamily;
import menu.editor.planning.config.OptimizationProfile;
import menu.editor.planning.interfaces.PlannedOperation;

/** Aplica regles d'optimització sobre plans d'operacions. */
public final class PlanOptimizer {

    private static final OperationAlgebra FAST_APPEND_ALGEBRA =
            new OperationAlgebra(List.of(
                    PlanOptimizer::rewriteNeutralElements,
                    PlanOptimizer::rewriteBarriers,
                    PlanOptimizer::rewriteDominantReorderFast));

    private static final OperationAlgebra FULL_OPTIMIZE_ALGEBRA =
            new OperationAlgebra(List.of(
                    PlanOptimizer::rewriteNeutralElements,
                    PlanOptimizer::rewriteBarriers,
                    PlanOptimizer::rewriteEquivalentStableOperations,
                    PlanOptimizer::rewriteDominantReorderFull));

    private PlanOptimizer() {
    }

    /** Optimitza globalment un pla. */
    public static <T, C> OperationPlan<T, C> optimize(OperationPlan<T, C> plan) {
        Objects.requireNonNull(plan, "El pla no pot ser nul");

        if (plan.isEmpty() || plan.isGloballyOptimized()) {
            return plan;
        }

        List<PlannedOperation<T, C>> source = plan.operations();
        if (source.isEmpty()) {
            return OperationPlan.emptyGloballyOptimized();
        }

        ArrayList<PlannedOperation<T, C>> stack = new ArrayList<>(source.size());

        for (PlannedOperation<T, C> operation : source) {
            reduceInto(stack, operation, FULL_OPTIMIZE_ALGEBRA);
        }

        return OperationPlan.ofOptimized(stack);
    }

    /** Reescriu dues operacions adjacents amb regles ràpides. */
    public static <T, C> RewriteResult<T, C> rewriteAdjacentFast(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        Objects.requireNonNull(previous, "L'operació anterior no pot ser nul·la");
        Objects.requireNonNull(current, "L'operació actual no pot ser nul·la");

        return FAST_APPEND_ALGEBRA.rewrite(previous, current);
    }

    /** Reescriu dues operacions adjacents amb regles completes. */
    public static <T, C> RewriteResult<T, C> rewriteAdjacentFull(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        Objects.requireNonNull(previous, "L'operació anterior no pot ser nul·la");
        Objects.requireNonNull(current, "L'operació actual no pot ser nul·la");

        return FULL_OPTIMIZE_ALGEBRA.rewrite(previous, current);
    }

    /** Redueix una operació dins d'una pila usant un àlgebra de reescriptura. */
    static <T, C> void reduceInto(
            List<PlannedOperation<T, C>> stack,
            PlannedOperation<T, C> current,
            OperationAlgebra algebra) {

        Objects.requireNonNull(stack, "La pila no pot ser nul·la");
        Objects.requireNonNull(current, "L'operació actual no pot ser nul·la");
        Objects.requireNonNull(algebra, "L'àlgebra no pot ser nul·la");

        if (current.isNoOp()) {
            return;
        }

        PlannedOperation<T, C> candidate = current;

        while (!stack.isEmpty()) {
            PlannedOperation<T, C> previous = stack.get(stack.size() - 1);
            RewriteResult<T, C> rewrite = algebra.rewrite(previous, candidate);

            switch (rewrite.kind()) {
                case KEEP_BOTH -> {
                    stack.add(candidate);
                    return;
                }
                case DROP_PREVIOUS -> stack.remove(stack.size() - 1);
                case DROP_CURRENT -> {
                    return;
                }
                case REPLACE_BOTH -> {
                    stack.remove(stack.size() - 1);
                    candidate = rewrite.replacement();

                    if (candidate == null || candidate.isNoOp()) {
                        return;
                    }
                }
            }
        }

        stack.add(candidate);
    }

    /** Elimina operacions neutres adjacents. */
    private static <T, C> RewriteResult<T, C> rewriteNeutralElements(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        if (previous.isNoOp()) {
            return RewriteResult.dropPrevious();
        }

        if (current.isNoOp()) {
            return RewriteResult.dropCurrent();
        }

        return RewriteResult.keepBoth();
    }

    /** Conserva el límit entre operacions que actuen com a barrera. */
    private static <T, C> RewriteResult<T, C> rewriteBarriers(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        OptimizationProfile left = previous.optimizationProfile();
        OptimizationProfile right = current.optimizationProfile();

        if (left.barrier() || right.barrier()) {
            return RewriteResult.keepBoth();
        }

        return RewriteResult.keepBoth();
    }

    /** Simplifica reordenacions dominants en append incremental. */
    private static <T, C> RewriteResult<T, C> rewriteDominantReorderFast(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        OptimizationProfile left = previous.optimizationProfile();
        OptimizationProfile right = current.optimizationProfile();

        if (left.family() != OptimizationFamily.REORDER
                || right.family() != OptimizationFamily.REORDER) {
            return RewriteResult.keepBoth();
        }

        if (!left.isPlainReorder() || !right.isPlainReorder()) {
            return RewriteResult.keepBoth();
        }

        if (!left.sameFamilyAs(right) || !left.sameRangeAs(right)) {
            return RewriteResult.keepBoth();
        }

        return RewriteResult.dropPrevious();
    }

    /** Simplifica reordenacions dominants amb criteris globals conservadors. */
    private static <T, C> RewriteResult<T, C> rewriteDominantReorderFull(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        OptimizationProfile left = previous.optimizationProfile();
        OptimizationProfile right = current.optimizationProfile();

        if (left.family() != OptimizationFamily.REORDER
                || right.family() != OptimizationFamily.REORDER) {
            return RewriteResult.keepBoth();
        }

        if (!left.isPlainReorder() || !right.isPlainReorder()) {
            return RewriteResult.keepBoth();
        }

        if (!left.sameFamilyAs(right) || !left.sameRangeAs(right)) {
            return RewriteResult.keepBoth();
        }

        if (left.isIndexSensitive() || right.isIndexSensitive()) {
            return RewriteResult.keepBoth();
        }

        if (left.isLabelSensitive() || right.isLabelSensitive()) {
            return RewriteResult.keepBoth();
        }

        return RewriteResult.dropPrevious();
    }

    /** Deduplica operacions semànticament equivalents quan és segur. */
    private static <T, C> RewriteResult<T, C> rewriteEquivalentStableOperations(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        if (!previous.supportsSemanticDeduplication()
                || !current.supportsSemanticDeduplication()) {
            return RewriteResult.keepBoth();
        }

        OptimizationProfile left = previous.optimizationProfile();
        OptimizationProfile right = current.optimizationProfile();

        if (!left.isConservativelySafeForSemanticDeduplication()
                || !right.isConservativelySafeForSemanticDeduplication()) {
            return RewriteResult.keepBoth();
        }

        if (!left.sameSemanticWindowAs(right)) {
            return RewriteResult.keepBoth();
        }

        if (!previous.isSemanticallyEquivalentTo(current)) {
            return RewriteResult.keepBoth();
        }

        return RewriteResult.dropCurrent();
    }

    /** Regla elemental d'una àlgebra de reescriptura. */
    @FunctionalInterface
    static interface AlgebraRule {

        /** Aplica la regla sobre dues operacions adjacents. */
        <T, C> RewriteResult<T, C> apply(
                PlannedOperation<T, C> previous,
                PlannedOperation<T, C> current);
    }

    /** Conjunt ordenat de regles de reescriptura. */
    static final class OperationAlgebra {

        private final List<AlgebraRule> rules;

        /** Crea una àlgebra a partir d'una llista de regles. */
        private OperationAlgebra(List<AlgebraRule> rules) {
            this.rules = List.copyOf(rules);
        }

        /** Aplica les regles fins a obtenir una decisió. */
        private <T, C> RewriteResult<T, C> rewrite(
                PlannedOperation<T, C> previous,
                PlannedOperation<T, C> current) {

            for (AlgebraRule rule : rules) {
                RewriteResult<T, C> result = rule.apply(previous, current);
                if (result.kind() != RewriteKind.KEEP_BOTH) {
                    return result;
                }
            }

            return RewriteResult.keepBoth();
        }
    }

    /** Tipus de resultat d'una reescriptura. */
    public enum RewriteKind {
        KEEP_BOTH,
        DROP_PREVIOUS,
        DROP_CURRENT,
        REPLACE_BOTH
    }

    /** Resultat d'una reescriptura entre dues operacions. */
    public static record RewriteResult<T, C>(
            RewriteKind kind,
            PlannedOperation<T, C> replacement) {

        /** Valida el tipus de resultat. */
        public RewriteResult {
            Objects.requireNonNull(kind, "El tipus de rewrite no pot ser nul");
        }

        /** Manté totes dues operacions. */
        public static <T, C> RewriteResult<T, C> keepBoth() {
            return new RewriteResult<>(RewriteKind.KEEP_BOTH, null);
        }

        /** Elimina l'operació anterior. */
        public static <T, C> RewriteResult<T, C> dropPrevious() {
            return new RewriteResult<>(RewriteKind.DROP_PREVIOUS, null);
        }

        /** Elimina l'operació actual. */
        public static <T, C> RewriteResult<T, C> dropCurrent() {
            return new RewriteResult<>(RewriteKind.DROP_CURRENT, null);
        }

        /** Substitueix totes dues operacions per una de nova. */
        public static <T, C> RewriteResult<T, C> replaceBoth(
                PlannedOperation<T, C> replacement) {
            return new RewriteResult<>(
                    RewriteKind.REPLACE_BOTH,
                    Objects.requireNonNull(replacement, "L'operació de reemplaçament no pot ser nul·la"));
        }
    }
}