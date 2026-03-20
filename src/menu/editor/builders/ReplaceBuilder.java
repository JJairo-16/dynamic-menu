package menu.editor.builders;

import java.util.Objects;

import menu.DynamicMenu;
import menu.action.MenuAction;
import menu.action.MenuRuntimeAction;
import menu.action.SimpleMenuAction;
import menu.editor.builders.base.AbstractEditBuilder;
import menu.editor.builders.base.InheritanceMode;
import menu.editor.core.MenuEditorSupport;
import menu.editor.core.ReplaceFamily;
import menu.editor.helpers.ActionMapper;
import menu.editor.helpers.LabelMapper;
import menu.editor.helpers.OptionMapper;
import menu.editor.planning.OperationPlan;
import menu.editor.planning.PlannedOperation;
import menu.editor.planning.operations.NoOpPlannedOperation;
import menu.editor.planning.operations.ReplacePlannedOperation;
import menu.model.MenuOption;

/** Builder fluent per a operacions de substitució. */
public final class ReplaceBuilder<T, C>
        extends AbstractEditBuilder<T, C, ReplaceBuilder<T, C>> {

    private OptionMapper<T, C> mapper;

    /** Crea un builder de substitució sobre un menú. */
    public ReplaceBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    /** Crea un builder amb operacions pendents. */
    public ReplaceBuilder(
            DynamicMenu<T, C> menu,
            OperationPlan<T, C> pendingPlan) {

        super(menu, pendingPlan);
    }

    /** Crea un builder amb estat pendent explícit. */
    public ReplaceBuilder(
            DynamicMenu<T, C> menu,
            OperationPlan<T, C> pendingPlan,
            boolean hasPendingOperations) {

        super(menu, pendingPlan, hasPendingOperations);
    }

    /** Retorna aquesta instància tipada. */
    @Override
    protected ReplaceBuilder<T, C> self() {
        return this;
    }

    /** Defineix el transformador general d'opcions. */
    public ReplaceBuilder<T, C> map(OptionMapper<T, C> mapper) {
        this.mapper = Objects.requireNonNull(mapper, "El transformador no pot ser nul");
        return this;
    }

    /** Substitueix només el label. */
    public ReplaceBuilder<T, C> label(String newLabel) {
        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(newLabel, option.action());
        return this;
    }

    /** Substitueix només el label amb un transformador. */
    public ReplaceBuilder<T, C> label(LabelMapper<T, C> mapper) {
        Objects.requireNonNull(mapper, "El transformador de labels no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(
                Objects.requireNonNull(
                        mapper.map(index, option),
                        "El transformador de labels no pot retornar nul"),
                option.action());
        return this;
    }

    /** Substitueix només el comportament. */
    public ReplaceBuilder<T, C> action(MenuRuntimeAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(option.label(), newAction);
        return this;
    }

    /** Substitueix només el comportament. */
    public ReplaceBuilder<T, C> action(MenuAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return action(MenuEditorSupport.runtimeOf(newAction));
    }

    /** Substitueix només el comportament. */
    public ReplaceBuilder<T, C> action(SimpleMenuAction<T> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return action(MenuEditorSupport.runtimeOf(newAction));
    }

    /** Substitueix només el comportament amb un transformador. */
    public ReplaceBuilder<T, C> action(ActionMapper<T, C> mapper) {
        Objects.requireNonNull(mapper, "El transformador de comportaments no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(
                option.label(),
                Objects.requireNonNull(
                        mapper.map(index, option),
                        "El transformador de comportaments no pot retornar nul"));
        return this;
    }

    /** Substitueix tota l'opció per label i comportament. */
    public ReplaceBuilder<T, C> option(String newLabel, MenuRuntimeAction<T, C> newAction) {
        Objects.requireNonNull(newLabel, "El nou label no pot ser nul");
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        this.mapper = (index, option) -> MenuEditorSupport.newOption(newLabel, newAction);
        return this;
    }

    /** Substitueix tota l'opció per label i comportament. */
    public ReplaceBuilder<T, C> option(String newLabel, MenuAction<T, C> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return option(newLabel, MenuEditorSupport.runtimeOf(newAction));
    }

    /** Substitueix tota l'opció per label i comportament. */
    public ReplaceBuilder<T, C> option(String newLabel, SimpleMenuAction<T> newAction) {
        Objects.requireNonNull(newAction, "El nou comportament no pot ser nul");
        return option(newLabel, MenuEditorSupport.runtimeOf(newAction));
    }

    /** Substitueix tota l'opció per una opció ja construïda. */
    public ReplaceBuilder<T, C> option(MenuOption<T, C> newOption) {
        Objects.requireNonNull(newOption, "La nova opció no pot ser nul·la");
        this.mapper = (index, option) -> newOption;
        return this;
    }

    /** Retorna el transformador definit. */
    private OptionMapper<T, C> requireMapper() {
        return Objects.requireNonNull(mapper, "El transformador no pot ser nul");
    }

    /** Construeix l'operació actual de substitució. */
    private PlannedOperation<T, C> currentOperation() {
        return new ReplacePlannedOperation<>(
                requireSelector(),
                requireMapper(),
                buildConfig());
    }

    /** Executa tota la cadena pendent i després l'última substitució. */
    public int execute() {
        applyPendingOperations();
        return ReplaceFamily.replaceIf(
                menu(),
                requireSelector(),
                requireMapper(),
                buildConfig());
    }

    /** Indica si s'ha substituït almenys una opció. */
    public boolean executeAny() {
        return execute() > 0;
    }

    /** Aplica l'herència d'edició. */
    private <B extends AbstractEditBuilder<T, C, B>> B applyEditInheritance(
            B target,
            InheritanceMode inheritanceMode) {

        Objects.requireNonNull(inheritanceMode, "El mode d'herència no pot ser nul");

        switch (inheritanceMode) {
            case NONE:
                return target;
            case RANGE:
                return inheritRangeTo(target);
            case SELECTION:
                return inheritSelectionTo(target);
            case ALL:
                return inheritEditStateTo(target);
            default:
                throw new IllegalArgumentException("Mode d'herència no suportat: " + inheritanceMode);
        }
    }

    /** Aplica l'herència per a consultes. */
    private QueryBuilder<T, C> applyQueryInheritance(
            QueryBuilder<T, C> target,
            InheritanceMode inheritanceMode) {

        Objects.requireNonNull(inheritanceMode, "El mode d'herència no pot ser nul");

        switch (inheritanceMode) {
            case NONE:
                return target;
            case RANGE:
                return inheritRangeTo(target);
            case SELECTION:
                return inheritSelectionTo(target);
            case ALL:
                return inheritSelectionTo(target);
            default:
                throw new IllegalArgumentException("Mode d'herència no suportat: " + inheritanceMode);
        }
    }

    /** Encadena una altra substitució sense herència. */
    public ReplaceBuilder<T, C> thenReplace() {
        return thenReplace(InheritanceMode.NONE);
    }

    /** Encadena una altra substitució. */
    public ReplaceBuilder<T, C> thenReplace(InheritanceMode inheritanceMode) {
        return applyEditInheritance(chainToReplace(currentOperation()), inheritanceMode);
    }

    /** Encadena una eliminació sense herència. */
    public RemoveBuilder<T, C> thenRemove() {
        return thenRemove(InheritanceMode.NONE);
    }

    /** Encadena una eliminació. */
    public RemoveBuilder<T, C> thenRemove(InheritanceMode inheritanceMode) {
        return applyEditInheritance(chainToRemove(currentOperation()), inheritanceMode);
    }

    /** Encadena una ordenació sense herència. */
    public SortBuilder<T, C> thenSort() {
        return thenSort(InheritanceMode.NONE);
    }

    /** Encadena una ordenació. */
    public SortBuilder<T, C> thenSort(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(chainToSort(currentOperation()), inheritanceMode);
    }

    /** Encadena una consulta sense herència. */
    public QueryBuilder<T, C> thenQuery() {
        return thenQuery(InheritanceMode.NONE);
    }

    /** Encadena una consulta. */
    public QueryBuilder<T, C> thenQuery(InheritanceMode inheritanceMode) {
        return applyQueryInheritance(chainToQuery(currentOperation()), inheritanceMode);
    }

    /** Encadena una barreja amb herència de rang. */
    public ShuffleBuilder<T, C> thenShuffle() {
        return thenShuffle(InheritanceMode.RANGE);
    }

    /** Encadena una barreja. */
    public ShuffleBuilder<T, C> thenShuffle(InheritanceMode inheritanceMode) {
        return applyRangedInheritance(
                chainToShuffle(NoOpPlannedOperation.instance()),
                inheritanceMode);
    }
}
