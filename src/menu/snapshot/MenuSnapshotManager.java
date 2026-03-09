package menu.snapshot;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import menu.DynamicMenu;
import menu.config.MenuCleanupConfig;
import menu.model.MenuOption;

/**
 * Gestiona l'estat relacionat amb snapshots d'un {@link DynamicMenu}.
 *
 * <p>
 * Aquesta classe encapsula tota la responsabilitat de mantenir:
 * </p>
 *
 * <ul>
 * <li>el snapshot actual del menú;</li>
 * <li>els snapshots registrats per nom;</li>
 * <li>la pila interna de snapshots;</li>
 * <li>la creació de snapshots i menús fills;</li>
 * <li>l'aplicació de la política de neteja automàtica sobre aquest estat.</li>
 * </ul>
 *
 * <p>
 * La classe no controla si el menú es pot modificar mentre s'està executant.
 * Aquesta responsabilitat continua sent del motor principal, que actua com a
 * capa d'orquestració i exposa wrappers per mantenir la compatibilitat de l'API
 * pública.
 * </p>
 *
 * <p>
 * Aquesta implementació utilitza una estratègia de <i>copy-on-write</i> per al
 * snapshot actual. Quan l'estat actual està compartit amb registres o amb la
 * pila, no se'n crea una còpia immediatament. La còpia només es materialitza
 * just abans de la primera mutació sobre aquest snapshot.
 * </p>
 *
 * <p>
 * Els snapshots conserven la <b>referència</b> al context, no una còpia profunda
 * del seu estat intern.
 * </p>
 *
 * <p>
 * La política de duplicats només afecta noves operacions d'afegit. Canviar-la
 * no altera les opcions que ja existeixen al snapshot actual.
 * </p>
 *
 * <p>
 * <b>Important:</b> aquesta classe no és segura per a entorns concurrents.
 * </p>
 *
 * @param <T> tipus del valor de retorn del menú
 * @param <C> tipus del context associat al menú
 */
public final class MenuSnapshotManager<T, C> {

    private MenuSnapshot<T, C> currentSnapshot;
    private boolean currentSnapshotShared;

    private final Map<String, MenuSnapshot<T, C>> registeredSnapshots;
    private final Deque<MenuSnapshot<T, C>> snapshotStack;

    private MenuDuplicatePolicy duplicatePolicy;

    /**
     * Crea un gestor de snapshots amb un snapshot inicial buit a partir d'un títol.
     *
     * @param initialTitle títol inicial del snapshot actual
     * @throws NullPointerException si {@code initialTitle} és {@code null}
     */
    public MenuSnapshotManager(String initialTitle) {
        this(initialTitle, null, MenuDuplicatePolicy.DISALLOW_THROW);
    }

    /**
     * Crea un gestor de snapshots amb un snapshot inicial buit i context.
     *
     * @param initialTitle títol inicial del snapshot actual
     * @param context      context inicial del menú; pot ser {@code null}
     * @throws NullPointerException si {@code initialTitle} és {@code null}
     */
    public MenuSnapshotManager(String initialTitle, C context) {
        this(initialTitle, context, MenuDuplicatePolicy.DISALLOW_THROW);
    }

    /**
     * Crea un gestor de snapshots amb un snapshot inicial buit i una política de
     * duplicats.
     *
     * @param initialTitle    títol inicial del snapshot actual
     * @param duplicatePolicy política de duplicats
     * @throws NullPointerException si algun paràmetre és {@code null}
     */
    public MenuSnapshotManager(String initialTitle, MenuDuplicatePolicy duplicatePolicy) {
        this(initialTitle, null, duplicatePolicy);
    }

    /**
     * Crea un gestor de snapshots amb un snapshot inicial buit, context i una
     * política de duplicats.
     *
     * @param initialTitle    títol inicial del snapshot actual
     * @param context         context inicial del menú; pot ser {@code null}
     * @param duplicatePolicy política de duplicats
     * @throws NullPointerException si {@code initialTitle} o
     *                              {@code duplicatePolicy} són {@code null}
     */
    public MenuSnapshotManager(String initialTitle, C context, MenuDuplicatePolicy duplicatePolicy) {
        this(
                new MenuSnapshot<>(Objects.requireNonNull(
                        initialTitle,
                        "El títol inicial del gestor de snapshots no pot ser nul"), context),
                duplicatePolicy);
    }

