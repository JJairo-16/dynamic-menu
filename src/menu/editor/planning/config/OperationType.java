package menu.editor.planning.config;

/** Tipus d'operació dins del pipeline. */
public enum OperationType {
    /** Operació neutra sense efecte. */
    NO_OP,

    /** Ordena elements segons un criteri. */
    SORT,

    /** Reordena elements de forma aleatòria. */
    SHUFFLE,

    /** Elimina elements que compleixen una condició. */
    REMOVE,

    /** Substitueix elements per altres. */
    REPLACE,

    /** Tipus genèric per operacions no classificades. */
    GENERIC
}
