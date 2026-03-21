package menu.editor.planning.operations;

import java.util.Objects;

import menu.DynamicMenu;
import menu.editor.EditConfig;
import menu.editor.core.RemoveFamily;
import menu.editor.helpers.OptionSelector;
import menu.editor.planning.config.OperationType;
import menu.editor.planning.config.OptimizationFamily;
import menu.editor.planning.config.OptimizationProfile;
import menu.editor.planning.config.SelectorDependency;
import menu.editor.planning.interfaces.PlannedOperation;

public record RemovePlannedOperation<T, C>(
        OptionSelector<T, C> selector,
        EditConfig config) implements PlannedOperation<T, C> {

    public RemovePlannedOperation {
        Objects.requireNonNull(selector, "El selector no pot ser nul");
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");
    }

    @Override
    public void apply(DynamicMenu<T, C> menu) {
        RemoveFamily.removeIf(menu, selector, config);
    }

    @Override
    public menu.editor.Range range() {
        return config.range();
    }

    @Override
    public OperationType type() {
        return OperationType.REMOVE;
    }

    @Override
    public boolean preservesCardinality() {
        return false;
    }

    @Override
    public boolean changesIndexes() {
        return true;
    }

    @Override
    public boolean isBarrier() {
        return true;
    }

    @Override
    public OptimizationProfile optimizationProfile() {
        return new OptimizationProfile(
                type(),
                range(),
                false,
                false,
                true,
                false,
                false,
                true,
                OptimizationFamily.REMOVE,
                SelectorDependency.INDEX);
    }
}
