package menu;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import menu.action.*;
import menu.cache.MenuOptionTextCache;
import menu.config.MenuCleanupConfig;
import menu.hook.MenuLoopHook;
import menu.hook.MenuLoopState;
import menu.model.MenuOption;
import menu.model.MenuResult;
import menu.selector.MenuSelector;
import menu.snapshot.MenuDuplicatePolicy;
import menu.snapshot.MenuSnapshot;
import menu.snapshot.MenuSnapshotManager;
import menu.wrappers.MenuMutationSupport;

/**
 * Motor de menú dinàmic amb suport per snapshots, sets registrats, menús
 * fills i neteja automàtica opcional.
 *
 * <p>
 * Aquesta versió delega tota la gestió de snapshots a
 * {@link MenuSnapshotManager},
 * però manté la mateixa API pública a través de wrappers per preservar la
 * compatibilitat amb el codi existent.
 * </p>
 *
 * <p>
 * El motor continua sent responsable del bucle d'execució, de la relació amb
 * el selector i de les regles sobre si es pot modificar l'estat mentre s'està
 * executant.
 * </p>
 *
 * <p>
 * El context del menú es pot substituir en qualsevol moment a través de
 * {@link #setContext(Object)}. Els snapshots en guarden la <b>referència</b>,
 * no una còpia profunda del seu estat intern.
 * </p>
 *
 * <p>
 * Per defecte, el menú <b>no permet modificacions mentre s'està executant</b>.
 * Es pot habilitar aquest comportament amb
 * {@link #modifiableDuringRun(boolean)}.
 * </p>
 *
 * <p>
 * Quan les modificacions durant {@link #run()} estan permeses, els canvis
 * sobre el menú afecten de manera garantida la següent iteració. La iteració
 * actual continua treballant amb una instantània immutable del set actiu.
 * </p>
 *
 * <p>
 * Opcionalment, el menú pot activar un sistema de <b>neteja automàtica</b>
 * per limitar el creixement de memòria relacionat amb snapshots registrats,
 * la pila interna de snapshots i l'herència de snapshots en menús fills.
 * Aquest comportament es pot activar amb {@link #autoCleanup(boolean)} i
 * personalitzar amb {@link #cleanupConfig(MenuCleanupConfig)}.
 * </p>
 *
 * <p>
 * <b>Important:</b> aquesta classe no és segura per a entorns concurrents.
 * </p>
 *
 * @param <T> tipus del valor de retorn del menú
 * @param <C> tipus del context associat al menú
 */
public final class DynamicMenu<T, C> {

    private final MenuSelector selector;
    private final MenuSnapshotManager<T, C> snapshotManager;

    private final MenuOptionTextCache<T, C> optionTextCache;

    private boolean running;
    private boolean modifiableDuringRun;

    private boolean autoCleanupEnabled;
    private MenuCleanupConfig cleanupConfig = MenuCleanupConfig.disabled();

    private final MenuMutationSupport<T, C> mutator;

    /**
     * Crea un menú amb el títol inicial indicat.
     *
     * @param title    títol inicial del menú
     * @param context  context del menú; pot ser {@code null}
     * @param selector selector utilitzat per triar opcions
     * @throws NullPointerException si {@code title} o {@code selector} són
     *                              {@code null}
     */
    public DynamicMenu(String title, C context, MenuSelector selector) {
        Objects.requireNonNull(title, "El títol del menú no pot ser nul");

        this.selector = Objects.requireNonNull(selector, "El selector del menú no pot ser nul");
        this.snapshotManager = new MenuSnapshotManager<>(title, context);
        this.optionTextCache = new MenuOptionTextCache<>();

        this.running = false;
        this.modifiableDuringRun = false;

        this.mutator = new MenuMutationSupport<>(
                this,
                this::ensureCanModifyDuringRun,
                this::invalidateVisibleMenuCaches);
    }

