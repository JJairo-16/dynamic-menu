package menu.snapshot.context;

/**
 * Contracte per a contexts que poden generar i restaurar el seu propi snapshot
 * d'estat intern.
 *
 * @param <S> tipus del snapshot d'estat del context
 */
public interface ContextStateSnapshotSupport<S> {

    /**
     * Crea un snapshot de l'estat intern actual del context.
     *
     * @return snapshot de l'estat intern
     */
    S createContextStateSnapshot();

    /**
     * Restaura l'estat intern del context a partir d'un snapshot.
     *
     * @param snapshot snapshot a restaurar
     */
    void restoreContextStateSnapshot(S snapshot);
}