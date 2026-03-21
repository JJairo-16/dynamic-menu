package menu.editor.planning.operations;

import java.util.Comparator;
import java.util.Objects;

import menu.DynamicMenu;
import menu.editor.Range;
import menu.editor.core.SortFamily;
import menu.editor.helpers.OptionSelector;
import menu.editor.planning.config.OperationType;
import menu.editor.planning.config.OptimizationFamily;
import menu.editor.planning.config.OptimizationProfile;
import menu.editor.planning.config.SelectorDependency;
import menu.editor.planning.interfaces.PlannedOperation;
import menu.model.MenuOption;

public record SortPlannedOperation<T, C>(
        Comparator<MenuOption<T, C>> comparator,
        OptionSelector<T, C> firstSelector,
        OptionSelector<T, C> lastSelector,
        Range range,
        boolean hasPinnedFirst,
        boolean hasPinnedLast) implements PlannedOperation<T, C> {

    public SortPlannedOperation {
        Objects.requireNonNull(comparator, "El comparador no pot ser nul");
        Objects.requireNonNull(firstSelector, "El selector inicial no pot ser nul");
        Objects.requireNonNull(lastSelector, "El selector final no pot ser nul");
        Objects.requireNonNull(range, "El rang no pot ser nul");
    }

    @Override
    public void apply(DynamicMenu<T, C> menu) {
        SortFamily.sortByLabel(
                menu,
                comparator,
                firstSelector,
                lastSelector,
                range);
    }

    @Override
    public OperationType type() {
        return OperationType.SORT;
    }

    @Override
    public boolean reorders() {
        return true;
    }

    @Override
    public boolean preservesCardinality() {
        return true;
    }

    @Override
    public boolean changesIndexes() {
        return true;
    }

    @Override
    public boolean hasPinnedSelectors() {
        return hasPinnedFirst || hasPinnedLast;
    }

    @Override
    public OptimizationProfile optimizationProfile() {
        return new OptimizationProfile(
                type(),
                range(),
                true,
                true,
                true,
                false,
                hasPinnedSelectors(),
                false,
                OptimizationFamily.REORDER,
                SelectorDependency.INDEX);
    }
}