    /**
     * Constructor intern utilitzat per crear menús fills sense duplicar lògica.
     *
     * @param selector        selector compartit amb el menú pare
     * @param snapshotManager gestor de snapshots ja inicialitzat per al fill
     * @throws NullPointerException si {@code selector} o {@code snapshotManager}
     *                              són {@code null}
     */
    DynamicMenu(MenuSelector selector, MenuSnapshotManager<T, C> snapshotManager) {
        this.selector = Objects.requireNonNull(selector, "El selector del menú no pot ser nul");
        this.snapshotManager = Objects.requireNonNull(
                snapshotManager,
                "El gestor de snapshots del menú no pot ser nul");
        this.optionTextCache = new MenuOptionTextCache<>();

        this.running = false;
        this.modifiableDuringRun = false;

        this.mutator = new MenuMutationSupport<>(
                this,
                this::ensureCanModifyDuringRun,
                this::invalidateVisibleMenuCaches);
    }

    /**
     * Crea un menú sense context.
     *
     * @param title    títol inicial del menú
     * @param selector selector utilitzat per triar opcions
     * @param <T>      tipus del valor de retorn del menú
     * @return nou menú sense context
     */
    public static <T> DynamicMenu<T, Void> withoutContext(String title, MenuSelector selector) {
        return new DynamicMenu<>(title, null, selector);
    }

    /**
     * Defineix si es permet modificar el menú mentre s'està executant.
     *
     * @param modifiable {@code true} si es volen permetre modificacions durant
     *                   {@link #run()}
     * @return aquest mateix menú
     * @throws IllegalStateException si s'intenta canviar mentre el menú s'està
     *                               executant
     */
    public DynamicMenu<T, C> modifiableDuringRun(boolean modifiable) {
        if (running) {
            throw new IllegalStateException(
                    "No es pot canviar la configuració de modificació mentre el menú s'està executant");
        }

        this.modifiableDuringRun = modifiable;
        return this;
    }

    /**
     * Indica si el menú permet modificacions durant l'execució.
     *
     * @return {@code true} si es permeten modificacions durant {@link #run()}
     */
    public boolean isModifiableDuringRun() {
        return modifiableDuringRun;
    }

    /**
     * Indica si el menú s'està executant actualment.
     *
     * @return {@code true} si {@link #run()} està actiu
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Retorna el context actual del menú.
     *
     * @return context actual, que pot ser {@code null}
     */
    public C getContext() {
        return snapshotManager.getContext();
    }

    /**
     * Defineix el context actual del menú.
     *
     * @param context nou context; pot ser {@code null}
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> setContext(C context) {
        return mutator.mutateNoCache(() -> snapshotManager.setContext(context));
    }

    /**
     * Retorna el títol actual del menú.
     *
     * @return títol actual
     */
    public String getTitle() {
        return snapshotManager.getTitle();
    }

    /**
     * Defineix el títol del menú actual.
     *
     * @param title nou títol
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> setTitle(String title) {
        return mutator.mutate(() -> snapshotManager.setTitle(title));
    }

    /**
     * Retorna una còpia del snapshot actual.
     *
     * @return còpia independent del snapshot actual
     */
    public MenuSnapshot<T, C> createSnapshot() {
        return snapshotManager.createSnapshot();
    }

    /**
     * Retorna la política actual de duplicats.
     *
     * @return política activa
     */
    public MenuDuplicatePolicy getDuplicatePolicy() {
        return snapshotManager.getDuplicatePolicy();
    }

    /**
     * Defineix la política de duplicats.
     *
     * @param duplicatePolicy nova política
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> setDuplicatePolicy(MenuDuplicatePolicy duplicatePolicy) {
        return mutator.mutateNoCache(() -> snapshotManager.setDuplicatePolicy(duplicatePolicy));
    }

    /**
     * Elimina un snapshot registrat pel seu nom.
     *
     * @param name nom del snapshot a eliminar
     * @return {@code true} si existia i s'ha eliminat; {@code false} altrament
     */
    public boolean removeRegisteredSnapshot(String name) {
        ensureCanModifyDuringRun();
        return snapshotManager.removeRegisteredSnapshot(name);
    }

    /**
     * Elimina tots els snapshots registrats del menú.
     *
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> clearRegisteredSnapshots() {
        return mutator.mutateNoCache(snapshotManager::clearRegisteredSnapshots);

    }

    /**
     * Elimina tots els snapshots desats a la pila del menú.
     *
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> clearSnapshotStack() {
        return mutator.mutateNoCache(snapshotManager::clearSnapshotStack);
    }

    /**
     * Retorna el nombre actual de snapshots registrats per nom.
     *
     * @return nombre de snapshots registrats
     */
    public int registeredSnapshotCount() {
        return snapshotManager.registeredSnapshotCount();
    }

