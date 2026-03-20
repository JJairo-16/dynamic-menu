package menu.editor.planning;

import menu.DynamicMenu;
import menu.editor.Range;

public interface PlannedOperation<T, C> {

    void apply(DynamicMenu<T, C> menu);

    OperationType type();

    Range range();

    default boolean isNoOp() {
        return false;
    }

    default boolean reorders() {
        return false;
    }

    default boolean preservesCardinality() {
        return true;
    }

    default boolean changesIndexes() {
        return false;
    }

    default boolean changesLabels() {
        return false;
    }

    default boolean hasPinnedSelectors() {
        return false;
    }

    default boolean isBarrier() {
        return false;
    }
}