package menu.config;

/**
 * Configuració de neteja automàtica del menú.
 *
 * <p>
 * Permet controlar com es gestionen snapshots i estructures internes
 * per evitar que el consum de memòria creixi sense control.
 * </p>
 */
public final class MenuCleanupConfig {

    private boolean clearSnapshotStackAfterRun;
    private int maxNamedSnapshots;
    private boolean removeOldestNamedSnapshotsWhenLimitExceeded;
    private boolean inheritNamedSnapshotsInChildMenus;
    private boolean copyOnlyCurrentSnapshotToChild;
    private int iterationsForCleanup;

    /**
     * Crea una nova configuració amb tots els valors indicats.
     *
     * @param clearSnapshotStackAfterRun indica si s'ha de netejar la pila de
     *                                   snapshots en acabar {@code run()}
     * @param maxNamedSnapshots nombre màxim de snapshots registrats per nom
     * @param removeOldestNamedSnapshotsWhenLimitExceeded indica si s'han
     *                                                    d'eliminar els snapshots
     *                                                    més antics quan se supera
     *                                                    el límit
     * @param inheritNamedSnapshotsInChildMenus indica si els menús fills han
     *                                          d'heretar snapshots registrats
     * @param copyOnlyCurrentSnapshotToChild indica si el menú fill només ha de
     *                                       rebre l'snapshot actual
     * @param iterationsForCleanup nombre d'iteracions entre cada neteja
     *                             automàtica periòdica
     */
    private MenuCleanupConfig(
            boolean clearSnapshotStackAfterRun,
            int maxNamedSnapshots,
            boolean removeOldestNamedSnapshotsWhenLimitExceeded,
            boolean inheritNamedSnapshotsInChildMenus,
            boolean copyOnlyCurrentSnapshotToChild,
            int iterationsForCleanup) {

        this.clearSnapshotStackAfterRun = clearSnapshotStackAfterRun;
        this.maxNamedSnapshots = maxNamedSnapshots;
        this.removeOldestNamedSnapshotsWhenLimitExceeded =
                removeOldestNamedSnapshotsWhenLimitExceeded;
        this.inheritNamedSnapshotsInChildMenus = inheritNamedSnapshotsInChildMenus;
        this.copyOnlyCurrentSnapshotToChild = copyOnlyCurrentSnapshotToChild;
        this.iterationsForCleanup = iterationsForCleanup;
    }

    /**
     * Retorna una configuració per defecte equilibrada.
     *
     * <p>
     * Aquesta configuració prioritza un cost de memòria moderat:
     * </p>
     *
     * <ul>
     * <li>neteja la pila de snapshots en acabar {@code run()};</li>
     * <li>limita el nombre de snapshots registrats;</li>
     * <li>elimina els més antics si se supera el límit;</li>
     * <li>evita heretar snapshots registrats als menús fills per defecte;</li>
     * <li>copia només el snapshot actual al menú fill;</li>
     * <li>executa neteges periòdiques cada determinat nombre d'iteracions.</li>
     * </ul>
     *
     * @return configuració per defecte
     */
    public static MenuCleanupConfig defaults() {
        return new MenuCleanupConfig(
                true,
                12,
                true,
                false,
                true,
                25);
    }

    /**
     * Retorna una configuració més agressiva en la neteja.
     *
     * <p>
     * Aquesta configuració redueix encara més el nombre de snapshots
     * i executa neteges amb més freqüència.
     * </p>
     *
     * @return configuració agressiva
     */
    public static MenuCleanupConfig aggressive() {
        return new MenuCleanupConfig(
                true,
                10,
                true,
                false,
                true,
                40);
    }

    /**
     * Retorna una configuració sense limitacions ni neteja automàtica.
     *
     * @return configuració sense neteja
     */
    public static MenuCleanupConfig disabled() {
        return new MenuCleanupConfig(
                false,
                0,
                false,
                true,
                false,
                0);
    }

    /**
     * Actualitza el valor de {@code clearSnapshotStackAfterRun}.
     *
     * @param value nou valor
     * @return aquesta mateixa configuració
     */
    public MenuCleanupConfig withClearSnapshotStackAfterRun(boolean value) {
        this.clearSnapshotStackAfterRun = value;
        return this;
    }