    /**
     * Crea un snapshot fill a partir del snapshot actual.
     *
     * @param childTitle nou títol del fill; si és {@code null}, conserva el títol
     *                   actual
     * @return snapshot fill independent
     */
    public MenuSnapshot<T, C> createChildSnapshot(String childTitle) {
        return snapshotManager.createChildSnapshot(childTitle);
    }

    /**
     * Crea un snapshot fill a partir del snapshot actual mantenint el títol.
     *
     * @return snapshot fill independent
     */
    public MenuSnapshot<T, C> createChildSnapshot() {
        return snapshotManager.createChildSnapshot();
    }

    /**
     * Crea un snapshot fill amb un context propi.
     *
     * @param childTitle   nou títol del fill; si és {@code null}, conserva el títol
     *                     actual
     * @param childContext context del snapshot fill; pot ser {@code null}
     * @return snapshot fill independent
     */
    public MenuSnapshot<T, C> createChildSnapshot(String childTitle, C childContext) {
        return snapshotManager.createChildSnapshot(childTitle, childContext);
    }

    /**
     * Crea un snapshot fill copiant el context actual.
     *
     * @param childTitle     nou títol del fill; si és {@code null}, conserva el títol
     *                       actual
     * @param contextCopier  funció que copia el context actual
     * @return snapshot fill independent
     */
    public MenuSnapshot<T, C> createChildSnapshotCopyingContext(
            String childTitle,
            Function<? super C, ? extends C> contextCopier) {

        return snapshotManager.createChildSnapshotCopyingContext(childTitle, contextCopier);
    }

    /**
     * Crea un menú fill executable a partir del snapshot actual.
     *
     * <p>
     * El menú fill reutilitza el mateix selector que el menú pare i hereta la
     * mateixa referència de context, però manté un estat independent.
     * </p>
     *
     * @param childTitle nou títol del menú fill; si és {@code null}, conserva el
     *                   títol actual
     * @return nou menú fill executable i independent
     */
    public DynamicMenu<T, C> createChildMenu(String childTitle) {
        MenuSnapshotManager<T, C> childManager = snapshotManager.createChildManager(
                childTitle,
                autoCleanupEnabled,
                cleanupConfig);

        DynamicMenu<T, C> childMenu = new DynamicMenu<>(selector, childManager);
        childMenu.modifiableDuringRun = this.modifiableDuringRun;
        childMenu.autoCleanupEnabled = this.autoCleanupEnabled;
        childMenu.cleanupConfig = this.cleanupConfig;
        return childMenu;
    }

    /**
     * Crea un menú fill executable a partir del snapshot actual mantenint el títol.
     *
     * @return nou menú fill executable i independent
     */
    public DynamicMenu<T, C> createChildMenu() {
        return createChildMenu(null);
    }

    /**
     * Crea un menú fill executable amb un context propi.
     *
     * @param childTitle   nou títol del menú fill; si és {@code null}, conserva el
     *                     títol actual
     * @param childContext context del menú fill; pot ser {@code null}
     * @return nou menú fill executable i independent
     */
    public DynamicMenu<T, C> createChildMenu(String childTitle, C childContext) {
        MenuSnapshotManager<T, C> childManager = snapshotManager.createChildManager(
                childTitle,
                childContext,
                autoCleanupEnabled,
                cleanupConfig);

        DynamicMenu<T, C> childMenu = new DynamicMenu<>(selector, childManager);
        childMenu.modifiableDuringRun = this.modifiableDuringRun;
        childMenu.autoCleanupEnabled = this.autoCleanupEnabled;
        childMenu.cleanupConfig = this.cleanupConfig;
        return childMenu;
    }

    /**
     * Crea un menú fill executable copiant el context actual.
     *
     * @param childTitle    nou títol del menú fill; si és {@code null}, conserva el
     *                      títol actual
     * @param contextCopier funció que copia el context actual
     * @return nou menú fill executable i independent
     */
    public DynamicMenu<T, C> createChildMenuCopyingContext(
            String childTitle,
            Function<? super C, ? extends C> contextCopier) {

        MenuSnapshotManager<T, C> childManager = snapshotManager.createChildManagerCopyingContext(
                childTitle,
                contextCopier,
                autoCleanupEnabled,
                cleanupConfig);

        DynamicMenu<T, C> childMenu = new DynamicMenu<>(selector, childManager);
        childMenu.modifiableDuringRun = this.modifiableDuringRun;
        childMenu.autoCleanupEnabled = this.autoCleanupEnabled;
        childMenu.cleanupConfig = this.cleanupConfig;
        return childMenu;
    }