    /**
     * Crea un gestor de snapshots a partir d'un snapshot inicial.
     *
     * <p>
     * El snapshot rebut es tracta inicialment com a compartit. Si posteriorment es
     * modifica des del gestor, s'aplicarà copy-on-write abans de mutar-lo.
     * </p>
     *
     * @param initialSnapshot snapshot inicial del gestor
     * @throws NullPointerException si {@code initialSnapshot} és {@code null}
     */
    public MenuSnapshotManager(MenuSnapshot<T, C> initialSnapshot) {
        this(initialSnapshot, MenuDuplicatePolicy.DISALLOW_THROW);
    }

    /**
     * Crea un gestor de snapshots a partir d'un snapshot inicial i una política de
     * duplicats.
     *
     * @param initialSnapshot snapshot inicial del gestor
     * @param duplicatePolicy política de duplicats
     * @throws NullPointerException si algun paràmetre és {@code null}
     */
    public MenuSnapshotManager(MenuSnapshot<T, C> initialSnapshot, MenuDuplicatePolicy duplicatePolicy) {
        this.currentSnapshot = Objects.requireNonNull(
                initialSnapshot,
                "El snapshot inicial del gestor no pot ser nul");
        this.currentSnapshotShared = true;
        this.registeredSnapshots = new LinkedHashMap<>();
        this.snapshotStack = new ArrayDeque<>();
        this.duplicatePolicy = Objects.requireNonNull(
                duplicatePolicy,
                "La política de duplicats no pot ser nul·la");
    }

    /**
     * Retorna el títol del snapshot actual.
     *
     * @return títol actual
     */
    public String getTitle() {
        return currentSnapshot.getTitle();
    }

    /**
     * Defineix el títol del snapshot actual.
     *
     * @param title nou títol
     * @return aquest mateix gestor
     * @throws NullPointerException si {@code title} és {@code null}
     */
    public MenuSnapshotManager<T, C> setTitle(String title) {
        ensureWritableCurrentSnapshot();
        currentSnapshot.setTitle(title);
        return this;
    }

    /**
     * Retorna el context actual.
     *
     * @return context actual, que pot ser {@code null}
     */
    public C getContext() {
        return currentSnapshot.getContext();
    }

    /**
     * Defineix el context actual.
     *
     * @param context nou context; pot ser {@code null}
     * @return aquest mateix gestor
     */
    public MenuSnapshotManager<T, C> setContext(C context) {
        ensureWritableCurrentSnapshot();
        currentSnapshot.setContext(context);
        return this;
    }

    /**
     * Retorna la política actual de duplicats.
     *
     * @return política activa
     */
    public MenuDuplicatePolicy getDuplicatePolicy() {
        return duplicatePolicy;
    }

    /**
     * Defineix la política de duplicats.
     *
     * @param duplicatePolicy nova política
     * @return aquest mateix gestor
     * @throws NullPointerException si {@code duplicatePolicy} és {@code null}
     */
    public MenuSnapshotManager<T, C> setDuplicatePolicy(MenuDuplicatePolicy duplicatePolicy) {
        this.duplicatePolicy = Objects.requireNonNull(
                duplicatePolicy,
                "La política de duplicats no pot ser nul·la");
        return this;
    }

    /**
     * Retorna una còpia del snapshot actual.
     *
     * @return còpia independent del snapshot actual
     */
    public MenuSnapshot<T, C> createSnapshot() {
        return currentSnapshot.copy();
    }

    /**
     * Retorna una instantània immutable de les opcions actuals.
     *
     * @return llista immutable d'opcions actuals
     */
    public List<MenuOption<T, C>> getCurrentOptionSnapshot() {
        return currentSnapshot.getOptionSnapshot();
    }

    /**
     * Indica si el snapshot actual ja conté una opció amb l'etiqueta indicada.
     *
     * @param label etiqueta a cercar
     * @return {@code true} si existeix almenys una coincidència
     */
    public boolean hasOption(String label) {
        return currentSnapshot.hasOption(label);
    }

    /**
     * Retorna la posició de la primera opció amb l'etiqueta indicada.
     *
     * @param label etiqueta a cercar
     * @return índex de la primera coincidència, o {@code -1} si no existeix
     */
    public int indexOfOption(String label) {
        return currentSnapshot.indexOfOption(label);
    }

