package menu.editor.planning.config;

/** Estat de normalització d'un pipeline d'operacions. */
public enum PlanNormalizationState {

    /** Pipeline sense cap optimització aplicada. */
    RAW,

    /** Pipeline amb optimització local (incremental). */
    LOCALLY_OPTIMIZED,

    /** Pipeline amb optimització global completa. */
    GLOBALLY_OPTIMIZED
}