    /**
     * Wrapper de compatibilitat que restaura el menú actual a partir d'un snapshot.
     *
     * @param snapshot snapshot a restaurar
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> restoreSnapshot(MenuSnapshot<T, C> snapshot) {
        return mutator.mutate(() -> snapshotManager.restoreSnapshot(snapshot));
    }

    /**
     * Wrapper de compatibilitat que registra l'estat actual sota un nom.
     *
     * @param name nom del set o snapshot registrat
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> saveCurrentAs(String name) {
        ensureCanModifyDuringRun();
        snapshotManager.saveCurrentAs(name)
                .cleanupRegisteredSnapshotsIfNeeded(autoCleanupEnabled, cleanupConfig);
        return this;
    }

    /**
     * Wrapper de compatibilitat que registra un snapshot sota un nom.
     *
     * @param name     nom de registre
     * @param snapshot snapshot a registrar
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> registerSnapshot(String name, MenuSnapshot<T, C> snapshot) {
        ensureCanModifyDuringRun();
        snapshotManager.registerSnapshot(name, snapshot)
                .cleanupRegisteredSnapshotsIfNeeded(autoCleanupEnabled, cleanupConfig);
        return this;
    }

    /**
     * Indica si existeix un snapshot registrat amb aquest nom.
     *
     * @param name nom del snapshot registrat
     * @return {@code true} si existeix
     */
    public boolean hasRegisteredSnapshot(String name) {
        return snapshotManager.hasRegisteredSnapshot(name);
    }

    /**
     * Wrapper de compatibilitat que restaura un snapshot registrat per nom.
     *
     * @param name nom del snapshot registrat
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> useSnapshot(String name) {
        return mutator.mutate(() -> snapshotManager.useSnapshot(name));
    }

    /**
     * Wrapper de compatibilitat que desa el snapshot actual a la pila i carrega un
     * snapshot registrat.
     *
     * @param name nom del snapshot registrat
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> pushSnapshot(String name) {
        return mutator.mutate(() -> snapshotManager.pushSnapshot(name));
    }

    /**
     * Wrapper de compatibilitat que desa el snapshot actual a la pila.
     *
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> pushSnapshot() {
        return mutator.mutateNoCache(snapshotManager::pushSnapshot);
    }

    /**
     * Wrapper de compatibilitat que desa el snapshot actual a la pila i carrega el
     * snapshot fill indicat.
     *
     * @param childSnapshot snapshot fill a activar
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> pushChildSnapshot(MenuSnapshot<T, C> childSnapshot) {
        return mutator.mutate(() -> snapshotManager.pushChildSnapshot(childSnapshot));
    }

    /**
     * Wrapper de compatibilitat que restaura l'últim snapshot desat a la pila.
     *
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> popSnapshot() {
        return mutator.mutate(snapshotManager::popSnapshot);
    }

    /**
     * Retorna el nombre de snapshots desats a la pila.
     *
     * @return mida actual de la pila de snapshots
     */
    public int snapshotStackSize() {
        return snapshotManager.snapshotStackSize();
    }

    /**
     * Activa o desactiva la neteja automàtica.
     *
     * @param enabled {@code true} per activar la neteja automàtica; {@code false}
     *                per desactivar-la
     * @return aquest mateix menú
     * @throws IllegalStateException si es crida mentre el menú s'està executant
     */
    public DynamicMenu<T, C> autoCleanup(boolean enabled) {
        if (running) {
            throw new IllegalStateException(
                    "No es pot canviar la neteja automàtica mentre el menú s'està executant");
        }

        this.autoCleanupEnabled = enabled;

        if (enabled) {
            if (cleanupConfig == null || cleanupConfig.maxNamedSnapshots() <= 0) {
                cleanupConfig = MenuCleanupConfig.defaults();
            }
            snapshotManager.cleanupRegisteredSnapshotsIfNeeded(true, cleanupConfig);
        } else {
            cleanupConfig = MenuCleanupConfig.disabled();
        }

        return this;
    }