    /**
     * Retorna el nombre d'opcions actuals.
     *
     * @return nombre d'opcions actuals
     */
    public int optionCount() {
        return currentSnapshot.optionCount();
    }

    public MenuSnapshotManager<T, C> addOption(String label, menu.action.MenuAction<T, C> action) {
        return handleAddToEnd(label, () -> currentSnapshot.addOption(label, action));
    }

    public MenuSnapshotManager<T, C> addOption(String label, menu.action.SimpleMenuAction<T> action) {
        return handleAddToEnd(label, () -> currentSnapshot.addOption(label, action));
    }

    public MenuSnapshotManager<T, C> addOption(String label, menu.action.MenuRuntimeAction<T, C> action) {
        return handleAddToEnd(label, () -> currentSnapshot.addOption(label, action));
    }

    public MenuSnapshotManager<T, C> addOptionAt(int index, String label, menu.action.MenuAction<T, C> action) {
        return handleAddAt(index, label, adjustedIndex -> currentSnapshot.addOptionAt(adjustedIndex, label, action));
    }

    public MenuSnapshotManager<T, C> addOptionAt(int index, String label, menu.action.SimpleMenuAction<T> action) {
        return handleAddAt(index, label, adjustedIndex -> currentSnapshot.addOptionAt(adjustedIndex, label, action));
    }

    public MenuSnapshotManager<T, C> addOptionAt(
            int index,
            String label,
            menu.action.MenuRuntimeAction<T, C> action) {

        return handleAddAt(index, label, adjustedIndex -> currentSnapshot.addOptionAt(adjustedIndex, label, action));
    }

    public MenuSnapshotManager<T, C> addOptionBefore(
            String referenceLabel,
            String label,
            menu.action.MenuAction<T, C> action) {

        return handleAddRelative(
                referenceLabel,
                label,
                () -> currentSnapshot.addOptionBefore(referenceLabel, label, action));
    }

    public MenuSnapshotManager<T, C> addOptionBefore(
            String referenceLabel,
            String label,
            menu.action.SimpleMenuAction<T> action) {

        return handleAddRelative(
                referenceLabel,
                label,
                () -> currentSnapshot.addOptionBefore(referenceLabel, label, action));
    }

    public MenuSnapshotManager<T, C> addOptionBefore(
            String referenceLabel,
            String label,
            menu.action.MenuRuntimeAction<T, C> action) {

        return handleAddRelative(
                referenceLabel,
                label,
                () -> currentSnapshot.addOptionBefore(referenceLabel, label, action));
    }

    public MenuSnapshotManager<T, C> addOptionAfter(
            String referenceLabel,
            String label,
            menu.action.MenuAction<T, C> action) {

        return handleAddRelative(
                referenceLabel,
                label,
                () -> currentSnapshot.addOptionAfter(referenceLabel, label, action));
    }

    public MenuSnapshotManager<T, C> addOptionAfter(
            String referenceLabel,
            String label,
            menu.action.SimpleMenuAction<T> action) {

        return handleAddRelative(
                referenceLabel,
                label,
                () -> currentSnapshot.addOptionAfter(referenceLabel, label, action));
    }

    public MenuSnapshotManager<T, C> addOptionAfter(
            String referenceLabel,
            String label,
            menu.action.MenuRuntimeAction<T, C> action) {

        return handleAddRelative(
                referenceLabel,
                label,
                () -> currentSnapshot.addOptionAfter(referenceLabel, label, action));
    }

    public MenuSnapshotManager<T, C> moveOptionToStart(int index) {
        return moveOptionToIndex(index, 0);
    }

    public MenuSnapshotManager<T, C> moveOptionToEnd(int index) {
        ensureWritableCurrentSnapshot();

        int size = optionCount();
        validateOptionIndex(index, size);

        if (index == size - 1) {
            return this;
        }

        currentSnapshot.moveOptionToEnd(index);
        return this;
    }

    public MenuSnapshotManager<T, C> moveOptionToIndex(int fromIndex, int toIndex) {
        ensureWritableCurrentSnapshot();

        int size = optionCount();
        validateOptionIndex(fromIndex, size);
        validatePositionIndex(toIndex, size);

        if (fromIndex == toIndex || (fromIndex == size - 1 && toIndex == size)) {
            return this;
        }

        if (toIndex == size) {
            currentSnapshot.moveOptionToEnd(fromIndex);
        } else {
            currentSnapshot.moveOptionToIndex(fromIndex, toIndex);
        }

        return this;
    }

