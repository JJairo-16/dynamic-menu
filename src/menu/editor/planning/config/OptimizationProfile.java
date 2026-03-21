package menu.editor.planning.config;

import java.util.Objects;

import menu.editor.Range;

/**
 * Perfil d'optimització associat a una operació.
 *
 * <p>Descriu propietats rellevants per a la reescriptura i simplificació
 * de pipelines.
 */
public record OptimizationProfile(
        OperationType type,
        Range range,
        boolean reorders,
        boolean preservesCardinality,
        boolean changesIndexes,
        boolean changesLabels,
        boolean hasPinnedSelectors,
        boolean barrier,
        OptimizationFamily family,
        SelectorDependency selectorDependency) {

    /** Valida els camps obligatoris del perfil. */
    public OptimizationProfile {
        Objects.requireNonNull(type, "El tipus no pot ser nul");
        Objects.requireNonNull(range, "El rang no pot ser nul");
        Objects.requireNonNull(family, "La família no pot ser nul·la");
        Objects.requireNonNull(selectorDependency, "La dependència no pot ser nul·la");
    }

    /** Indica si és una reordenació pura. */
    public boolean isPlainReorder() {
        return family == OptimizationFamily.REORDER
                && reorders
                && preservesCardinality
                && !hasPinnedSelectors
                && !barrier;
    }

    /** Comprova si dos perfils tenen el mateix rang. */
    public boolean sameRangeAs(OptimizationProfile other) {
        Objects.requireNonNull(other, "El perfil a comparar no pot ser nul");
        return Objects.equals(range, other.range);
    }

    /** Comprova si dos perfils pertanyen a la mateixa família. */
    public boolean sameFamilyAs(OptimizationProfile other) {
        Objects.requireNonNull(other, "El perfil a comparar no pot ser nul");
        return family == other.family;
    }

    /** Indica si l'operació depèn dels índexs. */
    public boolean isIndexSensitive() {
        return selectorDependency == SelectorDependency.INDEX;
    }

    /** Indica si l'operació depèn de les etiquetes. */
    public boolean isLabelSensitive() {
        return selectorDependency == SelectorDependency.LABEL;
    }

    /** Indica si la dependència és desconeguda. */
    public boolean isUnknownDependency() {
        return selectorDependency == SelectorDependency.UNKNOWN;
    }

    /** Indica si és segura per deduplicació semàntica conservadora. */
    public boolean isConservativelySafeForSemanticDeduplication() {
        return preservesCardinality
                && !changesIndexes
                && !changesLabels
                && !barrier
                && !hasPinnedSelectors
                && !isUnknownDependency()
                && !isIndexSensitive()
                && !isLabelSensitive();
    }

    /** Comprova si dos perfils comparteixen la mateixa finestra semàntica. */
    public boolean sameSemanticWindowAs(OptimizationProfile other) {
        Objects.requireNonNull(other, "El perfil a comparar no pot ser nul");
        return sameRangeAs(other) && sameFamilyAs(other);
    }
}