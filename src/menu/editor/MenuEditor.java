package menu.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import menu.DynamicMenu;
import menu.action.*;
import menu.editor.helpers.*;
import menu.model.MenuOption;
import menu.snapshot.MenuSnapshot;

/** Utilitats avançades d'edició per a {@link DynamicMenu}. */
public final class MenuEditor {

    private MenuEditor() {
        throw new AssertionError("No es pot instanciar MenuEditor");
    }

    public static <T, C> OptionSelector<T, C> alwaysFalseSelector() {
        return (index, option) -> false;
    }

    public static <T, C> OptionSelector<T, C> alwaysTrueSelector() {
        return (index, option) -> true;
    }

    // -------------------------------------------------------------------------
    // Replace by index
    // -------------------------------------------------------------------------

    /** Reemplaça el label d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceLabelAt(DynamicMenu<T, C> menu, int index, String newLabel) {
        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceAtInternal(menu, index, newLabel, null, true, false);
    }

    /** Reemplaça el comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            MenuRuntimeAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAtInternal(menu, index, null, newAction, false, true);
    }

    /** Reemplaça el comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            MenuAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceActionAt(menu, index, runtimeOf(newAction));
    }

    /** Reemplaça el comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            SimpleMenuAction<T> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceActionAt(menu, index, runtimeOf(newAction));
    }

    /** Reemplaça label i comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAtInternal(menu, index, newLabel, newAction, true, true);
    }

    /** Reemplaça label i comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            MenuAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAt(menu, index, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça label i comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAt(menu, index, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça una opció completa per índex. */
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

    /** Reemplaça el label de la primera coincidència exacta. */
    public static <T, C> boolean replaceFirstLabel(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return replaceFirstLabelIf(
                menu,
                exactLabelSelector(targetLabel),
                newLabel);
    }

    /** Reemplaça el label de l'última coincidència exacta. */
    public static <T, C> boolean replaceLastLabel(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return replaceLastLabelIf(
                menu,
                exactLabelSelector(targetLabel),
                newLabel);
    }

    /** Reemplaça el label de totes les coincidències exactes. */
    public static <T, C> int replaceAllLabels(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return replaceLabelIf(
                menu,
                exactLabelSelector(targetLabel),
                newLabel);
    }