    public MenuSnapshotManager<T, C> moveOptionBefore(int fromIndex, int targetIndex) {
        ensureWritableCurrentSnapshot();

        int size = optionCount();
        validateOptionIndex(fromIndex, size);
        validateOptionIndex(targetIndex, size);

        if (fromIndex == targetIndex) {
            return this;
        }

        currentSnapshot.moveOptionBefore(fromIndex, targetIndex);
        return this;
    }

    public MenuSnapshotManager<T, C> moveOptionAfter(int fromIndex, int targetIndex) {
        ensureWritableCurrentSnapshot();

        int size = optionCount();
        validateOptionIndex(fromIndex, size);
        validateOptionIndex(targetIndex, size);

        if (fromIndex == targetIndex) {
            return this;
        }

        currentSnapshot.moveOptionAfter(fromIndex, targetIndex);
        return this;
    }

    public boolean removeOption(String label) {
        ensureWritableCurrentSnapshot();
        return currentSnapshot.removeOption(label);
    }

    public MenuSnapshotManager<T, C> removeOptionAt(int index) {
        ensureWritableCurrentSnapshot();
        validateOptionIndex(index, optionCount());
        currentSnapshot.removeOptionAt(index);
        return this;
    }

    public int removeAllOptions(String label) {
        Objects.requireNonNull(label, "L'etiqueta de l'opció no pot ser nul·la");
        ensureWritableCurrentSnapshot();
        return currentSnapshot.removeAllOptions(label);
    }

    public MenuSnapshotManager<T, C> clearOptions() {
        ensureWritableCurrentSnapshot();
        currentSnapshot.clearOptions();
        return this;
    }

    public MenuSnapshotManager<T, C> beforeEachDisplay(menu.hook.MenuLoopHook<T, C> hook) {
        ensureWritableCurrentSnapshot();
        currentSnapshot.beforeEachDisplay(hook);
        return this;
    }

    public MenuSnapshotManager<T, C> beforeEachAction(menu.hook.MenuLoopHook<T, C> hook) {
        ensureWritableCurrentSnapshot();
        currentSnapshot.beforeEachAction(hook);
        return this;
    }

    public MenuSnapshotManager<T, C> afterEachAction(menu.hook.MenuLoopHook<T, C> hook) {
        ensureWritableCurrentSnapshot();
        currentSnapshot.afterEachAction(hook);
        return this;
    }

    /**
     * Restaura el menú actual a partir d'un snapshot.
     *
     * <p>
     * Aquesta operació manté una còpia defensiva perquè el snapshot rebut pot
     * provenir de fora del gestor.
     * </p>
     *
     * @param snapshot snapshot a restaurar
     * @return aquest mateix gestor
     * @throws NullPointerException si {@code snapshot} és {@code null}
     */
    public MenuSnapshotManager<T, C> restoreSnapshot(MenuSnapshot<T, C> snapshot) {
        this.currentSnapshot = Objects.requireNonNull(
                snapshot,
                "El snapshot a restaurar no pot ser nul").copy();
        this.currentSnapshotShared = false;
        return this;
    }

    public MenuSnapshotManager<T, C> saveCurrentAs(String name) {
        validateSnapshotName(name);
        registeredSnapshots.put(name, currentSnapshot);
        currentSnapshotShared = true;
        return this;
    }

    public MenuSnapshotManager<T, C> registerSnapshot(String name, MenuSnapshot<T, C> snapshot) {
        validateSnapshotName(name);
        registeredSnapshots.put(name, Objects.requireNonNull(
                snapshot,
                "El snapshot a registrar no pot ser nul").copy());
        return this;
    }

    public boolean hasRegisteredSnapshot(String name) {
        validateSnapshotName(name);
        return registeredSnapshots.containsKey(name);
    }

    public boolean removeRegisteredSnapshot(String name) {
        validateSnapshotName(name);
        return registeredSnapshots.remove(name) != null;
    }

    public MenuSnapshotManager<T, C> clearRegisteredSnapshots() {
        registeredSnapshots.clear();
        return this;
    }

