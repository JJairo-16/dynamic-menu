package menu.editor.builders.base;

import java.util.Objects;
import java.util.function.Consumer;

import menu.DynamicMenu;
import menu.editor.EditConfig;

/**
 * Pare per als builders d'edició que comparteixen {@link EditConfig}.
 */
public abstract class AbstractEditBuilder<
        T, C,
        S extends AbstractEditBuilder<T, C, S>>
        extends AbstractSelectableRangedBuilder<T, C, S> {

    private int limit = Integer.MAX_VALUE;
    private boolean reverse = false;

    private boolean configDirty = true;
    private EditConfig cachedConfig;

    protected AbstractEditBuilder(DynamicMenu<T, C> menu) {
        super(menu);
    }

    protected AbstractEditBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline) {

        super(menu, pendingPipeline);
    }

    protected AbstractEditBuilder(
            DynamicMenu<T, C> menu,
            Consumer<DynamicMenu<T, C>> pendingPipeline,
            boolean hasPendingOperations) {

        super(menu, pendingPipeline, hasPendingOperations);
    }

    @Override
    protected void onStateChanged() {
        this.configDirty = true;
    }

    /**
     * Defineix el límit màxim d'edicions.
     */
    public S limit(int limit) {
        this.limit = limit;
        onStateChanged();
        return self();
    }

    /**
     * Configura el recorregut en sentit invers.
     */
    public S reverse() {
        this.reverse = true;
        onStateChanged();
        return self();
    }

    /**
     * Defineix explícitament si s'ha de recórrer en sentit invers.
     */
    public S reverse(boolean reverse) {
        this.reverse = reverse;
        onStateChanged();
        return self();
    }

    /**
     * Limita l'operació a la primera coincidència.
     */
    public S first() {
        this.limit = 1;
        this.reverse = false;
        onStateChanged();
        return self();
    }

    /**
     * Limita l'operació a l'última coincidència.
     */
    public S last() {
        this.limit = 1;
        this.reverse = true;
        onStateChanged();
        return self();
    }

    /**
     * Defineix que s'han de considerar totes les coincidències.
     */
    public S all() {
        this.limit = Integer.MAX_VALUE;
        onStateChanged();
        return self();
    }

    /**
     * Aplica una configuració base.
     */
    public S config(EditConfig config) {
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        setRangeSilently(config.range());
        this.limit = config.limit();
        this.reverse = config.reverse();

        this.cachedConfig = config;
        this.configDirty = false;
        return self();
    }

    /**
     * Construeix la configuració actual.
     */
    public EditConfig buildConfig() {
        if (!configDirty && cachedConfig != null) {
            return cachedConfig;
        }

        cachedConfig = EditConfig.builder()
                .range(requireRange())
                .limit(limit)
                .reverse(reverse)
                .build();

        configDirty = false;
        return cachedConfig;
    }

    /**
     * Hereta selector, rang i configuració d'edició.
     */
    protected final <B extends AbstractEditBuilder<T, C, B>> B inheritEditStateTo(B target) {
        Objects.requireNonNull(target, "El builder objectiu no pot ser nul");
        target.where(requireSelector());
        target.config(buildConfig());
        return target;
    }
}