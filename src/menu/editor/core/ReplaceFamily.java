package menu.editor.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import menu.DynamicMenu;
import menu.editor.helpers.ActionMapper;
import menu.action.*;
import menu.editor.EditConfig;
import menu.editor.Range;
import menu.editor.helpers.*;
import menu.model.MenuOption;
import menu.snapshot.MenuSnapshot;

import static menu.editor.core.MenuEditorSupport.*;

public final class ReplaceFamily {
    private ReplaceFamily() {
        throw new AssertionError("No es pot instanciar ReplaceFamily");
    }

    // -------------------------------------------------------------------------
    // Replace by index
    // -------------------------------------------------------------------------

    public static <T, C> DynamicMenu<T, C> replaceLabelAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceAtInternal(menu, index, newLabel, null, true, false);
    }

    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            MenuRuntimeAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAtInternal(menu, index, null, newAction, false, true);
    }

    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            MenuAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceActionAt(menu, index, runtimeOf(newAction));
    }

    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            SimpleMenuAction<T> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceActionAt(menu, index, runtimeOf(newAction));
    }

    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAtInternal(menu, index, newLabel, newAction, true, true);
    }

    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            MenuAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAt(menu, index, newLabel, runtimeOf(newAction));
    }

    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAt(menu, index, newLabel, runtimeOf(newAction));
    }

    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            MenuOption<T, C> newOption) {

        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        return replaceAt(menu, index, newOption.label(), newOption.action());
    }

    // -------------------------------------------------------------------------
    // Replace by exact label
    // -------------------------------------------------------------------------

    public static <T, C> boolean replaceFirstLabel(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return replaceFirstLabelIf(
                menu,
                exactLabelSelector(targetLabel),
                newLabel);
    }

    public static <T, C> boolean replaceLastLabel(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return replaceLastLabelIf(
                menu,
                exactLabelSelector(targetLabel),
                newLabel);
    }

    public static <T, C> int replaceAllLabels(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return replaceLabelIf(
                menu,
                exactLabelSelector(targetLabel),
                newLabel);
    }

    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceFirstActionIf(
                menu,
                exactLabelSelector(targetLabel),
                newAction);
    }

    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return replaceFirstAction(menu, targetLabel, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return replaceFirstAction(menu, targetLabel, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceLastActionIf(
                menu,
                exactLabelSelector(targetLabel),
                newAction);
    }

    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return replaceLastAction(menu, targetLabel, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return replaceLastAction(menu, targetLabel, runtimeOf(newAction));
    }

    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceActionIf(
                menu,
                exactLabelSelector(targetLabel),
                newAction);
    }

    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return replaceAllActions(menu, targetLabel, runtimeOf(newAction));
    }

    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return replaceAllActions(menu, targetLabel, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceFirstIf(
                menu,
                exactLabelSelector(targetLabel),
                (index, option) -> newOption(newLabel, newAction));
    }

    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return replaceFirst(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return replaceFirst(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuOption<T, C> newOption) {

        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        return replaceFirst(menu, targetLabel, newOption.label(), newOption.action());
    }

    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceLastIf(
                menu,
                exactLabelSelector(targetLabel),
                (index, option) -> newOption(newLabel, newAction));
    }

    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return replaceLast(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return replaceLast(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuOption<T, C> newOption) {

        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        return replaceLast(menu, targetLabel, newOption.label(), newOption.action());
    }

    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceIf(
                menu,
                exactLabelSelector(targetLabel),
                (index, option) -> newOption(newLabel, newAction));
    }

    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return replaceAll(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return replaceAll(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuOption<T, C> newOption) {

        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        return replaceAll(menu, targetLabel, newOption.label(), newOption.action());
    }

    // -------------------------------------------------------------------------
    // Replace if
    // -------------------------------------------------------------------------

    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return replaceIf(menu, selector, mapper, EditConfig.defaults());
    }

    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range) {

        return replaceIf(menu, selector, mapper, EditConfig.of(range));
    }

    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range,
            int limit) {

        return replaceIf(
                menu,
                selector,
                mapper,
                EditConfig.builder().range(range).limit(limit).build());
    }

    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            EditConfig config) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(mapper, "El transformador no pot ser nul");
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        MenuSnapshot<T, C> snapshot = menu.createSnapshot();
        List<MenuOption<T, C>> options = new ArrayList<>(snapshot.getOptionSnapshot());

        validateRange(config.range(), options.size());

        if (options.isEmpty() || config.limit() == 0) {
            return 0;
        }

        Range effectiveRange = config.range().clamp(options.size());
        List<Integer> targets = new ArrayList<>(QueryFamily.collectMatchingIndexes(
                options,
                selector,
                effectiveRange,
                config.limit(),
                config.reverse()));
        targets.sort(Integer::compareTo);

        if (targets.isEmpty()) {
            return 0;
        }

        for (int index : targets) {
            MenuOption<T, C> current = options.get(index);
            MenuOption<T, C> mapped = Objects.requireNonNull(
                    mapper.map(index, current),
                    "El transformador no pot retornar una opció nul·la");
            options.set(index, mapped);
        }

        rebuildSnapshot(snapshot, options);
        menu.restoreSnapshot(snapshot);
        return targets.size();
    }

    // -------------------------------------------------------------------------
    // Replace if reverse
    // -------------------------------------------------------------------------

    public static <T, C> int replaceIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return replaceIf(
                menu,
                selector,
                mapper,
                EditConfig.builder()
                        .reverse(true)
                        .build());
    }

    public static <T, C> int replaceIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range) {

        return replaceIf(
                menu,
                selector,
                mapper,
                EditConfig.builder()
                        .range(range)
                        .reverse(true)
                        .build());
    }

    public static <T, C> int replaceIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range,
            int limit) {

        return replaceIf(
                menu,
                selector,
                mapper,
                EditConfig.builder()
                        .range(range)
                        .limit(limit)
                        .reverse(true)
                        .build());
    }

    public static <T, C> int replaceIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            EditConfig config) {

        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        return replaceIf(
                menu,
                selector,
                mapper,
                EditConfig.builder()
                        .range(config.range())
                        .limit(config.limit())
                        .reverse(true)
                        .build());
    }

    // -------------------------------------------------------------------------
    // Replace label if
    // -------------------------------------------------------------------------

    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLabelIf(menu, selector, (index, option) -> newLabel, EditConfig.defaults());
    }

    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            Range range) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLabelIf(menu, selector, newLabel, EditConfig.of(range));
    }

    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            Range range,
            int limit) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLabelIf(
                menu,
                selector,
                newLabel,
                EditConfig.builder().range(range).limit(limit).build());
    }

    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            EditConfig config) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLabelIf(menu, selector, (index, option) -> newLabel, config);
    }

    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        return replaceLabelIf(menu, selector, mapper, EditConfig.defaults());
    }

    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            Range range) {

        return replaceLabelIf(menu, selector, mapper, EditConfig.of(range));
    }

    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            Range range,
            int limit) {

        return replaceLabelIf(
                menu,
                selector,
                mapper,
                EditConfig.builder().range(range).limit(limit).build());
    }

    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            EditConfig config) {

        Objects.requireNonNull(mapper, "El transformador de labels no pot ser nul");
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        return replaceIf(
                menu,
                selector,
                (index, option) -> newOption(
                        Objects.requireNonNull(
                                mapper.map(index, option),
                                "El transformador de labels no pot retornar nul"),
                        option.action()),
                config);
    }

    // -------------------------------------------------------------------------
    // Replace label if reverse
    // -------------------------------------------------------------------------

    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLabelIfReverse(menu, selector, (index, option) -> newLabel);
    }

    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            Range range) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLabelIfReverse(menu, selector, (index, option) -> newLabel, range);
    }

    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            Range range,
            int limit) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLabelIfReverse(
                menu,
                selector,
                (index, option) -> newLabel,
                EditConfig.builder()
                        .range(range)
                        .limit(limit)
                        .reverse(true)
                        .build());
    }

    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            EditConfig config) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLabelIfReverse(menu, selector, (index, option) -> newLabel, config);
    }

    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        return replaceLabelIfReverse(
                menu,
                selector,
                mapper,
                EditConfig.builder().reverse(true).build());
    }

    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            Range range) {

        return replaceLabelIfReverse(
                menu,
                selector,
                mapper,
                EditConfig.builder().range(range).reverse(true).build());
    }

    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            Range range,
            int limit) {

        return replaceLabelIfReverse(
                menu,
                selector,
                mapper,
                EditConfig.builder().range(range).limit(limit).reverse(true).build());
    }

    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            EditConfig config) {

        Objects.requireNonNull(mapper, "El transformador de labels no pot ser nul");
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        return replaceLabelIf(
                menu,
                selector,
                mapper,
                EditConfig.builder()
                        .range(config.range())
                        .limit(config.limit())
                        .reverse(true)
                        .build());
    }

    public static <T, C> boolean replaceFirstLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceFirstIf(
                menu,
                selector,
                (index, option) -> newOption(newLabel, option.action()));
    }

    public static <T, C> boolean replaceFirstLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        Objects.requireNonNull(mapper, "El transformador de labels no pot ser nul");
        return replaceFirstIf(
                menu,
                selector,
                (index, option) -> newOption(
                        Objects.requireNonNull(
                                mapper.map(index, option),
                                "El transformador de labels no pot retornar nul"),
                        option.action()));
    }

    public static <T, C> boolean replaceLastLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLastIf(
                menu,
                selector,
                (index, option) -> newOption(newLabel, option.action()));
    }

    public static <T, C> boolean replaceLastLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        Objects.requireNonNull(mapper, "El transformador de labels no pot ser nul");
        return replaceLastIf(
                menu,
                selector,
                (index, option) -> newOption(
                        Objects.requireNonNull(
                                mapper.map(index, option),
                                "El transformador de labels no pot retornar nul"),
                        option.action()));
    }

    // -------------------------------------------------------------------------
    // Replace action if
    // -------------------------------------------------------------------------

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        return replaceActionIf(menu, selector, newAction, EditConfig.defaults());
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return replaceActionIf(menu, selector, runtimeOf(newAction));
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return replaceActionIf(menu, selector, runtimeOf(newAction));
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            Range range) {

        return replaceActionIf(menu, selector, newAction, EditConfig.of(range));
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range);
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range);
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            Range range,
            int limit) {

        return replaceActionIf(
                menu,
                selector,
                newAction,
                EditConfig.builder().range(range).limit(limit).build());
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range,
            int limit) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range, limit);
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range,
            int limit) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range, limit);
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            EditConfig config) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceActionIf(
                menu,
                selector,
                newAction,
                config);
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            EditConfig config) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), config);
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            EditConfig config) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), config);
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        return replaceActionIf(menu, selector, mapper, EditConfig.defaults());
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            Range range) {

        return replaceActionIf(menu, selector, mapper, EditConfig.of(range));
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            Range range,
            int limit) {

        return replaceActionIf(
                menu,
                selector,
                mapper,
                EditConfig.builder().range(range).limit(limit).build());
    }

    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            EditConfig config) {

        Objects.requireNonNull(mapper, "El transformador de comportaments no pot ser nul");
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        return replaceIf(
                menu,
                selector,
                (index, option) -> newOption(
                        option.label(),
                        Objects.requireNonNull(
                                mapper.map(index, option),
                                "El transformador de comportaments no pot retornar nul")),
                config);
    }

    // -------------------------------------------------------------------------
    // Replace action if reverse
    // -------------------------------------------------------------------------

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceActionIfReverse(
                menu,
                selector,
                newAction,
                EditConfig.builder().reverse(true).build());
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction));
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction));
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            Range range) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceActionIfReverse(
                menu,
                selector,
                newAction,
                EditConfig.builder().range(range).reverse(true).build());
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), range);
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), range);
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            Range range,
            int limit) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceActionIfReverse(
                menu,
                selector,
                newAction,
                EditConfig.builder().range(range).limit(limit).reverse(true).build());
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range,
            int limit) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), range, limit);
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range,
            int limit) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), range, limit);
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            EditConfig config) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceActionIfReverse(
                menu,
                selector,
                newAction,
                config);
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            EditConfig config) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), config);
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            EditConfig config) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), config);
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        return replaceActionIfReverse(
                menu,
                selector,
                mapper,
                EditConfig.builder().reverse(true).build());
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            Range range) {

        return replaceActionIfReverse(
                menu,
                selector,
                mapper,
                EditConfig.builder().range(range).reverse(true).build());
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            Range range,
            int limit) {

        return replaceActionIfReverse(
                menu,
                selector,
                mapper,
                EditConfig.builder().range(range).limit(limit).reverse(true).build());
    }

    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            EditConfig config) {

        Objects.requireNonNull(mapper, "El transformador de comportaments no pot ser nul");
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        return replaceActionIf(
                menu,
                selector,
                mapper,
                EditConfig.builder()
                        .range(config.range())
                        .limit(config.limit())
                        .reverse(true)
                        .build());
    }

    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceFirstIf(
                menu,
                selector,
                (index, option) -> newOption(option.label(), newAction));
    }

    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return replaceFirstActionIf(menu, selector, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return replaceFirstActionIf(menu, selector, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        Objects.requireNonNull(mapper, "El transformador de comportaments no pot ser nul");
        return replaceFirstIf(
                menu,
                selector,
                (index, option) -> newOption(
                        option.label(),
                        Objects.requireNonNull(
                                mapper.map(index, option),
                                "El transformador de comportaments no pot retornar nul")));
    }

    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceLastIf(
                menu,
                selector,
                (index, option) -> newOption(option.label(), newAction));
    }

    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return replaceLastActionIf(menu, selector, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return replaceLastActionIf(menu, selector, runtimeOf(newAction));
    }

    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        Objects.requireNonNull(mapper, "El transformador de comportaments no pot ser nul");
        return replaceLastIf(
                menu,
                selector,
                (index, option) -> newOption(
                        option.label(),
                        Objects.requireNonNull(
                                mapper.map(index, option),
                                "El transformador de comportaments no pot retornar nul")));
    }

    // -------------------------------------------------------------------------
    // First / last generic replace
    // -------------------------------------------------------------------------

    public static <T, C> boolean replaceFirstIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return replaceIf(
                menu,
                selector,
                mapper,
                EditConfig.builder().limit(1).build()) > 0;
    }

    public static <T, C> boolean replaceLastIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return replaceIf(
                menu,
                selector,
                mapper,
                EditConfig.builder().limit(1).reverse(true).build()) > 0;
    }

    // -------------------------------------------------------------------------
    // Batch replacements
    // -------------------------------------------------------------------------

    public static <T, C> DynamicMenu<T, C> replaceLabelsAt(
            DynamicMenu<T, C> menu,
            Map<Integer, String> replacements) {

        Objects.requireNonNull(replacements, "El mapa de reemplaços no pot ser nul");
        return replaceBatch(menu, replacements, null, null);
    }

    public static <T, C> DynamicMenu<T, C> replaceActionsAt(
            DynamicMenu<T, C> menu,
            Map<Integer, MenuRuntimeAction<T, C>> replacements) {

        Objects.requireNonNull(replacements, "El mapa de reemplaços no pot ser nul");
        return replaceBatch(menu, null, replacements, null);
    }

    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            Map<Integer, MenuOption<T, C>> replacements) {

        Objects.requireNonNull(replacements, "El mapa de reemplaços no pot ser nul");
        return replaceBatch(menu, null, null, replacements);
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private static <T, C> DynamicMenu<T, C> replaceAtInternal(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            MenuRuntimeAction<T, C> newAction,
            boolean replaceLabel,
            boolean replaceAction) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");

        if (replaceLabel) {
            Objects.requireNonNull(newLabel, "La nova etiqueta no pot ser nul");
        }

        if (replaceAction) {
            Objects.requireNonNull(newAction, "La nova acció no pot ser nul");
        }

        if (!replaceLabel && !replaceAction) {
            return menu;
        }

        MenuSnapshot<T, C> snapshot = menu.createSnapshot();
        List<MenuOption<T, C>> options = new ArrayList<>(snapshot.getOptionSnapshot());

        validateExistingIndex(index, options.size());

        MenuOption<T, C> oldOption = options.get(index);
        String finalLabel = replaceLabel ? newLabel : oldOption.label();
        MenuRuntimeAction<T, C> finalAction = replaceAction ? newAction : oldOption.action();

        options.set(index, newOption(finalLabel, finalAction));
        rebuildSnapshot(snapshot, options);
        return menu.restoreSnapshot(snapshot);
    }

    private static <T, C> DynamicMenu<T, C> replaceBatch(
            DynamicMenu<T, C> menu,
            Map<Integer, String> labelReplacements,
            Map<Integer, MenuRuntimeAction<T, C>> actionReplacements,
            Map<Integer, MenuOption<T, C>> optionReplacements) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");

        MenuSnapshot<T, C> snapshot = menu.createSnapshot();
        List<MenuOption<T, C>> options = new ArrayList<>(snapshot.getOptionSnapshot());

        if (options.isEmpty()) {
            return menu;
        }

        boolean hasLabelReplacements = labelReplacements != null && !labelReplacements.isEmpty();
        boolean hasActionReplacements = actionReplacements != null && !actionReplacements.isEmpty();
        boolean hasOptionReplacements = optionReplacements != null && !optionReplacements.isEmpty();

        if (!hasLabelReplacements && !hasActionReplacements && !hasOptionReplacements) {
            return menu;
        }

        if (hasLabelReplacements) {
            for (Map.Entry<Integer, String> entry : labelReplacements.entrySet()) {
                int index = entry.getKey();
                String newLabel = Objects.requireNonNull(
                        entry.getValue(),
                        "El nou label no pot ser nul");

                validateExistingIndex(index, options.size());

                MenuOption<T, C> current = options.get(index);
                options.set(index, newOption(newLabel, current.action()));
            }
        } else if (hasActionReplacements) {
            for (Map.Entry<Integer, MenuRuntimeAction<T, C>> entry : actionReplacements.entrySet()) {
                int index = entry.getKey();
                MenuRuntimeAction<T, C> newAction = Objects.requireNonNull(
                        entry.getValue(),
                        "El nou comportament no pot ser nul");

                validateExistingIndex(index, options.size());

                MenuOption<T, C> current = options.get(index);
                options.set(index, newOption(current.label(), newAction));
            }
        } else if (hasOptionReplacements) {
            for (Map.Entry<Integer, MenuOption<T, C>> entry : optionReplacements.entrySet()) {
                int index = entry.getKey();
                MenuOption<T, C> replacementOption = Objects.requireNonNull(
                        entry.getValue(),
                        "La nova opció no pot ser nul·la");

                validateExistingIndex(index, options.size());
                options.set(index, replacementOption);
            }
        }

        rebuildSnapshot(snapshot, options);
        return menu.restoreSnapshot(snapshot);
    }
}