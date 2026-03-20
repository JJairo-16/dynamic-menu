package menu.editor.planning.operations;

import menu.DynamicMenu;
import menu.editor.Range;
import menu.editor.planning.OperationType;
import menu.editor.planning.PlannedOperation;

public final class NoOpPlannedOperation<T, C> implements PlannedOperation<T, C> {

    private static final NoOpPlannedOperation<?, ?> INSTANCE = new NoOpPlannedOperation<>();

    private NoOpPlannedOperation() {
    }

    @SuppressWarnings("unchecked")
    public static <T, C> NoOpPlannedOperation<T, C> instance() {
        return (NoOpPlannedOperation<T, C>) INSTANCE;
    }

    @Override
    public void apply(DynamicMenu<T, C> menu) {
        // no-op
    }

    @Override
    public OperationType type() {
        return OperationType.NO_OP;
    }

    @Override
    public Range range() {
        return Range.all();
    }

    @Override
    public boolean isNoOp() {
        return true;
    }
}