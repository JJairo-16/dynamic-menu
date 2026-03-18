package menu.editor.builders.base;

import java.util.Objects;
import java.util.function.Consumer;

import menu.DynamicMenu;
import menu.editor.Range;

/** Pare per als builders que treballen amb {@link Range}. */
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

    /** Defineix el rang d'actuació. */
    public S range(Range range) {
        this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
        onStateChanged();
        return self();
    }

    /**
     * Defineix el rang d'actuació.
     *
     * Si es fan servir índexs negatius, es resolen comptant des del final
     * del menú actual.
     */
    public S range(int fromInclusive, int toExclusive) {
        return range(Range.of(fromInclusive, toExclusive, menu().optionCount()));
    }

    /** Retorna el rang actual, validat. */
    protected final Range requireRange() {
        return Objects.requireNonNull(range, "El rang no pot ser nul");
    }

    /** Assigna el rang sense passar per l'API fluent. */
    protected final void setRangeSilently(Range range) {
        this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
    }

    /** Hereta només el rang cap al builder objectiu. */
    protected final <B extends AbstractRangedBuilder<T, C, B>> B inheritRangeTo(B target) {
        Objects.requireNonNull(target, "El builder objectiu no pot ser nul");
        return target.range(requireRange());
    }

    /**
     * Aplica l'herència de rang al builder següent.
     *
     * @param target          builder objectiu
     * @param inheritanceMode mode d'herència
     * @param <B>             tipus del builder objectiu
     * @return builder objectiu
     */
    protected <B extends AbstractRangedBuilder<T, C, B>> B applyRangedInheritance(
            B target,
            InheritanceMode inheritanceMode) {

        Objects.requireNonNull(inheritanceMode, "El mode d'herència no pot ser nul");

        switch (inheritanceMode) {
            case NONE:
                return target;
            case RANGE:
                return inheritRangeTo(target);
            case ALL:
                return inheritRangeTo(target);
            case SELECTION:
                throw new IllegalArgumentException(
                        "ShuffleBuilder no té selector per heretar; usa RANGE o ALL");
            default:
                throw new IllegalArgumentException("Mode d'herència no suportat: " + inheritanceMode);
        }
    }
}