package menu.editor.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import menu.DynamicMenu;
import menu.editor.EditConfig;
import menu.editor.Range;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;
import menu.snapshot.MenuSnapshot;

import static menu.editor.core.MenuEditorSupport.*;

public final class RemoveFamily {
    private RemoveFamily() {
    }

    // -------------------------------------------------------------------------
    // Remove if
    // -------------------------------------------------------------------------

    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return removeIf(menu, selector, EditConfig.defaults());
    }

    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return removeIf(menu, selector, EditConfig.of(range));
    }

    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range,
            int limit) {

        return removeIf(
                menu,
                selector,
                EditConfig.builder()
                        .range(range)
                        .limit(limit)
                        .build());
    }

    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            EditConfig config) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        MenuSnapshot<T, C> snapshot = menu.createSnapshot();
        List<MenuOption<T, C>> options = new ArrayList<>(snapshot.getOptionSnapshot());

        validateRange(config.range(), options.size());

        if (options.isEmpty() || config.limit() == 0) {
            return 0;
        }

        Range effectiveRange = config.range().clamp(options.size());
        Set<Integer> toRemove = QueryFamily.collectMatchingIndexes(
                options,
                selector,
                effectiveRange,
                config.limit(),
                config.reverse());

        if (toRemove.isEmpty()) {
            return 0;
        }

        List<MenuOption<T, C>> rebuilt = new ArrayList<>(options.size() - toRemove.size());
        for (int i = 0; i < options.size(); i++) {
            if (!toRemove.contains(i)) {
                rebuilt.add(options.get(i));
            }
        }

        rebuildSnapshot(snapshot, rebuilt);
        menu.restoreSnapshot(snapshot);
        return toRemove.size();
    }

    // -------------------------------------------------------------------------
    // Remove all if reverse
    // -------------------------------------------------------------------------

    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return removeIf(
                menu,
                selector,
                EditConfig.builder()
                        .reverse(true)
                        .build());
    }

    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return removeIf(
                menu,
                selector,
                EditConfig.builder()
                        .range(range)
                        .reverse(true)
                        .build());
    }

    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range,
            int limit) {

        return removeIf(
                menu,
                selector,
                EditConfig.builder()
                        .range(range)
                        .limit(limit)
                        .reverse(true)
                        .build());
    }

    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            EditConfig config) {

        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        return removeIf(
                menu,
                selector,
                EditConfig.builder()
                        .range(config.range())
                        .limit(config.limit())
                        .reverse(true)
                        .build());
    }

    public static <T, C> boolean removeFirstIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return removeIf(
                menu,
                selector,
                EditConfig.builder().limit(1).build()) > 0;
    }

    public static <T, C> boolean removeLastIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return removeIf(
                menu,
                selector,
                EditConfig.builder().limit(1).reverse(true).build()) > 0;
    }

    public static <T, C> boolean removeFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return removeFirstIf(menu, exactLabelSelector(label));
    }

    public static <T, C> boolean removeLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return removeLastIf(menu, exactLabelSelector(label));
    }

    public static <T, C> int removeAllLabels(
            DynamicMenu<T, C> menu,
            String label) {

        return removeIf(menu, exactLabelSelector(label));
    }
}