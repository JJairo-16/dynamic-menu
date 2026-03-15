package menu.editor.builders;

import java.util.Objects;
import java.util.function.Consumer;

import menu.DynamicMenu;
import menu.action.MenuAction;
import menu.action.MenuRuntimeAction;
import menu.action.SimpleMenuAction;
import menu.editor.core.MenuEditorSupport;
import menu.editor.core.ReplaceFamily;
import menu.editor.helpers.ActionMapper;
import menu.editor.helpers.LabelMapper;
import menu.editor.helpers.OptionMapper;
import menu.model.MenuOption;

/**
 * Builder fluent per a operacions de substitució.
 */
public final class ReplaceBuilder<T, C>
        extends AbstractEditBuilder<T, C, ReplaceBuilder<T, C>> {

    private OptionMapper<T, C> mapper;

    public ReplaceBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    ReplaceBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {

        super(menu, pendingPipeline);
    }

    ReplaceBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline,
            boolean hasPendingOperations) {

        super(menu, pendingPipeline, hasPendingOperations);
    }

    @Override
    protected ReplaceBuilder<T, C> self() {
        return this;
    }

    /**
     * Defineix el transformador general d'opcions.
     */
    public ReplaceBuilder<T, C> map(OptionMapper<T, C> mapper) {
        this.mapper = Objects.requireNonNull(mapper, "El transformador no pot ser nul");
        return this;
    }

    /**
     * Substitueix només el label.
     */
    public ReplaceBuilder<T, C> label(String newLabel) {
        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(newLabel, option.action());
        return this;
    }

    /**
     * Substitueix només el label amb un transformador.
     */
    public ReplaceBuilder<T, C> label(LabelMapper<T, C> mapper) {
        Objects.requireNonNull(mapper, "El transformador de labels no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(
                Objects.requireNonNull(
                        mapper.map(index, option),
                        "El transformador de labels no pot retornar nul"),
                option.action());
        return this;
    }

    /**
     * Substitueix només el comportament.
     */
    public ReplaceBuilder<T, C> action(MenuRuntimeAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(option.label(), newAction);
        return this;
    }

    /**
     * Substitueix només el comportament.
     */
    public ReplaceBuilder<T, C> action(MenuAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return action(MenuEditorSupport.runtimeOf(newAction));
    }

    /**
     * Substitueix només el comportament.
     */
    public ReplaceBuilder<T, C> action(SimpleMenuAction<T> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return action(MenuEditorSupport.runtimeOf(newAction));
    }

    /**
     * Substitueix només el comportament amb un transformador.
     */
    public ReplaceBuilder<T, C> action(ActionMapper<T, C> mapper) {
        Objects.requireNonNull(mapper, "El transformador de comportaments no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(
                option.label(),
                Objects.requireNonNull(
                        mapper.map(index, option),
                        "El transformador de comportaments no pot retornar nul"));
        return this;
    }

    /**
     * Substitueix tota l'opció per label i comportament.
     */
    public ReplaceBuilder<T, C> option(String newLabel, MenuRuntimeAction<T, C> newAction) {
        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(newLabel, newAction);
        return this;
    }

    /**
     * Substitueix tota l'opció per label i comportament.
     */
    public ReplaceBuilder<T, C> option(String newLabel, MenuAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return option(newLabel, MenuEditorSupport.runtimeOf(newAction));
    }

    /**
     * Substitueix tota l'opció per label i comportament.
     */
    public ReplaceBuilder<T, C> option(String newLabel, SimpleMenuAction<T> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return option(newLabel, MenuEditorSupport.runtimeOf(newAction));
    }

    /**
     * Substitueix tota l'opció per una opció ja construïda.
     */
    public ReplaceBuilder<T, C> option(MenuOption<T, C> newOption) {
        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        this.mapper = (index, option) -> newOption;
        return this;
    }

    private OptionMapper<T, C> requireMapper() {
        return Objects.requireNonNull(mapper, "El transformador no pot ser nul");
    }

    private Consumer<DynamicMenu<T, C>> currentOperation() {
        return currentMenu -> ReplaceFamily.replaceIf(
                currentMenu,
                requireSelector(),
                requireMapper(),
                buildConfig());
    }

    /**
     * Executa tota la cadena pendent i després l'última substitució.
     */
    public int execute() {
        applyPendingOperations();
        return ReplaceFamily.replaceIf(
                menu(),
                requireSelector(),
                requireMapper(),
                buildConfig());
    }

    /**
     * Indica si s'ha substituït almenys una opció.
     */
    public boolean executeAny() {
        return execute() > 0;
    }

    public ReplaceBuilder<T, C> thenReplace() {
        return chainToReplace(currentOperation());
    }

    public RemoveBuilder<T, C> thenRemove() {
        return chainToRemove(currentOperation());
    }

    public SortBuilder<T, C> thenSort() {
        return chainToSort(currentOperation());
    }

    public QueryBuilder<T, C> thenQuery() {
        return chainToQuery(currentOperation())
                .where(requireSelector())
                .range(requireRange());
    }
}