    /**
     * Indica si la neteja automàtica està activada.
     *
     * @return {@code true} si està activada
     */
    public boolean isAutoCleanupEnabled() {
        return autoCleanupEnabled;
    }

    /**
     * Defineix la configuració de neteja automàtica.
     *
     * @param config configuració de neteja
     * @return aquest mateix menú
     * @throws NullPointerException  si {@code config} és {@code null}
     * @throws IllegalStateException si es crida mentre el menú s'està executant
     */
    public DynamicMenu<T, C> cleanupConfig(MenuCleanupConfig config) {
        if (running) {
            throw new IllegalStateException(
                    "No es pot canviar la configuració de neteja mentre el menú s'està executant");
        }

        this.cleanupConfig = Objects.requireNonNull(config, "La configuració de neteja no pot ser nul·la");
        this.autoCleanupEnabled = true;
        snapshotManager.cleanupRegisteredSnapshotsIfNeeded(true, cleanupConfig);
        return this;
    }

    /**
     * Retorna la configuració de neteja actual.
     *
     * @return configuració de neteja actual
     */
    public MenuCleanupConfig getCleanupConfig() {
        return cleanupConfig;
    }

    /**
     * Retorna una instantània immutable de les opcions actuals.
     *
     * @return llista immutable d'opcions actuals
     */
    public List<MenuOption<T, C>> getCurrentOptionSnapshot() {
        return snapshotManager.getCurrentOptionSnapshot();
    }

    /**
     * Indica si ja existeix una opció amb l'etiqueta indicada.
     *
     * @param label etiqueta a cercar
     * @return {@code true} si existeix almenys una coincidència
     */
    public boolean hasOption(String label) {
        return snapshotManager.hasOption(label);
    }

    /**
     * Retorna la posició de la primera opció amb l'etiqueta indicada.
     *
     * @param label etiqueta a cercar
     * @return índex de la primera coincidència, o {@code -1} si no existeix
     */
    public int indexOfOption(String label) {
        return snapshotManager.indexOfOption(label);
    }

    /**
     * Retorna el nombre d'opcions actuals.
     *
     * @return nombre d'opcions actuals
     */
    public int optionCount() {
        return snapshotManager.optionCount();
    }

    /** Afegeix una opció al final del menú actual. */
    public DynamicMenu<T, C> addOption(String label, MenuAction<T, C> action) {
        return mutator.mutate(() -> snapshotManager.addOption(label, action));
    }

    /** Afegeix una opció al final del menú actual. */
    public DynamicMenu<T, C> addOption(String label, SimpleMenuAction<T> action) {
        return mutator.mutate(() -> snapshotManager.addOption(label, action));
    }

    /** Afegeix una opció al final del menú actual. */
    public DynamicMenu<T, C> addOption(String label, MenuRuntimeAction<T, C> action) {
        return mutator.mutate(() -> snapshotManager.addOption(label, action));
    }

    /** Afegeix una opció en una posició concreta. */
    public DynamicMenu<T, C> addOptionAt(int index, String label, MenuAction<T, C> action) {
        return mutator.mutate(() -> snapshotManager.addOptionAt(index, label, action));
    }

    /** Afegeix una opció en una posició concreta. */
    public DynamicMenu<T, C> addOptionAt(int index, String label, SimpleMenuAction<T> action) {
        return mutator.mutate(() -> snapshotManager.addOptionAt(index, label, action));
    }

    /** Afegeix una opció en una posició concreta. */
    public DynamicMenu<T, C> addOptionAt(int index, String label, MenuRuntimeAction<T, C> action) {
        return mutator.mutate(() -> snapshotManager.addOptionAt(index, label, action));
    }

    /** Afegeix una opció abans d'una altra existent. */
    public DynamicMenu<T, C> addOptionBefore(String referenceLabel, String label, MenuAction<T, C> action) {
        return mutator.mutate(() -> snapshotManager.addOptionBefore(referenceLabel, label, action));
    }

    /** Afegeix una opció abans d'una altra existent. */
    public DynamicMenu<T, C> addOptionBefore(String referenceLabel, String label, SimpleMenuAction<T> action) {
        return mutator.mutate(() -> snapshotManager.addOptionBefore(referenceLabel, label, action));
    }