    public int registeredSnapshotCount() {
        return registeredSnapshots.size();
    }

    public MenuSnapshotManager<T, C> useSnapshot(String name) {
        currentSnapshot = getRegisteredSnapshotShared(name);
        currentSnapshotShared = true;
        return this;
    }

    public MenuSnapshotManager<T, C> pushSnapshot(String name) {
        snapshotStack.push(currentSnapshot);
        currentSnapshot = getRegisteredSnapshotShared(name);
        currentSnapshotShared = true;
        return this;
    }

    public MenuSnapshotManager<T, C> pushSnapshot() {
        snapshotStack.push(currentSnapshot);
        currentSnapshotShared = true;
        return this;
    }

    public MenuSnapshotManager<T, C> pushChildSnapshot(MenuSnapshot<T, C> childSnapshot) {
        snapshotStack.push(currentSnapshot);
        currentSnapshot = Objects.requireNonNull(
                childSnapshot,
                "El snapshot fill no pot ser nul").copy();
        currentSnapshotShared = false;
        return this;
    }

    public MenuSnapshotManager<T, C> popSnapshot() {
        if (snapshotStack.isEmpty()) {
            throw new IllegalStateException("No hi ha cap snapshot desat a la pila per restaurar");
        }

        currentSnapshot = snapshotStack.pop();
        currentSnapshotShared = true;
        return this;
    }

    public MenuSnapshotManager<T, C> clearSnapshotStack() {
        snapshotStack.clear();
        return this;
    }

    public int snapshotStackSize() {
        return snapshotStack.size();
    }

    /**
     * Crea un snapshot fill a partir del snapshot actual.
     *
     * @param childTitle nou títol del fill; si és {@code null}, conserva el títol
     *                   actual
     * @return snapshot fill independent
     */
    public MenuSnapshot<T, C> createChildSnapshot(String childTitle) {
        return currentSnapshot.createChild(childTitle);
    }

    /**
     * Crea un snapshot fill a partir del snapshot actual mantenint el títol.
     *
     * @return snapshot fill independent
     */
    public MenuSnapshot<T, C> createChildSnapshot() {
        return currentSnapshot.createChild();
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
        return currentSnapshot.createChild(childTitle, childContext);
    }

    /**
     * Crea un snapshot fill copiant el context actual.
     *
     * @param childTitle    nou títol del fill; si és {@code null}, conserva el títol
     *                      actual
     * @param contextCopier funció que copia el context actual
     * @return snapshot fill independent
     */
    public MenuSnapshot<T, C> createChildSnapshotCopyingContext(
            String childTitle,
            Function<? super C, ? extends C> contextCopier) {

        return currentSnapshot.createChildCopyingContext(childTitle, contextCopier);
    }

    /**
     * Crea un gestor fill a partir de l'estat actual compartint la referència del
     * context.
     *
     * @param childTitle         nou títol del fill; si és {@code null}, conserva el
     *                           títol actual
     * @param autoCleanupEnabled indica si la neteja automàtica està activada al
     *                           menú pare
     * @param cleanupConfig      configuració de neteja que governa l'herència dels
     *                           snapshots
     * @return nou gestor fill independent
     * @throws NullPointerException si {@code cleanupConfig} és {@code null}
     */
    public MenuSnapshotManager<T, C> createChildManager(
            String childTitle,
            boolean autoCleanupEnabled,
            MenuCleanupConfig cleanupConfig) {

        return createChildManagerFromSnapshot(
                currentSnapshot.createChild(childTitle),
                autoCleanupEnabled,
                cleanupConfig);
    }

    /**
     * Crea un gestor fill a partir de l'estat actual amb un context propi.
     *
     * @param childTitle         nou títol del fill; si és {@code null}, conserva el
     *                           títol actual
     * @param childContext       context del fill; pot ser {@code null}
     * @param autoCleanupEnabled indica si la neteja automàtica està activada al
     *                           menú pare
     * @param cleanupConfig      configuració de neteja que governa l'herència dels
     *                           snapshots
     * @return nou gestor fill independent
     * @throws NullPointerException si {@code cleanupConfig} és {@code null}
     */
    public MenuSnapshotManager<T, C> createChildManager(
            String childTitle,
            C childContext,
            boolean autoCleanupEnabled,
            MenuCleanupConfig cleanupConfig) {

        return createChildManagerFromSnapshot(
                currentSnapshot.createChild(childTitle, childContext),
                autoCleanupEnabled,
                cleanupConfig);
    }

