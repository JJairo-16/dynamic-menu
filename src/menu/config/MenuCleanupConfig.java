package menu.config;

/**
 * Configuració de neteja automàtica del menú.
 *
 * <p>
 * Permet controlar com es gestionen snapshots i estructures internes
 * per evitar que el consum de memòria creixi sense control.
 * </p>
 */
public record MenuCleanupConfig(
        boolean clearSnapshotStackAfterRun,
        int maxNamedSnapshots,
        boolean removeOldestNamedSnapshotsWhenLimitExceeded,
        boolean inheritNamedSnapshotsInChildMenus,
        boolean copyOnlyCurrentSnapshotToChild,
        int iterationsForCleanup) {

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
     * Crea una còpia amb un nou valor per a {@code clearSnapshotStackAfterRun}.
     *
     * @param value nou valor
     * @return nova configuració
     */
    public MenuCleanupConfig withClearSnapshotStackAfterRun(boolean value) {
        return new MenuCleanupConfig(
                value,
                maxNamedSnapshots,
                removeOldestNamedSnapshotsWhenLimitExceeded,
                inheritNamedSnapshotsInChildMenus,
                copyOnlyCurrentSnapshotToChild,
                iterationsForCleanup);
    }

    /**
     * Crea una còpia amb un nou valor per a {@code maxNamedSnapshots}.
     *
     * @param value nou valor
     * @return nova configuració
     */
    public MenuCleanupConfig withMaxNamedSnapshots(int value) {
        return new MenuCleanupConfig(
                clearSnapshotStackAfterRun,
                value,
                removeOldestNamedSnapshotsWhenLimitExceeded,
                inheritNamedSnapshotsInChildMenus,
                copyOnlyCurrentSnapshotToChild,
                iterationsForCleanup);
    }

    /**
     * Crea una còpia amb un nou valor per a
     * {@code removeOldestNamedSnapshotsWhenLimitExceeded}.
     *
     * @param value nou valor
     * @return nova configuració
     */
    public MenuCleanupConfig withRemoveOldestNamedSnapshotsWhenLimitExceeded(boolean value) {
        return new MenuCleanupConfig(
                clearSnapshotStackAfterRun,
                maxNamedSnapshots,
                value,
                inheritNamedSnapshotsInChildMenus,
                copyOnlyCurrentSnapshotToChild,
                iterationsForCleanup);
    }

    /**
     * Crea una còpia amb un nou valor per a
     * {@code inheritNamedSnapshotsInChildMenus}.
     *
     * @param value nou valor
     * @return nova configuració
     */
    public MenuCleanupConfig withInheritNamedSnapshotsInChildMenus(boolean value) {
        return new MenuCleanupConfig(
                clearSnapshotStackAfterRun,
                maxNamedSnapshots,
                removeOldestNamedSnapshotsWhenLimitExceeded,
                value,
                copyOnlyCurrentSnapshotToChild,
                iterationsForCleanup);
    }

    /**
     * Crea una còpia amb un nou valor per a
     * {@code copyOnlyCurrentSnapshotToChild}.
     *
     * @param value nou valor
     * @return nova configuració
     */
    public MenuCleanupConfig withCopyOnlyCurrentSnapshotToChild(boolean value) {
        return new MenuCleanupConfig(
                clearSnapshotStackAfterRun,
                maxNamedSnapshots,
                removeOldestNamedSnapshotsWhenLimitExceeded,
                inheritNamedSnapshotsInChildMenus,
                value,
                iterationsForCleanup);
    }

    /**
     * Crea una còpia amb un nou valor per a {@code iterationPerClean}.
     *
     * <p>
     * Aquest valor indica cada quantes iteracions del menú
     * s'ha d'executar una neteja automàtica.
     * </p>
     *
     * <ul>
     * <li>0 → neteja desactivada</li>
     * <li>valor positiu → neteja cada N iteracions</li>
     * </ul>
     *
     * @param value nombre d'iteracions entre neteges
     * @return nova configuració
     */
    public MenuCleanupConfig withIterationsForCleanup(int value) {
        return new MenuCleanupConfig(
                clearSnapshotStackAfterRun,
                maxNamedSnapshots,
                removeOldestNamedSnapshotsWhenLimitExceeded,
                inheritNamedSnapshotsInChildMenus,
                copyOnlyCurrentSnapshotToChild,
                value);
    }
}