    /** Afegeix una opció abans d'una altra existent. */
    public DynamicMenu<T, C> addOptionBefore(String referenceLabel, String label, MenuRuntimeAction<T, C> action) {
        return mutator.mutate(() -> snapshotManager.addOptionBefore(referenceLabel, label, action));
    }

    /** Afegeix una opció després d'una altra existent. */
    public DynamicMenu<T, C> addOptionAfter(String referenceLabel, String label, MenuAction<T, C> action) {
        return mutator.mutate(() -> snapshotManager.addOptionAfter(referenceLabel, label, action));
    }

    /** Afegeix una opció després d'una altra existent. */
    public DynamicMenu<T, C> addOptionAfter(String referenceLabel, String label, SimpleMenuAction<T> action) {
        return mutator.mutate(() -> snapshotManager.addOptionAfter(referenceLabel, label, action));
    }

    /** Afegeix una opció després d'una altra existent. */
    public DynamicMenu<T, C> addOptionAfter(String referenceLabel, String label, MenuRuntimeAction<T, C> action) {
        return mutator.mutate(() -> snapshotManager.addOptionAfter(referenceLabel, label, action));
    }

    /**
     * Mou una opció a l'inici.
     *
     * @param index índex actual de l'opció
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> moveOptionToStart(int index) {
        return mutator.mutate(() -> snapshotManager.moveOptionToStart(index));
    }

    /**
     * Mou una opció al final.
     *
     * @param index índex actual de l'opció
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> moveOptionToEnd(int index) {
        return mutator.mutate(() -> snapshotManager.moveOptionToEnd(index));
    }

    /**
     * Mou una opció a una posició concreta.
     *
     * @param fromIndex índex actual de l'opció
     * @param toIndex   índex de destí
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> moveOptionToIndex(int fromIndex, int toIndex) {
        return mutator.mutate(() -> snapshotManager.moveOptionToIndex(fromIndex, toIndex));
    }

    /**
     * Mou una opció abans d'una altra posició.
     *
     * @param fromIndex   índex actual de l'opció
     * @param targetIndex índex de referència
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> moveOptionBefore(int fromIndex, int targetIndex) {
        return mutator.mutate(() -> snapshotManager.moveOptionBefore(fromIndex, targetIndex));
    }

    /**
     * Mou una opció després d'una altra posició.
     *
     * @param fromIndex   índex actual de l'opció
     * @param targetIndex índex de referència
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> moveOptionAfter(int fromIndex, int targetIndex) {
        return mutator.mutate(() -> snapshotManager.moveOptionAfter(fromIndex, targetIndex));
    }

    /** Elimina la primera opció amb l'etiqueta indicada. */
    public boolean removeOption(String label) {
        return mutator.mutateBoolean(() -> snapshotManager.removeOption(label));
    }

    /**
     * Elimina l'opció d'un índex concret.
     *
     * @param index índex de l'opció a eliminar
     * @return aquest mateix menú
     */
    public DynamicMenu<T, C> removeOptionAt(int index) {
        return mutator.mutate(() -> snapshotManager.removeOptionAt(index));
    }

    /**
     * Elimina totes les opcions amb l'etiqueta indicada.
     *
     * @param label etiqueta a eliminar
     * @return nombre d'opcions eliminades
     */
    public int removeAllOptions(String label) {
        return mutator.mutateInt(() -> snapshotManager.removeAllOptions(label));
    }

    /** Elimina totes les opcions del menú actual. */
    public DynamicMenu<T, C> clearOptions() {
        return mutator.mutate(snapshotManager::clearOptions);
    }

    /** Defineix el hook executat abans de mostrar les opcions. */
    public DynamicMenu<T, C> beforeEachDisplay(MenuLoopHook<T, C> hook) {
        return mutator.mutateNoCache(() -> snapshotManager.beforeEachDisplay(hook));
    }

    /** Defineix el hook executat abans de l'acció triada. */
    public DynamicMenu<T, C> beforeEachAction(MenuLoopHook<T, C> hook) {
        return mutator.mutateNoCache(() -> snapshotManager.beforeEachAction(hook));
    }

