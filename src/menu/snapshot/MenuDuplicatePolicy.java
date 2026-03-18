package menu.snapshot;

/** Defineix com s'han de gestionar les opcions duplicades dins d'un snapshot. */
public enum MenuDuplicatePolicy {

    /** Permet afegir opcions duplicades sense cap restricció. */
    ALLOW,

    /** Si ja existeix una opció amb la mateixa etiqueta, la substitueix. */
    REPLACE,

    /** No permet duplicats i llança una excepció si ja existeixen. */
    DISALLOW_THROW,

    /** No permet duplicats i ignora l'operació si ja existeixen. */
    DISALLOW_IGNORE
}