    /**
     * Crea un gestor fill copiant el context actual.
     *
     * @param childTitle         nou títol del fill; si és {@code null}, conserva el
     *                           títol actual
     * @param contextCopier      funció que copia el context actual
     * @param autoCleanupEnabled indica si la neteja automàtica està activada al
     *                           menú pare
     * @param cleanupConfig      configuració de neteja que governa l'herència dels
     *                           snapshots
     * @return nou gestor fill independent
     * @throws NullPointerException si {@code contextCopier} o {@code cleanupConfig}
     *                              són {@code null}
     */
    public MenuSnapshotManager<T, C> createChildManagerCopyingContext(
            String childTitle,
            Function<? super C, ? extends C> contextCopier,
            boolean autoCleanupEnabled,
            MenuCleanupConfig cleanupConfig) {

        Objects.requireNonNull(contextCopier, "La funció de còpia del context no pot ser nul·la");

        return createChildManagerFromSnapshot(
                currentSnapshot.createChildCopyingContext(childTitle, contextCopier),
                autoCleanupEnabled,
                cleanupConfig);
    }

    public MenuSnapshotManager<T, C> cleanupRegisteredSnapshotsIfNeeded(
            boolean autoCleanupEnabled,
            MenuCleanupConfig cleanupConfig) {

        Objects.requireNonNull(cleanupConfig, "La configuració de neteja no pot ser nul·la");

        if (!autoCleanupEnabled) {
            return this;
        }

        int max = cleanupConfig.maxNamedSnapshots();
        if (max <= 0) {
            return this;
        }

        if (!cleanupConfig.removeOldestNamedSnapshotsWhenLimitExceeded()) {
            return this;
        }

        while (registeredSnapshots.size() > max) {
            String oldestKey = registeredSnapshots.keySet().iterator().next();
            registeredSnapshots.remove(oldestKey);
        }

        return this;
    }

    public MenuSnapshotManager<T, C> performAutoCleanupAfterRun(
            boolean autoCleanupEnabled,
            MenuCleanupConfig cleanupConfig) {

        Objects.requireNonNull(cleanupConfig, "La configuració de neteja no pot ser nul·la");

        if (!autoCleanupEnabled) {
            return this;
        }

        if (cleanupConfig.clearSnapshotStackAfterRun()) {
            snapshotStack.clear();
        }

        cleanupRegisteredSnapshotsIfNeeded(true, cleanupConfig);
        return this;
    }

    public MenuSnapshot<T, C> getCurrentSnapshotCopy() {
        return currentSnapshot.copy();
    }

    public MenuSnapshot<T, C> beginIterationView() {
        currentSnapshotShared = true;
        return currentSnapshot;
    }

    public MenuSnapshot<T, C> getCurrentSnapshotView() {
        return currentSnapshot;
    }

    /** Garanteix que el snapshot actual sigui exclusiu abans de mutar-lo. */
    private void ensureWritableCurrentSnapshot() {
        if (!currentSnapshotShared) {
            return;
        }

        currentSnapshot = currentSnapshot.copy();
        currentSnapshotShared = false;
    }

    /**
     * Crea un gestor fill a partir d'un snapshot base.
     *
     * @param childSnapshot      snapshot base del fill
     * @param autoCleanupEnabled indica si la neteja automàtica està activada al
     *                           menú pare
     * @param cleanupConfig      configuració de neteja aplicable
     * @return nou gestor fill independent
     */
    private MenuSnapshotManager<T, C> createChildManagerFromSnapshot(
            MenuSnapshot<T, C> childSnapshot,
            boolean autoCleanupEnabled,
            MenuCleanupConfig cleanupConfig) {

        Objects.requireNonNull(cleanupConfig, "La configuració de neteja no pot ser nul·la");

        MenuSnapshotManager<T, C> childManager = new MenuSnapshotManager<>(
                childSnapshot,
                duplicatePolicy);

        if (!autoCleanupEnabled) {
            copyRegisteredSnapshotsTo(childManager);
            return childManager;
        }

        if (cleanupConfig.copyOnlyCurrentSnapshotToChild()) {
            return childManager;
        }

        if (cleanupConfig.inheritNamedSnapshotsInChildMenus()) {
            copyRegisteredSnapshotsTo(childManager);
        }

        return childManager;
    }

