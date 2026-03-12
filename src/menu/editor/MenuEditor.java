package menu.editor;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import menu.DynamicMenu;
import menu.action.*;
import menu.editor.helpers.*;
import menu.model.MenuOption;

import menu.editor.core.*;

/** Utilitats avançades d'edició per a {@link DynamicMenu}. */
public final class MenuEditor {

    private MenuEditor() {
        throw new AssertionError("No es pot instanciar MenuEditor");
    }

    public static <T, C> OptionSelector<T, C> alwaysFalseSelector() {
        return MenuEditorSupport.alwaysFalseSelector();
    }

    public static <T, C> OptionSelector<T, C> alwaysTrueSelector() {
        return MenuEditorSupport.alwaysTrueSelector();
    }

    // -------------------------------------------------------------------------
    // Replace by index
    // -------------------------------------------------------------------------

    /** Reemplaça el label d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceLabelAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel) {

        return ReplaceFamily.replaceLabelAt(menu, index, newLabel);
    }

    /** Reemplaça el comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            MenuRuntimeAction<T, C> newAction) {

        return ReplaceFamily.replaceActionAt(menu, index, newAction);
    }

    /** Reemplaça el comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            MenuAction<T, C> newAction) {

        return ReplaceFamily.replaceActionAt(menu, index, newAction);
    }

    /** Reemplaça el comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceActionAt(
            DynamicMenu<T, C> menu,
            int index,
            SimpleMenuAction<T> newAction) {

        return ReplaceFamily.replaceActionAt(menu, index, newAction);
    }

    /** Reemplaça label i comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        return ReplaceFamily.replaceAt(menu, index, newLabel, newAction);
    }

    /** Reemplaça label i comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            MenuAction<T, C> newAction) {

        return ReplaceFamily.replaceAt(menu, index, newLabel, newAction);
    }

    /** Reemplaça label i comportament d'una opció per índex. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return ReplaceFamily.replaceAt(menu, index, newLabel, newAction);
    }

    /** Reemplaça una opció completa per índex. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            int index,
            MenuOption<T, C> newOption) {

        return ReplaceFamily.replaceAt(menu, index, newOption);
    }

    // -------------------------------------------------------------------------
    // Replace by exact label
    // -------------------------------------------------------------------------

    /** Reemplaça el label de la primera coincidència exacta. */
    public static <T, C> boolean replaceFirstLabel(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return ReplaceFamily.replaceFirstLabel(menu, targetLabel, newLabel);
    }

    /** Reemplaça el label de l'última coincidència exacta. */
    public static <T, C> boolean replaceLastLabel(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return ReplaceFamily.replaceLastLabel(menu, targetLabel, newLabel);
    }

    /** Reemplaça el label de totes les coincidències exactes. */
    public static <T, C> int replaceAllLabels(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel) {

        return ReplaceFamily.replaceAllLabels(menu, targetLabel, newLabel);
    }

