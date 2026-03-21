package menu.editor.planning.config;

/** Família d'optimització d'una operació. */
public enum OptimizationFamily {

    /** Operacions que reordenen elements. */
    REORDER,

    /** Operacions que eliminen elements. */
    REMOVE,

    /** Operacions que substitueixen elements. */
    REPLACE,

    /** Família genèrica sense comportament específic. */
    GENERIC
}