    /** Reemplaça el comportament de la primera coincidència exacta. */
    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceFirstActionIf(
                menu,
                exactLabelSelector(targetLabel),
                newAction);
    }

    /** Reemplaça el comportament de la primera coincidència exacta. */
    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return replaceFirstAction(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça el comportament de la primera coincidència exacta. */
    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return replaceFirstAction(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça el comportament de l'última coincidència exacta. */
    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceLastActionIf(
                menu,
                exactLabelSelector(targetLabel),
                newAction);
    }

    /** Reemplaça el comportament de l'última coincidència exacta. */
    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return replaceLastAction(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça el comportament de l'última coincidència exacta. */
    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return replaceLastAction(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça el comportament de totes les coincidències exactes. */
    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceActionIf(
                menu,
                exactLabelSelector(targetLabel),
                newAction);
    }

    /** Reemplaça el comportament de totes les coincidències exactes. */
    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return replaceAllActions(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça el comportament de totes les coincidències exactes. */
    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return replaceAllActions(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça la primera coincidència exacta. */
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

    /** Reemplaça la primera coincidència exacta. */
    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return replaceFirst(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça la primera coincidència exacta. */
    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return replaceFirst(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça la primera coincidència exacta. */
    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuOption<T, C> newOption) {

        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        return replaceFirst(menu, targetLabel, newOption.label(), newOption.action());
    }

    /** Reemplaça l'última coincidència exacta. */
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

    /** Reemplaça l'última coincidència exacta. */
    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return replaceLast(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça l'última coincidència exacta. */
    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return replaceLast(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça l'última coincidència exacta. */
    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuOption<T, C> newOption) {

        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        return replaceLast(menu, targetLabel, newOption.label(), newOption.action());
    }

    /** Reemplaça totes les coincidències exactes. */
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

    /** Reemplaça totes les coincidències exactes. */
    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return replaceAll(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça totes les coincidències exactes. */
    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return replaceAll(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça totes les coincidències exactes. */
    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuOption<T, C> newOption) {

        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        return replaceAll(menu, targetLabel, newOption.label(), newOption.action());
    }

    // -------------------------------------------------------------------------
    // Remove if
    // -------------------------------------------------------------------------

    /** Elimina totes les opcions que compleixen una condició. */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return removeIf(menu, selector, EditConfig.defaults());
    }

    /** Elimina totes les opcions que compleixen una condició dins d'un rang. */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return removeIf(menu, selector, EditConfig.of(range));
    }

    /** Elimina opcions que compleixen una condició dins d'un rang i amb límit. */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range,
            int limit) {

        return removeIf(
                menu,
                selector,
                EditConfig.builder().range(range).limit(limit).build());
    }

    /** Elimina opcions que compleixen una condició segons una configuració. */
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
        Set<Integer> toRemove = collectMatchingIndexes(
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

    /** Elimina la primera opció que compleix una condició. */
    public static <T, C> boolean removeFirstIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return removeIf(
                menu,
                selector,
                EditConfig.builder().limit(1).build()) > 0;
    }

    /** Elimina l'última opció que compleix una condició. */
    public static <T, C> boolean removeLastIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return removeIf(
                menu,
                selector,
                EditConfig.builder().limit(1).reverse(true).build()) > 0;
    }

    /** Elimina la primera coincidència exacta. */
    public static <T, C> boolean removeFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return removeFirstIf(menu, exactLabelSelector(label));
    }

    /** Elimina l'última coincidència exacta. */
    public static <T, C> boolean removeLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return removeLastIf(menu, exactLabelSelector(label));
    }

    /** Elimina totes les coincidències exactes. */
    public static <T, C> int removeAllLabels(DynamicMenu<T, C> menu, String label) {
        return removeIf(menu, exactLabelSelector(label));
    }

    // -------------------------------------------------------------------------
    // Replace if
    // -------------------------------------------------------------------------

    /** Reemplaça totes les opcions que compleixen una condició. */
    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return replaceIf(menu, selector, mapper, EditConfig.defaults());
    }

    /** Reemplaça opcions dins d'un rang. */
    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range) {

        return replaceIf(menu, selector, mapper, EditConfig.of(range));
    }

    /** Reemplaça opcions dins d'un rang i amb límit. */
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

    /** Reemplaça opcions segons una configuració. */
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
        List<Integer> targets = new ArrayList<>(collectMatchingIndexes(
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
    // Replace label if
    // -------------------------------------------------------------------------

    /** Reemplaça només labels. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLabelIf(menu, selector, (index, option) -> newLabel, EditConfig.defaults());
    }

    /** Reemplaça només labels dins d'un rang. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            Range range) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLabelIf(menu, selector, newLabel, EditConfig.of(range));
    }

    /** Reemplaça només labels dins d'un rang i amb límit. */
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

    /** Reemplaça només labels segons una configuració. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            EditConfig config) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceLabelIf(menu, selector, (index, option) -> newLabel, config);
    }

    /** Reemplaça només labels. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        return replaceLabelIf(menu, selector, mapper, EditConfig.defaults());
    }

    /** Reemplaça només labels dins d'un rang. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            Range range) {

        return replaceLabelIf(menu, selector, mapper, EditConfig.of(range));
    }

    /** Reemplaça només labels dins d'un rang i amb límit. */
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

    /** Reemplaça només labels segons una configuració. */
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

    /** Reemplaça el label de la primera opció que compleix una condició. */
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

    /** Reemplaça el label de la primera opció que compleix una condició. */
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

    /** Reemplaça el label de l'última opció que compleix una condició. */
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

    /** Reemplaça el label de l'última opció que compleix una condició. */
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

    /** Reemplaça només comportaments. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        return replaceActionIf(menu, selector, newAction, EditConfig.defaults());
    }

    /** Reemplaça només comportaments. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return replaceActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça només comportaments. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return replaceActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça només comportaments dins d'un rang. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            Range range) {

        return replaceActionIf(menu, selector, newAction, EditConfig.of(range));
    }

    /** Reemplaça només comportaments dins d'un rang. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range);
    }

    /** Reemplaça només comportaments dins d'un rang. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range);
    }

    /** Reemplaça només comportaments dins d'un rang i amb límit. */
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

    /** Reemplaça només comportaments dins d'un rang i amb límit. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range,
            int limit) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range, limit);
    }

    /** Reemplaça només comportaments dins d'un rang i amb límit. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range,
            int limit) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range, limit);
    }

    /** Reemplaça només comportaments segons una configuració. */
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

    /** Reemplaça només comportaments segons una configuració. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            EditConfig config) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), config);
    }

    /** Reemplaça només comportaments segons una configuració. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            EditConfig config) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), config);
    }

    /** Reemplaça només comportaments. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        return replaceActionIf(menu, selector, mapper, EditConfig.defaults());
    }

    /** Reemplaça només comportaments dins d'un rang. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            Range range) {

        return replaceActionIf(menu, selector, mapper, EditConfig.of(range));
    }

    /** Reemplaça només comportaments dins d'un rang i amb límit. */
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

    /** Reemplaça només comportaments segons una configuració. */
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

    /** Reemplaça el comportament de la primera opció que compleix una condició. */
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

    /** Reemplaça el comportament de la primera opció que compleix una condició. */
    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return replaceFirstActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça el comportament de la primera opció que compleix una condició. */
    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return replaceFirstActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça el comportament de la primera opció que compleix una condició. */
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

    /** Reemplaça el comportament de l'última opció que compleix una condició. */
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

    /** Reemplaça el comportament de l'última opció que compleix una condició. */
    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return replaceLastActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça el comportament de l'última opció que compleix una condició. */
    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return replaceLastActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça el comportament de l'última opció que compleix una condició. */
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

    /** Reemplaça la primera opció que compleix una condició. */
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

    /** Reemplaça l'última opció que compleix una condició. */
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
    // Sorting
    // -------------------------------------------------------------------------

    /** Ordena totes les opcions per label. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(DynamicMenu<T, C> menu) {
        return sortByLabelInternal(
                menu,
                defaultLabelComparator(),
                alwaysFalseSelector(),
                alwaysFalseSelector(),
                Range.all());
    }

    /** Ordena totes les opcions per label amb un comparador. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator) {

        return sortByLabelInternal(
                menu,
                comparator,
                alwaysFalseSelector(),
                alwaysFalseSelector(),
                Range.all());
    }

    /** Ordena les opcions per label dins d'un rang. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Range range) {

        return sortByLabelInternal(
                menu,
                defaultLabelComparator(),
                alwaysFalseSelector(),
                alwaysFalseSelector(),
                range);
    }

    /** Ordena les opcions per label dins d'un rang amb comparador. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Range range) {

        return sortByLabelInternal(
                menu,
                comparator,
                alwaysFalseSelector(),
                alwaysFalseSelector(),
                range);
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        return sortByLabelInternal(
                menu,
                defaultLabelComparator(),
                firstSelector,
                lastSelector,
                Range.all());
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        return sortByLabelInternal(
                menu,
                defaultLabelComparator(),
                firstSelector,
                lastSelector,
                range);
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        return sortByLabelInternal(
                menu,
                comparator,
                firstSelector,
                lastSelector,
                Range.all());
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        return sortByLabelInternal(menu, comparator, firstSelector, lastSelector, range);
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        return sortByLabelPinnedIndexes(
                menu,
                defaultLabelComparator(),
                firstIndexes,
                lastIndexes,
                Range.all());
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes,
            Range range) {

        return sortByLabelPinnedIndexes(
                menu,
                defaultLabelComparator(),
                firstIndexes,
                lastIndexes,
                range);
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        return sortByLabelPinnedIndexes(
                menu,
                comparator,
                firstIndexes,
                lastIndexes,
                Range.all());
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes,
            Range range) {

        Set<Integer> first = firstIndexes == null ? Set.of() : new LinkedHashSet<>(firstIndexes);
        Set<Integer> last = lastIndexes == null ? Set.of() : new LinkedHashSet<>(lastIndexes);

        return sortByLabelInternal(
                menu,
                comparator,
                (index, option) -> first.contains(index),
                (index, option) -> last.contains(index),
                range);
    }

    // -------------------------------------------------------------------------
    // Query helpers
    // -------------------------------------------------------------------------

    /** Retorna l'índex de la primera coincidència. */
    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return indexOfFirst(menu, selector, Range.all());
    }

    /** Retorna l'índex de la primera coincidència dins d'un rang. */
    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        List<MenuOption<T, C>> options = currentOptions(menu);
        validateRange(range, options.size());

        Range effectiveRange = range.clamp(options.size());
        for (int i = effectiveRange.fromInclusive(); i < effectiveRange.toExclusive(); i++) {
            if (selector.test(i, options.get(i))) {
                return i;
            }
        }

        return -1;
    }

    /** Retorna l'índex de l'última coincidència. */
    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return indexOfLast(menu, selector, Range.all());
    }

    /** Retorna l'índex de l'última coincidència dins d'un rang. */
    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        List<MenuOption<T, C>> options = currentOptions(menu);
        validateRange(range, options.size());

        Range effectiveRange = range.clamp(options.size());
        for (int i = effectiveRange.toExclusive() - 1; i >= effectiveRange.fromInclusive(); i--) {
            if (selector.test(i, options.get(i))) {
                return i;
            }
        }

        return -1;
    }

    /** Indica si existeix alguna coincidència. */
    public static <T, C> boolean containsMatch(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return indexOfFirst(menu, selector) >= 0;
    }

    /** Compta quantes opcions compleixen una condició. */
    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return countMatches(menu, selector, Range.all());
    }

    /** Compta quantes opcions compleixen una condició dins d'un rang. */
    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        List<MenuOption<T, C>> options = currentOptions(menu);
        validateRange(range, options.size());

        int count = 0;
        Range effectiveRange = range.clamp(options.size());
        for (int i = effectiveRange.fromInclusive(); i < effectiveRange.toExclusive(); i++) {
            if (selector.test(i, options.get(i))) {
                count++;
            }
        }

        return count;
    }

    /** Retorna tots els índexs que compleixen una condició. */
    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return indexesOf(menu, selector, Range.all());
    }

    /** Retorna tots els índexs que compleixen una condició dins d'un rang. */
    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        List<MenuOption<T, C>> options = currentOptions(menu);
        validateRange(range, options.size());

        List<Integer> matches = new ArrayList<>();
        Range effectiveRange = range.clamp(options.size());
        for (int i = effectiveRange.fromInclusive(); i < effectiveRange.toExclusive(); i++) {
            if (selector.test(i, options.get(i))) {
                matches.add(i);
            }
        }

        return List.copyOf(matches);
    }

    /** Retorna totes les opcions que compleixen una condició. */
    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return matchingOptions(menu, selector, Range.all());
    }

    /** Retorna totes les opcions que compleixen una condició dins d'un rang. */
    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        List<MenuOption<T, C>> options = currentOptions(menu);
        validateRange(range, options.size());

        List<MenuOption<T, C>> matches = new ArrayList<>();
        Range effectiveRange = range.clamp(options.size());
        for (int i = effectiveRange.fromInclusive(); i < effectiveRange.toExclusive(); i++) {
            MenuOption<T, C> option = options.get(i);
            if (selector.test(i, option)) {
                matches.add(option);
            }
        }

        return List.copyOf(matches);
    }

    /** Retorna l'índex de la primera coincidència exacta. */
    public static <T, C> int indexOfFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return indexOfFirst(menu, exactLabelSelector(label));
    }

    /** Retorna l'índex de l'última coincidència exacta. */
    public static <T, C> int indexOfLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return indexOfLast(menu, exactLabelSelector(label));
    }

    /** Indica si existeix alguna coincidència exacta. */
    public static <T, C> boolean containsLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return indexOfFirstLabel(menu, label) >= 0;
    }

    /** Compta quantes coincidències exactes hi ha. */
    public static <T, C> int countLabelMatches(
            DynamicMenu<T, C> menu,
            String label) {

        return countMatches(menu, exactLabelSelector(label));
    }

    /** Retorna la primera opció que compleix una condició o {@code null}. */
    public static <T, C> MenuOption<T, C> findFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");

        List<MenuOption<T, C>> options = currentOptions(menu);
        for (int i = 0; i < options.size(); i++) {
            MenuOption<T, C> option = options.get(i);

            if (selector.test(i, option)) {
                return option;
            }
        }

        return null;
    }

    /** Retorna l'última opció que compleix una condició o {@code null}. */
    public static <T, C> MenuOption<T, C> findLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");

        List<MenuOption<T, C>> options = currentOptions(menu);
        for (int i = options.size() - 1; i >= 0; i--) {
            MenuOption<T, C> option = options.get(i);

            if (selector.test(i, option)) {
                return option;
            }
        }

        return null;
    }
    
    /** Retorna la primera coincidència exacta o {@code null}. */
    public static <T, C> MenuOption<T, C> findFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return findFirst(menu, exactLabelSelector(label));
    }

    /** Retorna l'última coincidència exacta o {@code null}. */
    public static <T, C> MenuOption<T, C> findLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return findLast(menu, exactLabelSelector(label));
    }

    // -------------------------------------------------------------------------
    // Batch replacements
    // -------------------------------------------------------------------------

    /** Reemplaça diversos labels per índex. */
    public static <T, C> DynamicMenu<T, C> replaceLabelsAt(
            DynamicMenu<T, C> menu,
            Map<Integer, String> replacements) {

        Objects.requireNonNull(replacements, "El mapa de reemplaços no pot ser nul");
        return replaceBatch(menu, replacements, null, null);
    }

    /** Reemplaça diversos comportaments per índex. */
    public static <T, C> DynamicMenu<T, C> replaceActionsAt(
            DynamicMenu<T, C> menu,
            Map<Integer, MenuRuntimeAction<T, C>> replacements) {

        Objects.requireNonNull(replacements, "El mapa de reemplaços no pot ser nul");
        return replaceBatch(menu, null, replacements, null);
    }

    /** Reemplaça diverses opcions completes per índex. */
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

    private static <T, C> DynamicMenu<T, C> sortByLabelInternal(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(comparator, "El comparador no pot ser nul");
        Objects.requireNonNull(firstSelector, "El selector inicial no pot ser nul");
        Objects.requireNonNull(lastSelector, "El selector final no pot ser nul");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        MenuSnapshot<T, C> snapshot = menu.createSnapshot();
        List<MenuOption<T, C>> options = new ArrayList<>(snapshot.getOptionSnapshot());

        validateRange(range, options.size());

        Range effectiveRange = range.clamp(options.size());
        int from = effectiveRange.fromInclusive();
        int to = effectiveRange.toExclusive();
        int segmentSize = to - from;

        if (segmentSize < 2) {
            return menu;
        }

        List<MenuOption<T, C>> first = new ArrayList<>(segmentSize);
        List<MenuOption<T, C>> middle = new ArrayList<>(segmentSize);
        List<MenuOption<T, C>> last = new ArrayList<>(segmentSize);

        for (int index = from; index < to; index++) {
            MenuOption<T, C> option = options.get(index);

            boolean goesFirst = firstSelector.test(index, option);
            boolean goesLast = lastSelector.test(index, option);

            if (goesFirst && goesLast) {
                throw new IllegalArgumentException(
                        "Una mateixa opció no pot anar al principi i al final alhora. Índex: " + index);
            }

            if (goesFirst) {
                first.add(option);
            } else if (goesLast) {
                last.add(option);
            } else {
                middle.add(option);
            }
        }

        middle.sort(comparator);

        int writeIndex = from;
        for (MenuOption<T, C> option : first) {
            options.set(writeIndex++, option);
        }
        for (MenuOption<T, C> option : middle) {
            options.set(writeIndex++, option);
        }
        for (MenuOption<T, C> option : last) {
            options.set(writeIndex++, option);
        }

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
                String newLabel = Objects.requireNonNull(entry.getValue(),
                        "El nou label no pot ser nul");

                validateExistingIndex(index, options.size());

                MenuOption<T, C> current = options.get(index);
                options.set(index, newOption(newLabel, current.action()));
            }
        }

        else if (hasActionReplacements) {
            for (Map.Entry<Integer, MenuRuntimeAction<T, C>> entry : actionReplacements.entrySet()) {
                int index = entry.getKey();
                MenuRuntimeAction<T, C> newAction = Objects.requireNonNull(
                        entry.getValue(),
                        "El nou comportament no pot ser nul");

                validateExistingIndex(index, options.size());

                MenuOption<T, C> current = options.get(index);
                options.set(index, newOption(current.label(), newAction));
            }
        }

        else if (hasOptionReplacements) {
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

    private static <T, C> Set<Integer> collectMatchingIndexes(
            List<MenuOption<T, C>> options,
            OptionSelector<T, C> selector,
            Range range,
            int limit,
            boolean reverse) {

        if (limit < 0)
            throw new IllegalArgumentException("El límit no pot ser negatiu");

        if (limit == 0)
            return new LinkedHashSet<>();

        int capacity = Math.min(limit, range.toExclusive() - range.fromInclusive());
        Set<Integer> matches = LinkedHashSet.newLinkedHashSet(capacity);

        int from = range.fromInclusive();
        int to = range.toExclusive();

        if (!reverse) {
            for (int i = from; i < to; i++) {
                if (selector.test(i, options.get(i))) {
                    matches.add(i);
                    if (matches.size() >= limit) {
                        break;
                    }
                }
            }
            return matches;
        }

        for (int i = to - 1; i >= from; i--) {
            if (selector.test(i, options.get(i))) {
                matches.add(i);
                if (matches.size() >= limit) {
                    break;
                }
            }
        }

        return matches;
    }

    private static <T, C> OptionSelector<T, C> exactLabelSelector(String label) {
        return (index, option) -> Objects.equals(option.label(), label);
    }

    private static <T, C> Comparator<MenuOption<T, C>> defaultLabelComparator() {
        return Comparator.comparing(
                MenuOption<T, C>::label,
                String.CASE_INSENSITIVE_ORDER).thenComparing(MenuOption::label);
    }

    private static <T, C> List<MenuOption<T, C>> currentOptions(DynamicMenu<T, C> menu) {
        return new ArrayList<>(menu.getCurrentOptionSnapshot());
    }

    private static void validateExistingIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Índex fora de rang: " + index + ", mida actual: " + size);
        }
    }

    private static void validateRange(Range range, int size) {
        int from = range.fromInclusive();
        int to = range.toExclusive();

        if (from < 0)
            throw new IndexOutOfBoundsException("L'inici del rang no pot ser negatiu: " + from);

        if (to < 0)
            throw new IndexOutOfBoundsException("El final del rang no pot ser negatiu: " + to);

        if (from > to)
            throw new IllegalArgumentException("L'inici del rang no pot ser major que el final: " + from + " > " + to);

        if (from > size)
            throw new IndexOutOfBoundsException("L'inici del rang està fora de la mida actual: " + from + " > " + size);

        if (to > size && to != Integer.MAX_VALUE)
            throw new IndexOutOfBoundsException("El final del rang està fora de la mida actual: " + to + " > " + size);
    }

    private static <T, C> void rebuildSnapshot(MenuSnapshot<T, C> snapshot, List<MenuOption<T, C>> options) {
        snapshot.clearOptions();
        for (MenuOption<T, C> option : options) {
            snapshot.addOption(option.label(), option.action());
        }
    }

    private static <T, C> MenuOption<T, C> newOption(String label, MenuRuntimeAction<T, C> action) {
        Objects.requireNonNull(label, "El label no pot ser nul");
        Objects.requireNonNull(action, "El comportament no pot ser nul");
        return new MenuOption<>(label, action);
    }

    private static <T, C> MenuRuntimeAction<T, C> runtimeOf(MenuAction<T, C> action) {
        Objects.requireNonNull(action, "El comportament no pot ser nul");
        return (context, menu) -> action.execute(context);
    }

    private static <T, C> MenuRuntimeAction<T, C> runtimeOf(SimpleMenuAction<T> action) {
        Objects.requireNonNull(action, "El comportament no pot ser nul");
        return (context, menu) -> action.execute();
    }
}