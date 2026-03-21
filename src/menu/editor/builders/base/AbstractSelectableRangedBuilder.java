package menu.editor.builders.base;

import java.util.Objects;
import java.util.function.Predicate;

import menu.DynamicMenu;
import menu.editor.helpers.OptionSelector;
import menu.editor.planning.OperationPlan;

/** Pare per als builders que treballen amb selector i rang. */
public abstract class AbstractSelectableRangedBuilder<T, C, S extends AbstractSelectableRangedBuilder<T, C, S>>
        extends AbstractRangedBuilder<T, C, S> {

    private OptionSelector<T, C> selector;

    protected AbstractSelectableRangedBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    protected AbstractSelectableRangedBuilder(
            DynamicMenu<T, C> menu,
            OperationPlan<T, C> pendingPlan) {

        super(menu, pendingPlan);
    }

    protected AbstractSelectableRangedBuilder(
            DynamicMenu<T, C> menu,
            OperationPlan<T, C> pendingPlan,
            boolean hasPendingOperations) {

        super(menu, pendingPlan, hasPendingOperations);
    }

    /** Defineix la condició de selecció. */
    public S where(OptionSelector<T, C> selector) {
        this.selector = Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        onStateChanged();
        return self();
    }

    /** Defineix la condició de selecció basada en l'índex. */
    public S whereIndex(Predicate<Integer> predicate) {
        Objects.requireNonNull(predicate, "La condició no pot ser nul·la");
        this.selector = (index, option) -> predicate.test(index);
        onStateChanged();
        return self();
    }

    /** Defineix la condició de selecció basada en el label. */
    public S whereLabel(Predicate<String> predicate) {
        Objects.requireNonNull(predicate, "La condició no pot ser nul·la");
        this.selector = labelAdapter(predicate);
        onStateChanged();
        return self();
    }

    /** Defineix la condició de selecció basada en un label exactament igual. */
    public S whereLabelEquals(String text) {
        Objects.requireNonNull(text, "L'String no pot ser nul");
        this.selector = labelAdapter(l -> l.equals(text));
        onStateChanged();
        return self();
    }

    /**
     * Defineix la condició de selecció basada en un label igual ignorant majúscules
     * i minúscules.
     */
    public S whereLabelEqualsIgnoreCase(String text) {
        Objects.requireNonNull(text, "L'String no pot ser nul");
        this.selector = labelAdapter(l -> l.equalsIgnoreCase(text));
        onStateChanged();
        return self();
    }

    /**
     * Defineix la condició de selecció basada en un label que comença amb el prefix
     * indicat.
     */
    public S whereLabelStartsWith(String prefix) {
        Objects.requireNonNull(prefix, "El prefix no pot ser nul");
        if (prefix.isEmpty())
            throw new IllegalArgumentException("El prefix no pot estar en blanc");
        this.selector = labelAdapter(l -> l.startsWith(prefix));
        onStateChanged();
        return self();
    }

    /**
     * Defineix la condició de selecció basada en un label que acaba amb el sufix
     * indicat.
     */
    public S whereLabelEndsWith(String suffix) {
        Objects.requireNonNull(suffix, "El sufix no pot ser nul");
        if (suffix.isEmpty())
            throw new IllegalArgumentException("El sufix no pot estar en blanc");
        this.selector = labelAdapter(l -> l.endsWith(suffix));
        onStateChanged();
        return self();
    }

    /** Defineix que s'han de considerar totes les opcions. */
    public S whereAny() {
        this.selector = (index, option) -> true;
        onStateChanged();
        return self();
    }

    /** Retorna el selector actual, validat. */
    protected final OptionSelector<T, C> requireSelector() {
        return Objects.requireNonNull(selector, "La condició no pot ser nul·la");
    }

    /** Assigna el selector sense passar per l'API fluent. */
    protected final void setSelectorSilently(OptionSelector<T, C> selector) {
        this.selector = Objects.requireNonNull(selector, "La condició no pot ser nul·la");
    }

    /** Hereta selector i rang cap al builder objectiu. */
    protected final <B extends AbstractSelectableRangedBuilder<T, C, B>> B inheritSelectionTo(B target) {
        Objects.requireNonNull(target, "El builder objectiu no pot ser nul");
        target.where(requireSelector());
        target.range(requireRange());
        return target;
    }

    private final OptionSelector<T, C> labelAdapter(Predicate<String> predicate) {
        return (index, option) -> predicate.test(option.label());
    }
}