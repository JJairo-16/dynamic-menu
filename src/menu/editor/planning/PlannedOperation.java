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
}