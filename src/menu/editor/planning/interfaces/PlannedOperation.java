package menu.editor.planning.interfaces;

import menu.DynamicMenu;
import menu.editor.Range;
import menu.editor.planning.config.OperationType;
import menu.editor.planning.config.OptimizationFamily;
import menu.editor.planning.config.OptimizationProfile;
import menu.editor.planning.config.SelectorDependency;

/** Operació planificada dins d'un pipeline. */
public interface PlannedOperation<T, C> {

    /** Aplica l'operació sobre el menú. */
    void apply(DynamicMenu<T, C> menu);

    /** Retorna el tipus d'operació. */
    OperationType type();

    /** Retorna el rang afectat. */
    Range range();

    /** Indica si és una operació neutra. */
    default boolean isNoOp() {
        return false;
    }

    /** Indica si reordena elements. */
    default boolean reorders() {
        return false;
    }

    /** Indica si manté la cardinalitat. */
    default boolean preservesCardinality() {
        return true;
    }

    /** Indica si modifica índexs. */
    default boolean changesIndexes() {
        return false;
    }

    /** Indica si modifica etiquetes. */
    default boolean changesLabels() {
        return false;
    }

    /** Indica si té selectors fixats. */
    default boolean hasPinnedSelectors() {
        return false;
    }

    /** Indica si actua com a barrera d'optimització. */
    default boolean isBarrier() {
        return false;
    }

    /** Indica si suporta deduplicació semàntica global. */
    default boolean supportsSemanticDeduplication() {
        return false;
    }

    /**
     * Comprova equivalència semàntica amb una altra operació.
     *
     * <p>Per defecte es considera que no són equivalents.
     */
    default boolean isSemanticallyEquivalentTo(PlannedOperation<T, C> other) {
        return false;
    }

    /** Construeix el perfil d'optimització de l'operació. */
    default OptimizationProfile optimizationProfile() {
        return new OptimizationProfile(
                type(),
                range(),
                reorders(),
                preservesCardinality(),
                changesIndexes(),
                changesLabels(),
                hasPinnedSelectors(),
                isBarrier(),
                OptimizationFamily.GENERIC,
                SelectorDependency.UNKNOWN);
    }
}