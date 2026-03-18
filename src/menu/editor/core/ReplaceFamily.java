package menu.editor.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import menu.DynamicMenu;
import menu.action.*;
import menu.editor.EditConfig;
import menu.editor.Range;
import menu.editor.helpers.*;
import menu.model.MenuOption;
import menu.snapshot.MenuSnapshot;

import static menu.editor.core.MenuEditorSupport.*;

/** Utilitats de ReplaceFamily. */
public final class ReplaceFamily {
    private ReplaceFamily() {
        throw new AssertionError("No es pot instanciar ReplaceFamily");
    }

    // -------------------------------------------------------------------------
    // Replace by index
    // -------------------------------------------------------------------------

    /** Reemplaça l'etiqueta d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceLabelAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        return replaceAtInternal(menu, index, newLabel, null, true, false);
    }

    /** Reemplaça l'acció d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            MenuRuntimeAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAtInternal(menu, index, null, newAction, false, true);
    }

    /** Reemplaça l'acció d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            MenuAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceActionAt(menu, index, runtimeOf(newAction));
    }

    /** Reemplaça l'acció d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            SimpleMenuAction<T> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceActionAt(menu, index, runtimeOf(newAction));
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAtInternal(menu, index, newLabel, newAction, true, true);
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            MenuAction<T, C> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAt(menu, index, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return replaceAt(menu, index, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            MenuOption<T, C> newOption) {

        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        return replaceAt(menu, index, newOption.label(), newOption.action());
    }

    // -------------------------------------------------------------------------
    // Fluent entry points interns
    // -------------------------------------------------------------------------

    /** Inicia una operació de reemplaç. */
    public static <T, C> ReplaceOperation<T, C> replace(DynamicMenu<T, C> menu) {
        return new ReplaceOperation<>(menu);
    }

    /** Inicia una operació de reemplaç. */
    public static <T, C> ReplaceOperation<T, C> replace(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return replace(menu).where(selector);
    }

    /** Inicia un reemplaç per etiqueta exacta. */
    public static <T, C> ReplaceOperation<T, C> replaceLabel(
            DynamicMenu<T, C> menu,
            String targetLabel) {

        return replace(menu).where(exactLabelSelector(targetLabel));
    }

    // -------------------------------------------------------------------------
    // Replace by exact label
    // -------------------------------------------------------------------------

    /** Reemplaça la primera etiqueta coincident. */
    public static <T, C> boolean replaceFirstLabel(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return replaceLabel(menu, targetLabel)
                .label(newLabel)
                .first()
                .executeAny();
    }

    /** Reemplaça l'última etiqueta coincident. */
    public static <T, C> boolean replaceLastLabel(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return replaceLabel(menu, targetLabel)
                .label(newLabel)
                .last()
                .executeAny();
    }

    /** Reemplaça totes les etiquetes coincidents. */
    public static <T, C> int replaceAllLabels(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return replaceLabel(menu, targetLabel)
                .label(newLabel)
                .execute();
    }

