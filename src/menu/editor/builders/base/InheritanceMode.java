package menu.editor.builders.base;

/** Modes públics d'herència d'estat en l'encadenament de builders. */
public enum InheritanceMode {
    /** No hereta res. */
    NONE,

    /** Hereta només el rang. */
    RANGE,

    /** Hereta selector i rang. */
    SELECTION,

    /** Hereta tot l'estat compatible entre el builder origen i el destí. */
    ALL
}