    /** Reemplaça el comportament de la primera coincidència exacta. */
    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return ReplaceFamily.replaceFirstAction(menu, targetLabel, newAction);
    }

    /** Reemplaça el comportament de la primera coincidència exacta. */
    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return ReplaceFamily.replaceFirstAction(menu, targetLabel, newAction);
    }

    /** Reemplaça el comportament de la primera coincidència exacta. */
    public static <T, C> boolean replaceFirstAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return ReplaceFamily.replaceFirstAction(menu, targetLabel, newAction);
    }

    /** Reemplaça el comportament de l'última coincidència exacta. */
    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return ReplaceFamily.replaceLastAction(menu, targetLabel, newAction);
    }

    /** Reemplaça el comportament de l'última coincidència exacta. */
    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return ReplaceFamily.replaceLastAction(menu, targetLabel, newAction);
    }

    /** Reemplaça el comportament de l'última coincidència exacta. */
    public static <T, C> boolean replaceLastAction(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return ReplaceFamily.replaceLastAction(menu, targetLabel, newAction);
    }

    /** Reemplaça el comportament de totes les coincidències exactes. */
    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuRuntimeAction<T, C> newAction) {

        return ReplaceFamily.replaceAllActions(menu, targetLabel, newAction);
    }

    /** Reemplaça el comportament de totes les coincidències exactes. */
    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuAction<T, C> newAction) {

        return ReplaceFamily.replaceAllActions(menu, targetLabel, newAction);
    }

    /** Reemplaça el comportament de totes les coincidències exactes. */
    public static <T, C> int replaceAllActions(
            DynamicMenu<T, C> menu,
            String targetLabel,
            SimpleMenuAction<T> newAction) {

        return ReplaceFamily.replaceAllActions(menu, targetLabel, newAction);
    }

    /** Reemplaça la primera coincidència exacta. */
    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        return ReplaceFamily.replaceFirst(menu, targetLabel, newLabel, newAction);
    }

    /** Reemplaça la primera coincidència exacta. */
    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return ReplaceFamily.replaceFirst(menu, targetLabel, newLabel, newAction);
    }

    /** Reemplaça la primera coincidència exacta. */
    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return ReplaceFamily.replaceFirst(menu, targetLabel, newLabel, newAction);
    }

    /** Reemplaça la primera coincidència exacta. */
    public static <T, C> boolean replaceFirst(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuOption<T, C> newOption) {

        return ReplaceFamily.replaceFirst(menu, targetLabel, newOption);
    }

    /** Reemplaça l'última coincidència exacta. */
    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        return ReplaceFamily.replaceLast(menu, targetLabel, newLabel, newAction);
    }

    /** Reemplaça l'última coincidència exacta. */
    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return ReplaceFamily.replaceLast(menu, targetLabel, newLabel, newAction);
    }

    /** Reemplaça l'última coincidència exacta. */
    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return ReplaceFamily.replaceLast(menu, targetLabel, newLabel, newAction);
    }

    /** Reemplaça l'última coincidència exacta. */
    public static <T, C> boolean replaceLast(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuOption<T, C> newOption) {

        return ReplaceFamily.replaceLast(menu, targetLabel, newOption);
    }

    /** Reemplaça totes les coincidències exactes. */
    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuRuntimeAction<T, C> newAction) {

        return ReplaceFamily.replaceAll(menu, targetLabel, newLabel, newAction);
    }

    /** Reemplaça totes les coincidències exactes. */
    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            MenuAction<T, C> newAction) {

        return ReplaceFamily.replaceAll(menu, targetLabel, newLabel, newAction);
    }

    /** Reemplaça totes les coincidències exactes. */
    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            String newLabel,
            SimpleMenuAction<T> newAction) {

        return ReplaceFamily.replaceAll(menu, targetLabel, newLabel, newAction);
    }

    /** Reemplaça totes les coincidències exactes. */
    public static <T, C> int replaceAll(
            DynamicMenu<T, C> menu,
            String targetLabel,
            MenuOption<T, C> newOption) {

        return ReplaceFamily.replaceAll(menu, targetLabel, newOption);
    }

    // -------------------------------------------------------------------------
    // Replace if
    // -------------------------------------------------------------------------

    /** Reemplaça totes les opcions que compleixen una condició. */
    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return ReplaceFamily.replaceIf(menu, selector, mapper);
    }

    /** Reemplaça opcions dins d'un rang. */
    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range) {

        return ReplaceFamily.replaceIf(menu, selector, mapper, range);
    }

    /** Reemplaça opcions dins d'un rang i amb límit. */
    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            Range range,
            int limit) {

        return ReplaceFamily.replaceIf(menu, selector, mapper, range, limit);
    }

    /** Reemplaça opcions segons una configuració. */
    public static <T, C> int replaceIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper,
            EditConfig config) {

        return ReplaceFamily.replaceIf(menu, selector, mapper, config);
    }

    // -------------------------------------------------------------------------
    // Replace label if
    // -------------------------------------------------------------------------

    /** Reemplaça només labels. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        return ReplaceFamily.replaceLabelIf(menu, selector, newLabel);
    }

    /** Reemplaça només labels dins d'un rang. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            Range range) {

        return ReplaceFamily.replaceLabelIf(menu, selector, newLabel, range);
    }

    /** Reemplaça només labels dins d'un rang i amb límit. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            Range range,
            int limit) {

        return ReplaceFamily.replaceLabelIf(menu, selector, newLabel, range, limit);
    }

    /** Reemplaça només labels segons una configuració. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel,
            EditConfig config) {

        return ReplaceFamily.replaceLabelIf(menu, selector, newLabel, config);
    }

    /** Reemplaça només labels. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        return ReplaceFamily.replaceLabelIf(menu, selector, mapper);
    }

    /** Reemplaça només labels dins d'un rang. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            Range range) {

        return ReplaceFamily.replaceLabelIf(menu, selector, mapper, range);
    }

    /** Reemplaça només labels dins d'un rang i amb límit. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            Range range,
            int limit) {

        return ReplaceFamily.replaceLabelIf(menu, selector, mapper, range, limit);
    }

    /** Reemplaça només labels segons una configuració. */
    public static <T, C> int replaceLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper,
            EditConfig config) {

        return ReplaceFamily.replaceLabelIf(menu, selector, mapper, config);
    }

    /** Reemplaça el label de la primera opció que compleix una condició. */
    public static <T, C> boolean replaceFirstLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        return ReplaceFamily.replaceFirstLabelIf(menu, selector, newLabel);
    }

    /** Reemplaça el label de la primera opció que compleix una condició. */
    public static <T, C> boolean replaceFirstLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        return ReplaceFamily.replaceFirstLabelIf(menu, selector, mapper);
    }

    /** Reemplaça el label de l'última opció que compleix una condició. */
    public static <T, C> boolean replaceLastLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            String newLabel) {

        return ReplaceFamily.replaceLastLabelIf(menu, selector, newLabel);
    }

    /** Reemplaça el label de l'última opció que compleix una condició. */
    public static <T, C> boolean replaceLastLabelIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            LabelMapper<T, C> mapper) {

        return ReplaceFamily.replaceLastLabelIf(menu, selector, mapper);
    }

    // -------------------------------------------------------------------------
    // Replace action if
    // -------------------------------------------------------------------------

    /** Reemplaça només comportaments. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction);
    }

    /** Reemplaça només comportaments. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction);
    }

    /** Reemplaça només comportaments. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction);
    }

    /** Reemplaça només comportaments dins d'un rang. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            Range range) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction, range);
    }

    /** Reemplaça només comportaments dins d'un rang. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction, range);
    }

    /** Reemplaça només comportaments dins d'un rang. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction, range);
    }

    /** Reemplaça només comportaments dins d'un rang i amb límit. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            Range range,
            int limit) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction, range, limit);
    }

    /** Reemplaça només comportaments dins d'un rang i amb límit. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            Range range,
            int limit) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction, range, limit);
    }

    /** Reemplaça només comportaments dins d'un rang i amb límit. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            Range range,
            int limit) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction, range, limit);
    }

    /** Reemplaça només comportaments segons una configuració. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction,
            EditConfig config) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction, config);
    }

    /** Reemplaça només comportaments segons una configuració. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction,
            EditConfig config) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction, config);
    }

    /** Reemplaça només comportaments segons una configuració. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction,
            EditConfig config) {

        return ReplaceFamily.replaceActionIf(menu, selector, newAction, config);
    }

    /** Reemplaça només comportaments. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        return ReplaceFamily.replaceActionIf(menu, selector, mapper);
    }

    /** Reemplaça només comportaments dins d'un rang. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            Range range) {

        return ReplaceFamily.replaceActionIf(menu, selector, mapper, range);
    }

    /** Reemplaça només comportaments dins d'un rang i amb límit. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            Range range,
            int limit) {

        return ReplaceFamily.replaceActionIf(menu, selector, mapper, range, limit);
    }

    /** Reemplaça només comportaments segons una configuració. */
    public static <T, C> int replaceActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper,
            EditConfig config) {

        return ReplaceFamily.replaceActionIf(menu, selector, mapper, config);
    }

    /** Reemplaça el comportament de la primera opció que compleix una condició. */
    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        return ReplaceFamily.replaceFirstActionIf(menu, selector, newAction);
    }

    /** Reemplaça el comportament de la primera opció que compleix una condició. */
    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return ReplaceFamily.replaceFirstActionIf(menu, selector, newAction);
    }

    /** Reemplaça el comportament de la primera opció que compleix una condició. */
    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return ReplaceFamily.replaceFirstActionIf(menu, selector, newAction);
    }

    /** Reemplaça el comportament de la primera opció que compleix una condició. */
    public static <T, C> boolean replaceFirstActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        return ReplaceFamily.replaceFirstActionIf(menu, selector, mapper);
    }

    /** Reemplaça el comportament de l'última opció que compleix una condició. */
    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuRuntimeAction<T, C> newAction) {

        return ReplaceFamily.replaceLastActionIf(menu, selector, newAction);
    }

    /** Reemplaça el comportament de l'última opció que compleix una condició. */
    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            MenuAction<T, C> newAction) {

        return ReplaceFamily.replaceLastActionIf(menu, selector, newAction);
    }

    /** Reemplaça el comportament de l'última opció que compleix una condició. */
    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            SimpleMenuAction<T> newAction) {

        return ReplaceFamily.replaceLastActionIf(menu, selector, newAction);
    }

    /** Reemplaça el comportament de l'última opció que compleix una condició. */
    public static <T, C> boolean replaceLastActionIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            ActionMapper<T, C> mapper) {

        return ReplaceFamily.replaceLastActionIf(menu, selector, mapper);
    }

    // -------------------------------------------------------------------------
    // First / last generic replace
    // -------------------------------------------------------------------------

    /** Reemplaça la primera opció que compleix una condició. */
    public static <T, C> boolean replaceFirstIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return ReplaceFamily.replaceFirstIf(menu, selector, mapper);
    }

    /** Reemplaça l'última opció que compleix una condició. */
    public static <T, C> boolean replaceLastIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            OptionMapper<T, C> mapper) {

        return ReplaceFamily.replaceLastIf(menu, selector, mapper);
    }

    // -------------------------------------------------------------------------
    // Batch replacements
    // -------------------------------------------------------------------------

    /** Reemplaça diversos labels per índex. */
    public static <T, C> DynamicMenu<T, C> replaceLabelsAt(
            DynamicMenu<T, C> menu,
            Map<Integer, String> replacements) {

        return ReplaceFamily.replaceLabelsAt(menu, replacements);
    }

    /** Reemplaça diversos comportaments per índex. */
    public static <T, C> DynamicMenu<T, C> replaceActionsAt(
            DynamicMenu<T, C> menu,
            Map<Integer, MenuRuntimeAction<T, C>> replacements) {

        return ReplaceFamily.replaceActionsAt(menu, replacements);
    }

    /** Reemplaça diverses opcions completes per índex. */
    public static <T, C> DynamicMenu<T, C> replaceAt(
            DynamicMenu<T, C> menu,
            Map<Integer, MenuOption<T, C>> replacements) {

        return ReplaceFamily.replaceAt(menu, replacements);
    }

    // -------------------------------------------------------------------------
    // Remove if
    // -------------------------------------------------------------------------

    /** Elimina totes les opcions que compleixen una condició. */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return RemoveFamily.removeIf(menu, selector);
    }

    /** Elimina totes les opcions que compleixen una condició dins d'un rang. */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return RemoveFamily.removeIf(menu, selector, range);
    }

    /** Elimina opcions que compleixen una condició dins d'un rang i amb límit. */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range,
            int limit) {

        return RemoveFamily.removeIf(menu, selector, range, limit);
    }

    /** Elimina opcions que compleixen una condició segons una configuració. */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            EditConfig config) {

        return RemoveFamily.removeIf(menu, selector, config);
    }

    /** Elimina la primera opció que compleix una condició. */
    public static <T, C> boolean removeFirstIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return RemoveFamily.removeFirstIf(menu, selector);
    }

    /** Elimina l'última opció que compleix una condició. */
    public static <T, C> boolean removeLastIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return RemoveFamily.removeLastIf(menu, selector);
    }

    /** Elimina la primera coincidència exacta. */
    public static <T, C> boolean removeFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return RemoveFamily.removeFirstLabel(menu, label);
    }

    /** Elimina l'última coincidència exacta. */
    public static <T, C> boolean removeLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return RemoveFamily.removeLastLabel(menu, label);
    }

    /** Elimina totes les coincidències exactes. */
    public static <T, C> int removeAllLabels(
            DynamicMenu<T, C> menu,
            String label) {

        return RemoveFamily.removeAllLabels(menu, label);
    }

    // -------------------------------------------------------------------------
    // Sorting
    // -------------------------------------------------------------------------

    /** Ordena totes les opcions per label. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(DynamicMenu<T, C> menu) {
        return SortFamily.sortByLabel(menu);
    }

    /** Ordena totes les opcions per label amb un comparador. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator) {

        return SortFamily.sortByLabel(menu, comparator);
    }

    /** Ordena les opcions per label dins d'un rang. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Range range) {

        return SortFamily.sortByLabel(menu, range);
    }

    /** Ordena les opcions per label dins d'un rang amb comparador. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Range range) {

        return SortFamily.sortByLabel(menu, comparator, range);
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        return SortFamily.sortByLabel(menu, firstSelector, lastSelector);
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        return SortFamily.sortByLabel(menu, firstSelector, lastSelector, range);
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector) {

        return SortFamily.sortByLabel(menu, comparator, firstSelector, lastSelector);
    }

    /** Ordena per label fixant opcions al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            OptionSelector<T, C> firstSelector,
            OptionSelector<T, C> lastSelector,
            Range range) {

        return SortFamily.sortByLabel(menu, comparator, firstSelector, lastSelector, range);
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        return SortFamily.sortByLabelPinnedIndexes(menu, firstIndexes, lastIndexes);
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes,
            Range range) {

        return SortFamily.sortByLabelPinnedIndexes(menu, firstIndexes, lastIndexes, range);
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes) {

        return SortFamily.sortByLabelPinnedIndexes(menu, comparator, firstIndexes, lastIndexes);
    }

    /** Ordena per label fixant índexs al principi o al final. */
    public static <T, C> DynamicMenu<T, C> sortByLabelPinnedIndexes(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Collection<Integer> firstIndexes,
            Collection<Integer> lastIndexes,
            Range range) {

        return SortFamily.sortByLabelPinnedIndexes(menu, comparator, firstIndexes, lastIndexes, range);
    }
    // -------------------------------------------------------------------------
    // Query helpers
    // -------------------------------------------------------------------------

    /** Retorna l'índex de la primera coincidència. */
    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.indexOfFirst(menu, selector);
    }

    /** Retorna l'índex de la primera coincidència dins d'un rang. */
    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.indexOfFirst(menu, selector, range);
    }

    /** Retorna l'índex de l'última coincidència. */
    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.indexOfLast(menu, selector);
    }

    /** Retorna l'índex de l'última coincidència dins d'un rang. */
    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.indexOfLast(menu, selector, range);
    }

    /** Indica si existeix alguna coincidència. */
    public static <T, C> boolean containsMatch(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.containsMatch(menu, selector);
    }

    /** Compta quantes opcions compleixen una condició. */
    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.countMatches(menu, selector);
    }

    /** Compta quantes opcions compleixen una condició dins d'un rang. */
    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.countMatches(menu, selector, range);
    }

    /** Retorna tots els índexs que compleixen una condició. */
    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.indexesOf(menu, selector);
    }

    /** Retorna tots els índexs que compleixen una condició dins d'un rang. */
    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.indexesOf(menu, selector, range);
    }

    /** Retorna totes les opcions que compleixen una condició. */
    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.matchingOptions(menu, selector);
    }

    /** Retorna totes les opcions que compleixen una condició dins d'un rang. */
    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.matchingOptions(menu, selector, range);
    }

    /** Retorna l'índex de la primera coincidència exacta. */
    public static <T, C> int indexOfFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.indexOfFirstLabel(menu, label);
    }

    /** Retorna l'índex de l'última coincidència exacta. */
    public static <T, C> int indexOfLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.indexOfLastLabel(menu, label);
    }

    /** Indica si existeix alguna coincidència exacta. */
    public static <T, C> boolean containsLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.containsLabel(menu, label);
    }

    /** Compta quantes coincidències exactes hi ha. */
    public static <T, C> int countLabelMatches(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.countLabelMatches(menu, label);
    }

    /** Retorna la primera opció que compleix una condició o {@code null}. */
    public static <T, C> MenuOption<T, C> findFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.findFirst(menu, selector);
    }

    /** Retorna l'última opció que compleix una condició o {@code null}. */
    public static <T, C> MenuOption<T, C> findLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.findLast(menu, selector);
    }

    /** Retorna la primera coincidència exacta o {@code null}. */
    public static <T, C> MenuOption<T, C> findFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.findFirstLabel(menu, label);
    }

    /** Retorna l'última coincidència exacta o {@code null}. */
    public static <T, C> MenuOption<T, C> findLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.findLastLabel(menu, label);
    }

}