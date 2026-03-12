package menu.editor.builders;

import java.util.Objects;

import menu.DynamicMenu;
import menu.action.*;
import menu.editor.EditConfig;
import menu.editor.Range;
import menu.editor.core.MenuEditorSupport;
import menu.editor.core.ReplaceFamily;
import menu.editor.helpers.ActionMapper;
import menu.editor.helpers.LabelMapper;
import menu.editor.helpers.OptionMapper;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;

public final class ReplaceBuilder<T, C> {
    private final DynamicMenu<T, C> menu;
    private OptionSelector<T, C> selector;
    private OptionMapper<T, C> mapper;
    private Range range = Range.all();
    private int limit = Integer.MAX_VALUE;
    private boolean reverse = false;

    public ReplaceBuilder(DynamicMenu<T, C> menu) {
        this.menu = Objects.requireNonNull(menu, "El menú no pot ser nul");
    }

    public ReplaceBuilder<T, C> where(OptionSelector<T, C> selector) {
        this.selector = Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        return this;
    }

    public ReplaceBuilder<T, C> map(OptionMapper<T, C> mapper) {
        this.mapper = Objects.requireNonNull(mapper, "El transformador no pot ser nul");
        return this;
    }

    public ReplaceBuilder<T, C> label(String newLabel) {
        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(newLabel, option.action());
        return this;
    }

    public ReplaceBuilder<T, C> label(LabelMapper<T, C> mapper) {
        Objects.requireNonNull(mapper, "El transformador de labels no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(
                Objects.requireNonNull(
                        mapper.map(index, option),
                        "El transformador de labels no pot retornar nul"),
                option.action());
        return this;
    }

    public ReplaceBuilder<T, C> action(MenuRuntimeAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(option.label(), newAction);
        return this;
    }

    public ReplaceBuilder<T, C> action(MenuAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return action(MenuEditorSupport.runtimeOf(newAction));
    }

    public ReplaceBuilder<T, C> action(SimpleMenuAction<T> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return action(MenuEditorSupport.runtimeOf(newAction));
    }

    public ReplaceBuilder<T, C> action(ActionMapper<T, C> mapper) {
        Objects.requireNonNull(mapper, "El transformador de comportaments no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(
                option.label(),
                Objects.requireNonNull(
                        mapper.map(index, option),
                        "El transformador de comportaments no pot retornar nul"));
        return this;
    }

    public ReplaceBuilder<T, C> option(String newLabel, MenuRuntimeAction<T, C> newAction) {
        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(newLabel, newAction);
        return this;
    }

    public ReplaceBuilder<T, C> option(String newLabel, MenuAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return option(newLabel, MenuEditorSupport.runtimeOf(newAction));
    }

    public ReplaceBuilder<T, C> option(String newLabel, SimpleMenuAction<T> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return option(newLabel, MenuEditorSupport.runtimeOf(newAction));
    }

    public ReplaceBuilder<T, C> option(MenuOption<T, C> newOption) {
        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        this.mapper = (index, option) -> newOption;
        return this;
    }

    public ReplaceBuilder<T, C> range(Range range) {
        this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
        return this;
    }

    public ReplaceBuilder<T, C> range(int fromInclusive, int toExclusive) {
        return range(Range.of(fromInclusive, toExclusive));
    }

    public ReplaceBuilder<T, C> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public ReplaceBuilder<T, C> reverse() {
        this.reverse = true;
        return this;
    }

    public ReplaceBuilder<T, C> reverse(boolean reverse) {
        this.reverse = reverse;
        return this;
    }

    public ReplaceBuilder<T, C> first() {
        this.limit = 1;
        this.reverse = false;
        return this;
    }

    public ReplaceBuilder<T, C> last() {
        this.limit = 1;
        this.reverse = true;
        return this;
    }

    public ReplaceBuilder<T, C> all() {
        this.limit = Integer.MAX_VALUE;
        return this;
    }

    public ReplaceBuilder<T, C> config(EditConfig config) {
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");
        this.range = config.range();
        this.limit = config.limit();
        this.reverse = config.reverse();
        return this;
    }

    public EditConfig buildConfig() {
        return EditConfig.builder()
                .range(range)
                .limit(limit)
                .reverse(reverse)
                .build();
    }

    public int execute() {
        return ReplaceFamily.replaceIf(
                menu,
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                Objects.requireNonNull(mapper, "El transformador no pot ser nul"),
                buildConfig());
    }

    public boolean executeAny() {
        return execute() > 0;
    }
}