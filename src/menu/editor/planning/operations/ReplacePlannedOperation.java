package menu.editor.planning.operations;

import java.util.Objects;

import menu.DynamicMenu;
import menu.editor.EditConfig;
import menu.editor.core.ReplaceFamily;
import menu.editor.helpers.OptionMapper;
import menu.editor.helpers.OptionSelector;
import menu.editor.planning.config.OperationType;
import menu.editor.planning.config.OptimizationFamily;
import menu.editor.planning.config.OptimizationProfile;
import menu.editor.planning.config.SelectorDependency;
import menu.editor.planning.interfaces.PlannedOperation;

public record ReplacePlannedOperation<T, C>(
        OptionSelector<T, C> selector,
        OptionMapper<T, C> mapper,
        EditConfig config) implements PlannedOperation<T, C> {

    public ReplacePlannedOperation {
        Objects.requireNonNull(selector, "El selector no pot ser nul");
        Objects.requireNonNull(mapper, "El transformador no pot ser nul");
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");
    }

    @Override
    public void apply(DynamicMenu<T, C> menu) {
        ReplaceFamily.replaceIf(menu, selector, mapper, config);
    }

    @Override
    public menu.editor.Range range() {
        return config.range();
    }

    @Override
    public OperationType type() {
        return OperationType.REPLACE;
    }

    @Override
    public boolean changesLabels() {
        return true;
    }

    @Override
    public OptimizationProfile optimizationProfile() {
        return new OptimizationProfile(
                type(),
                range(),
                false,
                true,
                false,
                true,
                false,
                false,
                OptimizationFamily.REPLACE,
                SelectorDependency.UNKNOWN);
    }
}
