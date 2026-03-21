package menu.editor.planning.operations;

import menu.DynamicMenu;
import menu.editor.Range;
import menu.editor.planning.config.OperationType;
import menu.editor.planning.config.OptimizationFamily;
import menu.editor.planning.config.OptimizationProfile;
import menu.editor.planning.config.SelectorDependency;
import menu.editor.planning.interfaces.PlannedOperation;

public final class NoOpPlannedOperation<T, C> implements PlannedOperation<T, C> {

    private static final NoOpPlannedOperation<?, ?> INSTANCE = new NoOpPlannedOperation<>();

    private static final OptimizationProfile PROFILE =
            new OptimizationProfile(
                    OperationType.NO_OP,
                    Range.all(),
                    false,
                    true,
                    false,
                    false,
                    false,
                    false,
                    OptimizationFamily.GENERIC,
                    SelectorDependency.NONE);

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

    @Override
    public boolean supportsSemanticDeduplication() {
        return true;
    }

    @Override
    public boolean isSemanticallyEquivalentTo(PlannedOperation<T, C> other) {
        return other instanceof NoOpPlannedOperation<?, ?>;
    }

    @Override
    public OptimizationProfile optimizationProfile() {
        return PROFILE;
    }
}