    /** Reemplaça la primera acció coincident. */
    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceLabel(menu, targetLabel)
                .action(newAction)
                .first()
                .executeAny();
    }

    /** Reemplaça la primera acció coincident. */
    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return replaceFirstAction(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça la primera acció coincident. */
    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return replaceFirstAction(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça l'última acció coincident. */
    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceLabel(menu, targetLabel)
                .action(newAction)
                .last()
                .executeAny();
    }

    /** Reemplaça l'última acció coincident. */
    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return replaceLastAction(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça l'última acció coincident. */
    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return replaceLastAction(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça totes les accions coincidents. */
    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceLabel(menu, targetLabel)
                .action(newAction)
                .execute();
    }

    /** Reemplaça totes les accions coincidents. */
    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return replaceAllActions(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça totes les accions coincidents. */
    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return replaceAllActions(menu, targetLabel, runtimeOf(newAction));
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceLabel(menu, targetLabel)
                .option(newLabel, newAction)
                .first()
                .executeAny();
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return replaceFirst(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return replaceFirst(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuOption<T, C> newOption) {

        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        return replaceFirst(menu, targetLabel, newOption.label(), newOption.action());
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceLabel(menu, targetLabel)
                .option(newLabel, newAction)
                .last()
                .executeAny();
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return replaceLast(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return replaceLast(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuOption<T, C> newOption) {

        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        return replaceLast(menu, targetLabel, newOption.label(), newOption.action());
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        return replaceLabel(menu, targetLabel)
                .option(newLabel, newAction)
                .execute();
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return replaceAll(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return replaceAll(menu, targetLabel, newLabel, runtimeOf(newAction));
    }

    /** Reemplaça opcions del menú. */
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

    /** Reemplaça les opcions que compleixen la condició. */
    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return replace(menu, selector)
                .map(mapper)
                .execute();
    }

    /** Reemplaça les opcions que compleixen la condició. */
    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range) {

        return replace(menu, selector)
                .map(mapper)
                .range(range)
                .execute();
    }

    /** Reemplaça les opcions que compleixen la condició. */
    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range,
            int limit) {

        return replace(menu, selector)
                .map(mapper)
                .range(range)
                .limit(limit)
                .execute();
    }

    /** Reemplaça les opcions que compleixen la condició. */
    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            EditConfig config) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(mapper, "El transformador no pot ser nul");
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        return executeReplace(menu, selector, mapper, config);
    }

    // -------------------------------------------------------------------------
    // Replace if reverse
    // -------------------------------------------------------------------------

    /** Reemplaça coincidències en ordre invers. */
    public static <T, C> int replaceIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return replace(menu, selector)
                .map(mapper)
                .reverse()
                .execute();
    }

    /** Reemplaça coincidències en ordre invers. */
    public static <T, C> int replaceIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range) {

        return replace(menu, selector)
                .map(mapper)
                .range(range)
                .reverse()
                .execute();
    }

    /** Reemplaça coincidències en ordre invers. */
    public static <T, C> int replaceIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range,
            int limit) {

        return replace(menu, selector)
                .map(mapper)
                .range(range)
                .limit(limit)
                .reverse()
                .execute();
    }

    /** Reemplaça coincidències en ordre invers. */
    public static <T, C> int replaceIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            EditConfig config) {

        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        return replace(menu, selector)
                .map(mapper)
                .config(config)
                .reverse()
                .execute();
    }

    // -------------------------------------------------------------------------
    // Replace label if
    // -------------------------------------------------------------------------

    /** Reemplaça etiquetes segons la condició. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        return replace(menu, selector)
                .label(newLabel)
                .execute();
    }

    /** Reemplaça etiquetes segons la condició. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            Range range) {

        return replace(menu, selector)
                .label(newLabel)
                .range(range)
                .execute();
    }

    /** Reemplaça etiquetes segons la condició. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            Range range,
            int limit) {

        return replace(menu, selector)
                .label(newLabel)
                .range(range)
                .limit(limit)
                .execute();
    }

    /** Reemplaça etiquetes segons la condició. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            EditConfig config) {

        return replace(menu, selector)
                .label(newLabel)
                .config(config)
                .execute();
    }

    /** Reemplaça etiquetes segons la condició. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        return replace(menu, selector)
                .label(mapper)
                .execute();
    }

    /** Reemplaça etiquetes segons la condició. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            Range range) {

        return replace(menu, selector)
                .label(mapper)
                .range(range)
                .execute();
    }

    /** Reemplaça etiquetes segons la condició. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            Range range,
            int limit) {

        return replace(menu, selector)
                .label(mapper)
                .range(range)
                .limit(limit)
                .execute();
    }

    /** Reemplaça etiquetes segons la condició. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            EditConfig config) {

        return replace(menu, selector)
                .label(mapper)
                .config(config)
                .execute();
    }

    // -------------------------------------------------------------------------
    // Replace label if reverse
    // -------------------------------------------------------------------------

    /** Reemplaça etiquetes en ordre invers. */
    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        return replace(menu, selector)
                .label(newLabel)
                .reverse()
                .execute();
    }

    /** Reemplaça etiquetes en ordre invers. */
    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            Range range) {

        return replace(menu, selector)
                .label(newLabel)
                .range(range)
                .reverse()
                .execute();
    }

    /** Reemplaça etiquetes en ordre invers. */
    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            Range range,
            int limit) {

        return replace(menu, selector)
                .label(newLabel)
                .range(range)
                .limit(limit)
                .reverse()
                .execute();
    }

    /** Reemplaça etiquetes en ordre invers. */
    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            EditConfig config) {

        return replace(menu, selector)
                .label(newLabel)
                .config(config)
                .reverse()
                .execute();
    }

    /** Reemplaça etiquetes en ordre invers. */
    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        return replace(menu, selector)
                .label(mapper)
                .reverse()
                .execute();
    }

    /** Reemplaça etiquetes en ordre invers. */
    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            Range range) {

        return replace(menu, selector)
                .label(mapper)
                .range(range)
                .reverse()
                .execute();
    }

    /** Reemplaça etiquetes en ordre invers. */
    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            Range range,
            int limit) {

        return replace(menu, selector)
                .label(mapper)
                .range(range)
                .limit(limit)
                .reverse()
                .execute();
    }

    /** Reemplaça etiquetes en ordre invers. */
    public static <T, C> int replaceLabelIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            EditConfig config) {

        return replace(menu, selector)
                .label(mapper)
                .config(config)
                .reverse()
                .execute();
    }

    /** Reemplaça l'etiqueta de la primera coincidència. */
    public static <T, C> boolean replaceFirstLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        return replace(menu, selector)
                .label(newLabel)
                .first()
                .executeAny();
    }

    /** Reemplaça l'etiqueta de la primera coincidència. */
    public static <T, C> boolean replaceFirstLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        return replace(menu, selector)
                .label(mapper)
                .first()
                .executeAny();
    }

    /** Reemplaça l'etiqueta de l'última coincidència. */
    public static <T, C> boolean replaceLastLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        return replace(menu, selector)
                .label(newLabel)
                .last()
                .executeAny();
    }

    /** Reemplaça l'etiqueta de l'última coincidència. */
    public static <T, C> boolean replaceLastLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        return replace(menu, selector)
                .label(mapper)
                .last()
                .executeAny();
    }

    // -------------------------------------------------------------------------
    // Replace action if
    // -------------------------------------------------------------------------

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        return replace(menu, selector)
                .action(newAction)
                .execute();
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return replaceActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return replaceActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            Range range) {

        return replace(menu, selector)
                .action(newAction)
                .range(range)
                .execute();
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range);
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range);
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            Range range,
            int limit) {

        return replace(menu, selector)
                .action(newAction)
                .range(range)
                .limit(limit)
                .execute();
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range,
            int limit) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range, limit);
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range,
            int limit) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), range, limit);
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            EditConfig config) {

        return replace(menu, selector)
                .action(newAction)
                .config(config)
                .execute();
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            EditConfig config) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), config);
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            EditConfig config) {

        return replaceActionIf(menu, selector, runtimeOf(newAction), config);
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        return replace(menu, selector)
                .action(mapper)
                .execute();
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            Range range) {

        return replace(menu, selector)
                .action(mapper)
                .range(range)
                .execute();
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            Range range,
            int limit) {

        return replace(menu, selector)
                .action(mapper)
                .range(range)
                .limit(limit)
                .execute();
    }

    /** Reemplaça accions segons la condició. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            EditConfig config) {

        return replace(menu, selector)
                .action(mapper)
                .config(config)
                .execute();
    }

    // -------------------------------------------------------------------------
    // Replace action if reverse
    // -------------------------------------------------------------------------

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        return replace(menu, selector)
                .action(newAction)
                .reverse()
                .execute();
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            Range range) {

        return replace(menu, selector)
                .action(newAction)
                .range(range)
                .reverse()
                .execute();
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), range);
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), range);
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            Range range,
            int limit) {

        return replace(menu, selector)
                .action(newAction)
                .range(range)
                .limit(limit)
                .reverse()
                .execute();
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range,
            int limit) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), range, limit);
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range,
            int limit) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), range, limit);
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            EditConfig config) {

        return replace(menu, selector)
                .action(newAction)
                .config(config)
                .reverse()
                .execute();
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            EditConfig config) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), config);
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            EditConfig config) {

        return replaceActionIfReverse(menu, selector, runtimeOf(newAction), config);
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        return replace(menu, selector)
                .action(mapper)
                .reverse()
                .execute();
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            Range range) {

        return replace(menu, selector)
                .action(mapper)
                .range(range)
                .reverse()
                .execute();
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            Range range,
            int limit) {

        return replace(menu, selector)
                .action(mapper)
                .range(range)
                .limit(limit)
                .reverse()
                .execute();
    }

    /** Reemplaça accions en ordre invers. */
    public static <T, C> int replaceActionIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            EditConfig config) {

        return replace(menu, selector)
                .action(mapper)
                .config(config)
                .reverse()
                .execute();
    }

    /** Reemplaça l'acció de la primera coincidència. */
    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        return replace(menu, selector)
                .action(newAction)
                .first()
                .executeAny();
    }

    /** Reemplaça l'acció de la primera coincidència. */
    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return replaceFirstActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça l'acció de la primera coincidència. */
    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return replaceFirstActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça l'acció de la primera coincidència. */
    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        return replace(menu, selector)
                .action(mapper)
                .first()
                .executeAny();
    }

    /** Reemplaça l'acció de l'última coincidència. */
    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        return replace(menu, selector)
                .action(newAction)
                .last()
                .executeAny();
    }

    /** Reemplaça l'acció de l'última coincidència. */
    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return replaceLastActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça l'acció de l'última coincidència. */
    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return replaceLastActionIf(menu, selector, runtimeOf(newAction));
    }

    /** Reemplaça l'acció de l'última coincidència. */
    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        return replace(menu, selector)
                .action(mapper)
                .last()
                .executeAny();
    }

    // -------------------------------------------------------------------------
    // First / last generic replace
    // -------------------------------------------------------------------------

    /** Reemplaça la primera coincidència. */
    public static <T, C> boolean replaceFirstIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return replace(menu, selector)
                .map(mapper)
                .first()
                .executeAny();
    }

    /** Reemplaça l'última coincidència. */
    public static <T, C> boolean replaceLastIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return replace(menu, selector)
                .map(mapper)
                .last()
                .executeAny();
    }

    // -------------------------------------------------------------------------
    // Batch replacements
    // -------------------------------------------------------------------------

    /** Reemplaça opcions del menú. */
    public static <T, C> DynamicMenu<T, C> replaceLabelsAt(
            DynamicMenu<T, C> menu,
            Map<Integer, String> replacements) {

        Objects.requireNonNull(replacements, "El mapa de reemplaços no pot ser nul");
        return replaceBatch(menu, replacements, null, null);
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> DynamicMenu<T, C> replaceActionsAt(
            DynamicMenu<T, C> menu,
            Map<Integer, MenuRuntimeAction<T, C>> replacements) {

        Objects.requireNonNull(replacements, "El mapa de reemplaços no pot ser nul");
        return replaceBatch(menu, null, replacements, null);
    }

    /** Reemplaça opcions del menú. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            Map<Integer, MenuOption<T, C>> replacements) {

        Objects.requireNonNull(replacements, "El mapa de reemplaços no pot ser nul");
        return replaceBatch(menu, null, null, replacements);
    }

    // -------------------------------------------------------------------------
    // Nucli intern únic
    // -------------------------------------------------------------------------

    /** Executa aquesta operació. */
    private static <T, C> int executeReplace(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            EditConfig config) {

        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        return executeReplace(
                menu,
                selector,
                mapper,
                config.range(),
                config.limit(),
                config.reverse());
    }

    // -------------------------------------------------------------------------
    // Fluent operation interna
    // -------------------------------------------------------------------------

    public static final class ReplaceOperation<T, C> {
        private final DynamicMenu<T, C> menu;
        private OptionSelector<T, C> selector;
        private OptionMapper<T, C> mapper;
        private Range range = Range.all();
        private int limit = Integer.MAX_VALUE;
        private boolean reverse = false;

        /** Reemplaça opcions del menú. */
        private ReplaceOperation(DynamicMenu<T, C> menu) {
            this.menu = Objects.requireNonNull(menu, "El menú no pot ser nul");
        }

        /** Defineix la condició de selecció. */
        public ReplaceOperation<T, C> where(OptionSelector<T, C> selector) {
            this.selector = Objects.requireNonNull(selector, "La condició no pot ser nul·la");
            return this;
        }

        /** Defineix el transformador a aplicar. */
        public ReplaceOperation<T, C> map(OptionMapper<T, C> mapper) {
            this.mapper = Objects.requireNonNull(mapper, "El transformador no pot ser nul");
            return this;
        }

        /** Defineix una selecció o substitució per etiqueta. */
        public ReplaceOperation<T, C> label(String newLabel) {
            Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
            return map((index, option) -> newOption(newLabel, option.action()));
        }

        /** Defineix una selecció o substitució per etiqueta. */
        public ReplaceOperation<T, C> label(LabelMapper<T, C> mapper) {
            Objects.requireNonNull(mapper, "El transformador de labels no pot ser nul");
            return map((index, option) -> newOption(
                    Objects.requireNonNull(
                            mapper.map(index, option),
                            "El transformador de labels no pot retornar nul"),
                    option.action()));
        }

        /** Defineix l'acció de reemplaç. */
        public ReplaceOperation<T, C> action(MenuRuntimeAction<T, C> newAction) {
            Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
            return map((index, option) -> newOption(option.label(), newAction));
        }

        /** Defineix l'acció de reemplaç. */
        public ReplaceOperation<T, C> action(MenuAction<T, C> newAction) {
            Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
            return action(runtimeOf(newAction));
        }

        /** Defineix l'acció de reemplaç. */
        public ReplaceOperation<T, C> action(SimpleMenuAction<T> newAction) {
            Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
            return action(runtimeOf(newAction));
        }

        /** Defineix l'acció de reemplaç. */
        public ReplaceOperation<T, C> action(ActionMapper<T, C> mapper) {
            Objects.requireNonNull(mapper, "El transformador de comportaments no pot ser nul");
            return map((index, option) -> newOption(
                    option.label(),
                    Objects.requireNonNull(
                            mapper.map(index, option),
                            "El transformador de comportaments no pot retornar nul")));
        }

        /** Defineix l'opció de reemplaç. */
        public ReplaceOperation<T, C> option(String newLabel, MenuRuntimeAction<T, C> newAction) {
            Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
            Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
            return map((index, option) -> newOption(newLabel, newAction));
        }

        /** Defineix l'opció de reemplaç. */
        public ReplaceOperation<T, C> option(String newLabel, MenuAction<T, C> newAction) {
            Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
            return option(newLabel, runtimeOf(newAction));
        }

        /** Defineix l'opció de reemplaç. */
        public ReplaceOperation<T, C> option(String newLabel, SimpleMenuAction<T> newAction) {
            Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
            return option(newLabel, runtimeOf(newAction));
        }

        /** Defineix l'opció de reemplaç. */
        public ReplaceOperation<T, C> option(MenuOption<T, C> newOption) {
            Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
            return map((index, option) -> newOption);
        }

        /** Defineix el rang d'actuació. */
        public ReplaceOperation<T, C> range(Range range) {
            this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
            return this;
        }

        /** Defineix el rang d'actuació. */
        public ReplaceOperation<T, C> range(int fromInclusive, int toExclusive) {
            return range(Range.of(fromInclusive, toExclusive));
        }

        /** Defineix el límit d'elements afectats. */
        public ReplaceOperation<T, C> limit(int limit) {
            this.limit = limit;
            return this;
        }

        /** Configura el recorregut en sentit invers. */
        public ReplaceOperation<T, C> reverse() {
            this.reverse = true;
            return this;
        }

        /** Configura el recorregut en sentit invers. */
        public ReplaceOperation<T, C> reverse(boolean reverse) {
            this.reverse = reverse;
            return this;
        }

        /** Limita l'operació a la primera coincidència. */
        public ReplaceOperation<T, C> first() {
            this.limit = 1;
            this.reverse = false;
            return this;
        }

        /** Limita l'operació a l'última coincidència. */
        public ReplaceOperation<T, C> last() {
            this.limit = 1;
            this.reverse = true;
            return this;
        }

        /** Considera totes les coincidències. */
        public ReplaceOperation<T, C> all() {
            this.limit = Integer.MAX_VALUE;
            return this;
        }

        /** Aplica una configuració base. */
        public ReplaceOperation<T, C> config(EditConfig config) {
            Objects.requireNonNull(config, "La configuració no pot ser nul·la");
            this.range = config.range();
            this.limit = config.limit();
            this.reverse = config.reverse();
            return this;
        }

        /** Construeix la configuració efectiva. */
        public EditConfig buildConfig() {
            return EditConfig.builder()
                    .range(range)
                    .limit(limit)
                    .reverse(reverse)
                    .build();
        }

        /** Executa l'operació. */
        public int execute() {
            return executeReplace(
                    menu,
                    Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                    Objects.requireNonNull(mapper, "El transformador no pot ser nul"),
                    range,
                    limit,
                    reverse);
        }

        /** Executa l'operació i indica si hi ha canvis. */
        public boolean executeAny() {
            return execute() > 0;
        }
    }

    // -------------------------------------------------------------------------
    // Internals by index / batch
    // -------------------------------------------------------------------------

    /** Reemplaça una opció per índex. */
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
        snapshot.setOptionAt(index, newOption(finalLabel, finalAction));
        return menu.restoreSnapshot(snapshot);
    }

    /** Aplica un conjunt de reemplaços per índex. */
    private static <T, C> DynamicMenu<T, C> replaceBatch(
            DynamicMenu<T, C> menu,
            Map<Integer, String> labelReplacements,
            Map<Integer, MenuRuntimeAction<T, C>> actionReplacements,
            Map<Integer, MenuOption<T, C>> optionReplacements) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");

        boolean hasLabelReplacements = labelReplacements != null && !labelReplacements.isEmpty();
        boolean hasActionReplacements = actionReplacements != null && !actionReplacements.isEmpty();
        boolean hasOptionReplacements = optionReplacements != null && !optionReplacements.isEmpty();

        if (!hasLabelReplacements && !hasActionReplacements && !hasOptionReplacements) {
            return menu;
        }

        MenuSnapshot<T, C> snapshot = menu.createSnapshot();
        List<MenuOption<T, C>> options = snapshot.getOptionSnapshot();

        if (options.isEmpty()) {
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
                snapshot.setOptionAt(index, newOption(newLabel, current.action()));
            }
        } else if (hasActionReplacements) {
            for (Map.Entry<Integer, MenuRuntimeAction<T, C>> entry : actionReplacements.entrySet()) {
                int index = entry.getKey();
                MenuRuntimeAction<T, C> newAction = Objects.requireNonNull(
                        entry.getValue(),
                        "El nou comportament no pot ser nul");

                validateExistingIndex(index, options.size());

                MenuOption<T, C> current = options.get(index);
                snapshot.setOptionAt(index, newOption(current.label(), newAction));
            }
        } else if (hasOptionReplacements) {
            for (Map.Entry<Integer, MenuOption<T, C>> entry : optionReplacements.entrySet()) {
                int index = entry.getKey();
                MenuOption<T, C> replacementOption = Objects.requireNonNull(
                        entry.getValue(),
                        "La nova opció no pot ser nul·la");

                validateExistingIndex(index, options.size());
                snapshot.setOptionAt(index, replacementOption);
            }
        }

        return menu.restoreSnapshot(snapshot);
    }

    /** Executa aquesta operació. */
    private static <T, C> int executeReplace(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range,
            int limit,
            boolean reverse) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(mapper, "El transformador no pot ser nul");
        Objects.requireNonNull(range, "El rang no pot ser nul");

        if (limit == 0) {
            return 0;
        }

        MenuSnapshot<T, C> snapshot = menu.createSnapshot();
        List<MenuOption<T, C>> options = new ArrayList<>(snapshot.getOptionSnapshot());

        if (options.isEmpty()) {
            return 0;
        }

        validateRange(range, options.size());
        Range effectiveRange = range.clamp(options.size());

        int replaced = 0;

        if (!reverse) {
            int index = effectiveRange.fromInclusive();
            while (index < effectiveRange.toExclusive() && replaced < limit) {
                MenuOption<T, C> current = options.get(index);
                if (selector.test(index, current)) {
                    MenuOption<T, C> mapped = Objects.requireNonNull(
                            mapper.map(index, current),
                            "El transformador no pot retornar una opció nul·la");

                    options.set(index, mapped);
                    replaced++;
                }
                index++;
            }
        } else {
            int index = effectiveRange.toExclusive() - 1;
            while (index >= effectiveRange.fromInclusive() && replaced < limit) {
                MenuOption<T, C> current = options.get(index);
                if (selector.test(index, current)) {
                    MenuOption<T, C> mapped = Objects.requireNonNull(
                            mapper.map(index, current),
                            "El transformador no pot retornar una opció nul·la");

                    options.set(index, mapped);
                    replaced++;
                }
                index--;
            }
        }

        if (replaced == 0) {
            return 0;
        }

        rebuildSnapshot(snapshot, options);
        menu.restoreSnapshot(snapshot);
        return replaced;
    }
}