    private MenuSnapshotManager<T, C> handleAddToEnd(String label, Runnable addOperation) {
        DuplicateAction action = resolveDuplicateAction(label);

        if (action == DuplicateAction.SKIP) {
            return this;
        }

        ensureWritableCurrentSnapshot();

        if (action == DuplicateAction.REPLACE) {
            currentSnapshot.removeOption(label);
        }

        addOperation.run();
        return this;
    }

    private MenuSnapshotManager<T, C> handleAddAt(
            int index,
            String label,
            IndexedAddOperation addOperation) {

        DuplicateAction action = resolveDuplicateAction(label);

        if (action == DuplicateAction.SKIP) {
            return this;
        }

        ensureWritableCurrentSnapshot();

        int adjustedIndex = index;

        if (action == DuplicateAction.REPLACE) {
            int existingIndex = currentSnapshot.indexOfOption(label);
            currentSnapshot.removeOption(label);
            if (existingIndex >= 0 && existingIndex < index) {
                adjustedIndex = index - 1;
            }
        }

        addOperation.add(adjustedIndex);
        return this;
    }

    private MenuSnapshotManager<T, C> handleAddRelative(
            String referenceLabel,
            String label,
            Runnable addOperation) {

        Objects.requireNonNull(referenceLabel, "L'etiqueta de referència no pot ser nul·la");

        DuplicateAction action = resolveDuplicateAction(label);

        if (action == DuplicateAction.SKIP) {
            return this;
        }

        ensureWritableCurrentSnapshot();

        if (action == DuplicateAction.REPLACE) {
            currentSnapshot.removeOption(label);
        }

        addOperation.run();
        return this;
    }

    private DuplicateAction resolveDuplicateAction(String label) {
        Objects.requireNonNull(label, "L'etiqueta de l'opció no pot ser nul·la");

        if (!currentSnapshot.hasOption(label)) {
            return DuplicateAction.ADD;
        }

        switch (duplicatePolicy) {
            case ALLOW:
                return DuplicateAction.ADD;

            case REPLACE:
                return DuplicateAction.REPLACE;

            case DISALLOW_IGNORE:
                return DuplicateAction.SKIP;

            case DISALLOW_THROW:
                throw new IllegalArgumentException(
                        "Ja existeix una opció amb l'etiqueta: " + label);

            default:
                throw new IllegalStateException(
                        "Política de duplicats no suportada: " + duplicatePolicy);
        }
    }

    private void validateOptionIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Índex d'opció fora de rang: " + index + ". Mida actual: " + size);
        }
    }

    private void validatePositionIndex(int index, int size) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "Posició d'inserció fora de rang: " + index + ". Mida actual: " + size);
        }
    }

    private MenuSnapshot<T, C> getRegisteredSnapshotShared(String name) {
        validateSnapshotName(name);

        MenuSnapshot<T, C> snapshot = registeredSnapshots.get(name);
        if (snapshot == null) {
            throw new IllegalArgumentException(
                    "No existeix cap snapshot registrat amb el nom: " + name);
        }

        return snapshot;
    }

    private void validateSnapshotName(String name) {
        Objects.requireNonNull(name, "El nom del snapshot no pot ser nul");
        if (name.isBlank()) {
            throw new IllegalArgumentException("El nom del snapshot no pot estar buit");
        }
    }

    private void copyRegisteredSnapshotsTo(MenuSnapshotManager<T, C> targetManager) {
        for (Map.Entry<String, MenuSnapshot<T, C>> entry : registeredSnapshots.entrySet()) {
            targetManager.registeredSnapshots.put(entry.getKey(), entry.getValue().copy());
        }
    }

    /** Representa l'acció efectiva davant d'un duplicat. */
    private enum DuplicateAction {
        ADD,
        REPLACE,
        SKIP
    }

    /** Operació d'afegit amb índex ajustable. */
    @FunctionalInterface
    private interface IndexedAddOperation {

        /**
         * Afegeix una opció a l'índex indicat.
         *
         * @param index índex final d'inserció
         */
        void add(int index);
    }
}
