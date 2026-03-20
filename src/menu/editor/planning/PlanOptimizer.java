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

        if (plan.isEmpty() || plan.size() < 2)
            return plan;

        List<PlannedOperation<T, C>> current = removeNoOps(plan.operations());

        boolean changed;
        do {
            PassResult<T, C> pass = optimizePass(current);
            current = pass.operations();
            changed = pass.changed();
        } while (changed);

        return OperationPlan.of(current);
    }

    private static <T, C> PassResult<T, C> optimizePass(
            List<PlannedOperation<T, C>> input) {

        List<PlannedOperation<T, C>> optimized = new ArrayList<>(input.size());
        boolean changed = false;

        for (PlannedOperation<T, C> current : input) {
            if (optimized.isEmpty()) {
                optimized.add(current);
                continue;
            }

            PlannedOperation<T, C> previous = optimized.get(optimized.size() - 1);
            RewriteResult<T, C> rewrite = rewrite(previous, current);

            switch (rewrite.kind()) {
                case KEEP_BOTH -> optimized.add(current);

                case DROP_PREVIOUS -> {
                    optimized.remove(optimized.size() - 1);
                    optimized.add(current);
                    changed = true;
                }

                case DROP_CURRENT -> changed = true;

                case REPLACE_BOTH -> {
                    optimized.remove(optimized.size() - 1);
                    optimized.add(rewrite.replacement());
                    changed = true;
                }
            }
        }

        return new PassResult<>(List.copyOf(optimized), changed);
    }

    private static <T, C> RewriteResult<T, C> rewrite(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        RewriteResult<T, C> result;

        result = rewriteShuffleSort(previous, current);
        if (result.kind() != RewriteKind.KEEP_BOTH) {
            return result;
        }

        result = rewriteSortSort(previous, current);
        if (result.kind() != RewriteKind.KEEP_BOTH) {
            return result;
        }

        result = rewriteShuffleShuffle(previous, current);
        if (result.kind() != RewriteKind.KEEP_BOTH) {
            return result;
        }

        return RewriteResult.keepBoth();
    }

    private static <T, C> RewriteResult<T, C> rewriteShuffleSort(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        if (!sameType(previous, OperationType.SHUFFLE)
                || !sameType(current, OperationType.SORT)) {
            return RewriteResult.keepBoth();
        }

        if (!isPlainReorder(previous) || !isPlainReorder(current)) {
            return RewriteResult.keepBoth();
        }

        if (!sameRange(previous, current)) {
            return RewriteResult.keepBoth();
        }

        ShufflePlannedOperation<T, C> shuffle = (ShufflePlannedOperation<T, C>) previous;
        SortPlannedOperation<T, C> sort = (SortPlannedOperation<T, C>) current;

        if (shuffle.hasPinnedSelectors() || sort.hasPinnedSelectors()) {
            return RewriteResult.keepBoth();
        }

        return RewriteResult.dropPrevious();
    }

    private static <T, C> RewriteResult<T, C> rewriteSortSort(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        if (!sameType(previous, OperationType.SORT)
                || !sameType(current, OperationType.SORT)) {
            return RewriteResult.keepBoth();
        }

        if (!isPlainReorder(previous) || !isPlainReorder(current)) {
            return RewriteResult.keepBoth();
        }

        if (!sameRange(previous, current)) {
            return RewriteResult.keepBoth();
        }

        SortPlannedOperation<T, C> first = (SortPlannedOperation<T, C>) previous;
        SortPlannedOperation<T, C> second = (SortPlannedOperation<T, C>) current;

        if (first.hasPinnedSelectors() || second.hasPinnedSelectors()) {
            return RewriteResult.keepBoth();
        }

        return RewriteResult.dropPrevious();
    }

    private static <T, C> RewriteResult<T, C> rewriteShuffleShuffle(
            PlannedOperation<T, C> previous,
            PlannedOperation<T, C> current) {

        if (!sameType(previous, OperationType.SHUFFLE)
                || !sameType(current, OperationType.SHUFFLE)) {
            return RewriteResult.keepBoth();
        }

        if (!isPlainReorder(previous) || !isPlainReorder(current)) {
            return RewriteResult.keepBoth();
        }

        if (!sameRange(previous, current)) {
            return RewriteResult.keepBoth();
        }

        ShufflePlannedOperation<T, C> first = (ShufflePlannedOperation<T, C>) previous;
        ShufflePlannedOperation<T, C> second = (ShufflePlannedOperation<T, C>) current;

        if (first.hasPinnedSelectors() || second.hasPinnedSelectors()) {
            return RewriteResult.keepBoth();
        }

        return RewriteResult.dropPrevious();
    }

    private static <T, C> List<PlannedOperation<T, C>> removeNoOps(
            List<PlannedOperation<T, C>> operations) {

        Objects.requireNonNull(operations, "La llista d'operacions no pot ser nul·la");

        List<PlannedOperation<T, C>> filtered = new ArrayList<>(operations.size());

        for (PlannedOperation<T, C> op : operations) {
            if (op == null || op.isNoOp()) {
                continue;
            }
            filtered.add(op);
        }

        return filtered;
    }

    private static boolean sameRange(
            PlannedOperation<?, ?> left,
            PlannedOperation<?, ?> right) {
        return Objects.equals(left.range(), right.range());
    }

    private static boolean sameType(
            PlannedOperation<?, ?> op,
            OperationType type) {
        return op.type() == type;
    }

    private static boolean isPlainReorder(PlannedOperation<?, ?> op) {
        return op.reorders()
                && op.preservesCardinality()
                && !op.hasPinnedSelectors();
    }

    private enum RewriteKind {
        KEEP_BOTH,
        DROP_PREVIOUS,
        DROP_CURRENT,
        REPLACE_BOTH
    }

    private static record RewriteResult<T, C>(RewriteKind kind, PlannedOperation<T, C> replacement) {
        private RewriteResult {
            Objects.requireNonNull(kind, "El tipus de reescriptura no pot ser nul");
        }

        public static <T, C> RewriteResult<T, C> keepBoth() {
            return new RewriteResult<>(RewriteKind.KEEP_BOTH, null);
        }

        public static <T, C> RewriteResult<T, C> dropPrevious() {
            return new RewriteResult<>(RewriteKind.DROP_PREVIOUS, null);
        }

        @SuppressWarnings("unused")
        public static <T, C> RewriteResult<T, C> dropCurrent() {
            return new RewriteResult<>(RewriteKind.DROP_CURRENT, null);
        }

        @SuppressWarnings("unused")
        public static <T, C> RewriteResult<T, C> replaceBoth(
                PlannedOperation<T, C> replacement) {

            return new RewriteResult<>(
                    RewriteKind.REPLACE_BOTH,
                    Objects.requireNonNull(replacement, "L'operació de reemplaç no pot ser nul·la"));
        }
    }

    private static record PassResult<T, C>(
            List<PlannedOperation<T, C>> operations,
            boolean changed) {

        private PassResult {
            Objects.requireNonNull(
                    operations,
                    "La llista d'operacions no pot ser nul·la");
        }
    }
}