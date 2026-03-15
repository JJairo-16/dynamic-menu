package menu.editor.builders.base;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import menu.DynamicMenu;
import menu.editor.helpers.OptionSelector;

/**
 * Pare per als builders que treballen amb selector i rang.
 */
public abstract class AbstractSelectableRangedBuilder<
        T, C,
        S extends AbstractSelectableRangedBuilder<T, C, S>>
        extends AbstractRangedBuilder<T, C, S> {

    private OptionSelector<T, C> selector;

    protected AbstractSelectableRangedBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    protected AbstractSelectableRangedBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {

        super(menu, pendingPipeline);
    }

    protected AbstractSelectableRangedBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline,
            boolean hasPendingOperations) {

        super(menu, pendingPipeline, hasPendingOperations);
    }

    /**
     * Defineix la condició de selecció.
     */
    public S where(OptionSelector<T, C> selector) {
        this.selector = Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        onStateChanged();
        return self();
    }

    /**
     * Defineix la condició de selecció basada en el label.
     */
    public S whereLabel(Predicate<String> predicate) {
        Objects.requireNonNull(predicate, "La condició no pot ser nul·la");
        this.selector = (index, option) -> predicate.test(option.label());
        onStateChanged();
        return self();
    }

    /**
     * Defineix que s'han de considerar totes les opcions.
     */
    public S whereAny() {
        this.selector = (index, option) -> true;
        onStateChanged();
        return self();
    }

    /**
     * Retorna el selector actual, validat.
     */
    protected final OptionSelector<T, C> requireSelector() {
        return Objects.requireNonNull(selector, "La condició no pot ser nul·la");
    }

    /**
     * Assigna el selector sense passar per l'API fluent.
     */
    protected final void setSelectorSilently(OptionSelector<T, C> selector) {
        this.selector = Objects.requireNonNull(selector, "La condició no pot ser nul·la");
    }

    /**
     * Hereta selector i rang cap al builder objectiu.
     */
    protected final <B extends AbstractSelectableRangedBuilder<T, C, B>> B inheritSelectionTo(B target) {
        Objects.requireNonNull(target, "El builder objectiu no pot ser nul");
        target.where(requireSelector());
        target.range(requireRange());
        return target;
    }
}