    /** Defineix el hook executat després de l'acció triada. */
    public DynamicMenu<T, C> afterEachAction(MenuLoopHook<T, C> hook) {
        return mutator.mutateNoCache(() -> snapshotManager.afterEachAction(hook));
    }

    /**
     * Executa el menú en bucle fins que una acció indiqui retorn o sortida.
     *
     * @return valor retornat per una acció de tipus {@code RETURN}, o {@code null}
     *         en cas de {@code EXIT}
     * @throws IllegalStateException si ja s'està executant, si no hi ha opcions,
     *                               si el selector retorna una opció fora de rang o
     *                               si una acció retorna {@code null}
     */
    public T run() {
        if (running) {
            throw new IllegalStateException(
                    "No es pot cridar run() mentre aquest menú ja s'està executant");
        }

        running = true;

        try {
            int iteration = 1;

            while (true) {
                MenuSnapshot<T, C> snapshotState = modifiableDuringRun
                        ? snapshotManager.beginIterationView()
                        : snapshotManager.getCurrentSnapshotView();

                C iterationContext = snapshotState.getContext();
                List<MenuOption<T, C>> optionSnapshot = snapshotState.getOptionSnapshot();

                if (optionSnapshot.isEmpty()) {
                    throw new IllegalStateException(
                            "El menú ha de tenir com a mínim una opció abans de continuar l'execució");
                }

                MenuLoopHook<T, C> displayHook = snapshotState.getBeforeEachDisplay();
                MenuLoopHook<T, C> beforeHook = snapshotState.getBeforeEachAction();
                MenuLoopHook<T, C> afterHook = snapshotState.getAfterEachAction();
                String titleSnapshot = snapshotState.getTitle();

                if (displayHook != null) {
                    displayHook.execute(new MenuLoopState<>(
                            iterationContext,
                            iteration,
                            null,
                            true,
                            null,
                            null));
                }

                List<String> optionTexts = optionTextCache.get(optionSnapshot, optionSnapshot);
                int selectedOptionNumber = selector.getOption(optionTexts, titleSnapshot);
                int selectedIndex = selectedOptionNumber - 1;

                if (selectedIndex < 0 || selectedIndex >= optionSnapshot.size()) {
                    throw new IllegalStateException(
                            "El selector ha retornat l'opció " + selectedOptionNumber
                                    + ", però el rang vàlid és 1.." + optionSnapshot.size());
                }

                MenuOption<T, C> selectedOption = optionSnapshot.get(selectedIndex);

                if (beforeHook != null) {
                    beforeHook.execute(new MenuLoopState<>(
                            iterationContext,
                            iteration,
                            null,
                            true,
                            selectedOptionNumber,
                            selectedOption.label()));
                }

                MenuResult<T> result = selectedOption.action().execute(iterationContext, this);
                if (result == null) {
                    throw new IllegalStateException("Les accions del menú no poden retornar null");
                }

                boolean willContinue = result.continuesLoop();

                if (afterHook != null) {
                    afterHook.execute(new MenuLoopState<>(
                            iterationContext,
                            iteration,
                            result,
                            willContinue,
                            selectedOptionNumber,
                            selectedOption.label()));
                }

                switch (result.getType()) {
                    case CONTINUE -> iteration++;
                    case RETURN -> {
                        return result.getValue();
                    }
                    case EXIT -> {
                        return null;
                    }
                    default -> throw new IllegalStateException(
                            "S'ha detectat un tipus de resultat de menú desconegut: " + result.getType());
                }
            }
        } finally {
            try {
                snapshotManager.performAutoCleanupAfterRun(autoCleanupEnabled, cleanupConfig);
            } finally {
                running = false;
            }
        }
    }

    /** Invalida les caches relacionades amb l'estat visible actual del menú. */
    private void invalidateVisibleMenuCaches() {
        optionTextCache.invalidate();
    }

    /**
     * Verifica si el menú es pot modificar en l'estat actual d'execució.
     *
     * @throws IllegalStateException si el menú s'està executant i no permet
     *                               modificacions durant {@link #run()}
     */
    private void ensureCanModifyDuringRun() {
        if (running && !modifiableDuringRun) {
            throw new IllegalStateException(
                    "No es pot modificar el menú mentre s'està executant perquè la modificació durant run() està desactivada");
        }
    }
}
