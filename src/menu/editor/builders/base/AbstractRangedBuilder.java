package menu.editor.builders.base;

import java.util.Objects;
import java.util.function.Consumer;

import menu.DynamicMenu;
import menu.editor.Range;

/**
 * Pare per als builders que treballen amb {@link Range}.
 */
public abstract class AbstractRangedBuilder<
        T, C,
        S extends AbstractRangedBuilder<T, C, S>>
        extends AbstractChainableMenuBuilder<T, C, S> {

    private Range range = Range.all();

    protected AbstractRangedBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    protected AbstractRangedBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {

        super(menu, pendingPipeline);
    }

    protected AbstractRangedBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline,
            boolean hasPendingOperations) {

        super(menu, pendingPipeline, hasPendingOperations);
    }

    /**
     * Defineix el rang d'actuació.
     */
    public S range(Range range) {
        this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
        onStateChanged();
        return self();
    }

    /**
     * Defineix el rang d'actuació.
     */
    public S range(int fromInclusive, int toExclusive) {
        return range(Range.of(fromInclusive, toExclusive));
    }

    /**
     * Retorna el rang actual, validat.
     */
    protected final Range requireRange() {
        return Objects.requireNonNull(range, "El rang no pot ser nul");
    }

    /**
     * Assigna el rang sense passar per l'API fluent.
     */
    protected final void setRangeSilently(Range range) {
        this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
    }

    /**
     * Hereta només el rang cap al builder objectiu.
     */
    protected final <B extends AbstractRangedBuilder<T, C, B>> B inheritRangeTo(B target) {
        Objects.requireNonNull(target, "El builder objectiu no pot ser nul");
        return target.range(requireRange());
    }
}