    /**
     * Actualitza el valor de {@code maxNamedSnapshots}.
     *
     * @param value nou valor
     * @return aquesta mateixa configuració
     */
    public MenuCleanupConfig withMaxNamedSnapshots(int value) {
        this.maxNamedSnapshots = value;
        return this;
    }

    /**
     * Actualitza el valor de
     * {@code removeOldestNamedSnapshotsWhenLimitExceeded}.
     *
     * @param value nou valor
     * @return aquesta mateixa configuració
     */
    public MenuCleanupConfig withRemoveOldestNamedSnapshotsWhenLimitExceeded(boolean value) {
        this.removeOldestNamedSnapshotsWhenLimitExceeded = value;
        return this;
    }

    /**
     * Actualitza el valor de {@code inheritNamedSnapshotsInChildMenus}.
     *
     * @param value nou valor
     * @return aquesta mateixa configuració
     */
    public MenuCleanupConfig withInheritNamedSnapshotsInChildMenus(boolean value) {
        this.inheritNamedSnapshotsInChildMenus = value;
        return this;
    }

    /**
     * Actualitza el valor de {@code copyOnlyCurrentSnapshotToChild}.
     *
     * @param value nou valor
     * @return aquesta mateixa configuració
     */
    public MenuCleanupConfig withCopyOnlyCurrentSnapshotToChild(boolean value) {
        this.copyOnlyCurrentSnapshotToChild = value;
        return this;
    }

    /**
     * Actualitza el valor de {@code iterationsForCleanup}.
     *
     * <p>
     * Un valor de {@code 0} implica que la neteja periòdica queda desactivada.
     * Un valor positiu indica cada quantes iteracions s'ha d'intentar executar.
     * </p>
     *
     * @param value nou valor
     * @return aquesta mateixa configuració
     */
    public MenuCleanupConfig withIterationsForCleanup(int value) {
        this.iterationsForCleanup = value;
        return this;
    }

    /**
     * Indica si s'ha de netejar la pila de snapshots en acabar {@code run()}.
     *
     * @return {@code true} si la pila s'ha de netejar automàticament
     */
    public boolean clearSnapshotStackAfterRun() {
        return clearSnapshotStackAfterRun;
    }

    /**
     * Retorna el nombre màxim de snapshots registrats per nom que es poden
     * conservar.
     *
     * @return nombre màxim de snapshots registrats
     */
    public int maxNamedSnapshots() {
        return maxNamedSnapshots;
    }

    /**
     * Indica si s'han d'eliminar els snapshots registrats més antics quan
     * se supera el límit configurat.
     *
     * @return {@code true} si s'han d'eliminar els més antics
     */
    public boolean removeOldestNamedSnapshotsWhenLimitExceeded() {
        return removeOldestNamedSnapshotsWhenLimitExceeded;
    }

    /**
     * Indica si els menús fills han d'heretar snapshots registrats del menú pare.
     *
     * @return {@code true} si els menús fills han d'heretar-los
     */
    public boolean inheritNamedSnapshotsInChildMenus() {
        return inheritNamedSnapshotsInChildMenus;
    }

    /**
     * Indica si el menú fill només ha de rebre l'snapshot actual.
     *
     * @return {@code true} si només s'ha de copiar l'snapshot actual
     */
    public boolean copyOnlyCurrentSnapshotToChild() {
        return copyOnlyCurrentSnapshotToChild;
    }

    /**
     * Retorna el nombre d'iteracions entre cada neteja periòdica.
     *
     * <p>
     * Un valor de {@code 0} indica que la neteja periòdica està desactivada.
     * </p>
     *
     * @return nombre d'iteracions entre neteges
     */
    public int iterationsForCleanup() {
        return iterationsForCleanup;
    }

    /**
     * Indica si la neteja periòdica està activada.
     *
     * @return {@code true} si hi ha una freqüència de neteja periòdica activa
     */
    public boolean isPeriodicCleanupEnabled() {
        return iterationsForCleanup > 0;
    }
}