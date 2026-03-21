package menu.editor.planning.config;

/** Tipus de dependència d'una operació respecte al selector. */
public enum SelectorDependency {

    /** No té dependències del selector. */
    NONE,

    /** Depèn de la posició (índex) dels elements. */
    INDEX,

    /** Depèn de les etiquetes dels elements. */
    LABEL,

    /** Depèn de l'estat actual del menú. */
    MENU_STATE,

    /** Depèn de factors aleatoris. */
    RANDOM,

    /** Dependència desconeguda o no especificada. */
    UNKNOWN
}