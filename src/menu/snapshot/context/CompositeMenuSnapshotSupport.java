package menu.snapshot.context;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import menu.DynamicMenu;

/**
 * Suport extern per gestionar snapshots compostos de diferents menús.
 *
 * <p>
 * Aquest suport manté registres i pila per cada instància de menú rebuda. No
 * forma part de {@link DynamicMenu} i sempre rep el menú com a paràmetre en
 * cada operació.
 * </p>
 *
 * <p>
 * La pila de snapshots compostos pot limitar-se per instància. Per defecte,
 * el límit està actiu i és de {@value #DEFAULT_SNAPSHOT_STACK_LIMIT} snapshots
 * per menú.
 * </p>
 *
 * <p>
 * Si el límit està actiu i s'intenta afegir un nou snapshot a una pila plena,
 * s'elimina primer el snapshot més antic per mantenir la mida màxima
 * configurada.
 * </p>
 *
 * <p>
 * Es considera que el límit està desactivat quan el seu valor és
 * {@link #UNLIMITED_SNAPSHOT_STACK_LIMIT}.
 * </p>
 */
public final class CompositeMenuSnapshotSupport {

    /**
     * Valor que indica que la pila de snapshots no té límit.
     */
    public static final int UNLIMITED_SNAPSHOT_STACK_LIMIT = -1;

    /**
     * Límit predeterminat de snapshots compostos en pila per menú.
     */
    public static final int DEFAULT_SNAPSHOT_STACK_LIMIT = 20;

    private final Map<DynamicMenu<?, ?>, MenuSnapshotStore> storesByMenu;

    private int snapshotStackLimit;

    /**
     * Contenidor intern de snapshots associats a un menú.
     */
    private static final class MenuSnapshotStore {

        /**
         * Snapshots compostos registrats per nom.
         */
        private Map<String, CompositeMenuSnapshot<?, ?, ?>> registeredSnapshots;

        /**
         * Pila de snapshots compostos del menú.
         */
        private Deque<CompositeMenuSnapshot<?, ?, ?>> snapshotStack;
    }

    /**
     * Crea un suport buit de snapshots compostos amb el límit de pila
     * predeterminat activat.
     */
    public CompositeMenuSnapshotSupport() {
        this(DEFAULT_SNAPSHOT_STACK_LIMIT);
    }

    /**
     * Crea un suport buit de snapshots compostos amb el límit de pila indicat.
     *
     * <p>
     * Regles del límit:
     * </p>
     *
     * <ul>
     * <li>valor positiu: límit actiu</li>
     * <li>{@link #UNLIMITED_SNAPSHOT_STACK_LIMIT}: límit desactivat</li>
     * </ul>
     *
     * @param snapshotStackLimit límit inicial de la pila per menú
     */
    public CompositeMenuSnapshotSupport(int snapshotStackLimit) {
        this.storesByMenu = new IdentityHashMap<>();
        setSnapshotStackLimit(snapshotStackLimit);
    }

    /**
     * Retorna el límit actual de snapshots en pila per menú.
     *
     * @return límit actual o {@link #UNLIMITED_SNAPSHOT_STACK_LIMIT} si està
     *         desactivat
     */
    public int getSnapshotStackLimit() {
        return snapshotStackLimit;
    }

    /**
     * Indica si el límit de la pila de snapshots està actiu.
     *
     * @return {@code true} si el límit està actiu
     */
    public boolean isSnapshotStackLimitEnabled() {
        return snapshotStackLimit != UNLIMITED_SNAPSHOT_STACK_LIMIT;
    }

    /**
     * Configura el límit de snapshots compostos en pila per menú.
     *
     * <p>
     * Regles del límit:
     * </p>
     *
     * <ul>
     * <li>valor positiu: límit actiu</li>
     * <li>{@link #UNLIMITED_SNAPSHOT_STACK_LIMIT}: límit desactivat</li>
     * </ul>
     *
     * <p>
     * Si el nou límit és inferior a la mida actual d'alguna pila, aquestes
     * piles es retallen automàticament conservant els snapshots més recents.
     * </p>
     *
     * @param snapshotStackLimit nou límit de la pila per menú
     * @return aquest mateix suport
     * @throws IllegalArgumentException si el valor és 0 o inferior a
     *                                  {@link #UNLIMITED_SNAPSHOT_STACK_LIMIT}
     */
    public CompositeMenuSnapshotSupport setSnapshotStackLimit(int snapshotStackLimit) {
        validateSnapshotStackLimit(snapshotStackLimit);
        this.snapshotStackLimit = snapshotStackLimit;
        trimAllStacksToCurrentLimit();
        return this;
    }

    /**
     * Activa o desactiva el límit de snapshots en pila.
     *
     * <p>
     * En activar-lo, si actualment estava desactivat, es recupera el valor
     * predeterminat.
     * </p>
     *
     * <p>
     * En desactivar-lo, la pila passa a no tenir límit.
     * </p>
     *
     * @param enabled {@code true} per activar el límit, {@code false} per
     *                desactivar-lo
     * @return aquest mateix suport
     */
    public CompositeMenuSnapshotSupport setSnapshotStackLimitEnabled(boolean enabled) {
        if (enabled) {
            if (!isSnapshotStackLimitEnabled()) {
                this.snapshotStackLimit = DEFAULT_SNAPSHOT_STACK_LIMIT;
                trimAllStacksToCurrentLimit();
            }
        } else {
            this.snapshotStackLimit = UNLIMITED_SNAPSHOT_STACK_LIMIT;
        }
        return this;
    }

    /**
     * Crea un snapshot compost del menú indicat.
     *
     * @param menu menú a capturar
     * @param contextSnapshotFactory funció que crea el snapshot intern del context
     * @return snapshot compost
     */
    public <T, C, S> CompositeMenuSnapshot<T, C, S> createSnapshot(
            DynamicMenu<T, C> menu,
            Function<? super C, ? extends S> contextSnapshotFactory) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(
                contextSnapshotFactory,
                "La funció de snapshot del context no pot ser nul·la");

        return new CompositeMenuSnapshot<>(
                menu.createSnapshot(),
                contextSnapshotFactory.apply(menu.getContext()));
    }

    /**
     * Crea un snapshot compost del menú indicat utilitzant el mateix context.
     *
     * @param menu menú a capturar
     * @return snapshot compost
     */
    public <T, C extends ContextStateSnapshotSupport<S>, S> CompositeMenuSnapshot<T, C, S> createSnapshot(
            DynamicMenu<T, C> menu) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        C context = ensureContextPresent(menu);

        return new CompositeMenuSnapshot<>(
                menu.createSnapshot(),
                context.createContextStateSnapshot());
    }

    /**
     * Restaura un snapshot compost sobre el menú indicat.
     *
     * @param menu menú a restaurar
     * @param snapshot snapshot compost a restaurar
     * @param contextRestorer funció que restaura l'estat intern del context
     */
    public <T, C, S> void restoreSnapshot(
            DynamicMenu<T, C> menu,
            CompositeMenuSnapshot<T, C, S> snapshot,
            BiConsumer<? super C, ? super S> contextRestorer) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(snapshot, "El snapshot compost no pot ser nul");
        Objects.requireNonNull(
                contextRestorer,
                "La funció de restauració del context no pot ser nul·la");

        menu.restoreSnapshot(snapshot.menuSnapshot());
        contextRestorer.accept(menu.getContext(), snapshot.contextStateSnapshot());
    }

    /**
     * Restaura un snapshot compost sobre el menú indicat utilitzant el mateix
     * context.
     *
     * @param menu menú a restaurar
     * @param snapshot snapshot compost a restaurar
     */
    public <T, C extends ContextStateSnapshotSupport<S>, S> void restoreSnapshot(
            DynamicMenu<T, C> menu,
            CompositeMenuSnapshot<T, C, S> snapshot) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(snapshot, "El snapshot compost no pot ser nul");

        menu.restoreSnapshot(snapshot.menuSnapshot());
        ensureContextPresent(menu).restoreContextStateSnapshot(snapshot.contextStateSnapshot());
    }

    /**
     * Desa l'estat compost actual del menú sota un nom.
     *
     * @param menu menú a capturar
     * @param name nom de registre
     * @param contextSnapshotFactory funció que crea el snapshot intern del context
     * @return aquest mateix suport
     */
    public <T, C, S> CompositeMenuSnapshotSupport saveCurrentAs(
            DynamicMenu<T, C> menu,
            String name,
            Function<? super C, ? extends S> contextSnapshotFactory) {

        return registerSnapshot(menu, name, createSnapshot(menu, contextSnapshotFactory));
    }

    /**
     * Desa l'estat compost actual del menú sota un nom utilitzant el mateix
     * context.
     *
     * @param menu menú a capturar
     * @param name nom de registre
     * @return aquest mateix suport
     */
    public <T, C extends ContextStateSnapshotSupport<S>, S> CompositeMenuSnapshotSupport saveCurrentAs(
            DynamicMenu<T, C> menu,
            String name) {

        return registerSnapshot(menu, name, createSnapshot(menu));
    }

    /**
     * Registra un snapshot compost sota un nom per al menú indicat.
     *
     * @param menu menú propietari del registre
     * @param name nom de registre
     * @param snapshot snapshot a registrar
     * @return aquest mateix suport
     */
    public <T, C, S> CompositeMenuSnapshotSupport registerSnapshot(
            DynamicMenu<T, C> menu,
            String name,
            CompositeMenuSnapshot<T, C, S> snapshot) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        validateSnapshotName(name);
        Objects.requireNonNull(snapshot, "El snapshot compost no pot ser nul");

        Map<String, CompositeMenuSnapshot<?, ?, ?>> registered = registeredSnapshotsForWrite(menu);
        if (registered.putIfAbsent(name, snapshot) != null) {
            throw new IllegalStateException(
                    "Ja existeix un snapshot compost registrat amb el nom '" + name + "'");
        }

        return this;
    }

    /**
     * Indica si existeix un snapshot compost registrat per al menú indicat.
     *
     * @param menu menú propietari del registre
     * @param name nom de registre
     * @return {@code true} si existeix
     */
    public boolean hasRegisteredSnapshot(DynamicMenu<?, ?> menu, String name) {
        Objects.requireNonNull(menu, "El menú no pot ser nul");
        validateSnapshotName(name);

        Map<String, CompositeMenuSnapshot<?, ?, ?>> registered = registeredSnapshotsForRead(menu);
        return registered != null && registered.containsKey(name);
    }

    /**
     * Elimina un snapshot compost registrat pel seu nom.
     *
     * @param menu menú propietari del registre
     * @param name nom de registre
     * @return {@code true} si existia
     */
    public boolean removeRegisteredSnapshot(DynamicMenu<?, ?> menu, String name) {
        Objects.requireNonNull(menu, "El menú no pot ser nul");
        validateSnapshotName(name);

        Map<String, CompositeMenuSnapshot<?, ?, ?>> registered = registeredSnapshotsForRead(menu);
        if (registered == null) {
            return false;
        }

        boolean removed = registered.remove(name) != null;
        removeRegisteredSnapshotsIfEmpty(menu, registered);
        return removed;
    }

    /**
     * Elimina tots els snapshots compostos registrats del menú indicat.
     *
     * @param menu menú a netejar
     * @return aquest mateix suport
     */
    public CompositeMenuSnapshotSupport clearRegisteredSnapshots(DynamicMenu<?, ?> menu) {
        Objects.requireNonNull(menu, "El menú no pot ser nul");

        MenuSnapshotStore store = storeForRead(menu);
        if (store != null) {
            store.registeredSnapshots = null;
            removeStoreIfEmpty(menu, store);
        }

        return this;
    }

    /**
     * Retorna el nombre de snapshots compostos registrats del menú indicat.
     *
     * @param menu menú a consultar
     * @return nombre de snapshots registrats
     */
    public int registeredSnapshotCount(DynamicMenu<?, ?> menu) {
        Objects.requireNonNull(menu, "El menú no pot ser nul");

        Map<String, CompositeMenuSnapshot<?, ?, ?>> registered = registeredSnapshotsForRead(menu);
        return registered == null ? 0 : registered.size();
    }

    /**
     * Restaura un snapshot compost registrat pel seu nom.
     *
     * @param menu menú a restaurar
     * @param name nom de registre
     * @param contextRestorer funció que restaura l'estat intern del context
     */
    public <T, C, S> void useSnapshot(
            DynamicMenu<T, C> menu,
            String name,
            BiConsumer<? super C, ? super S> contextRestorer) {

        restoreSnapshot(menu, getRegisteredSnapshot(menu, name), contextRestorer);
    }

    /**
     * Restaura un snapshot compost registrat pel seu nom utilitzant el mateix
     * context.
     *
     * @param menu menú a restaurar
     * @param name nom de registre
     */
    public <T, C extends ContextStateSnapshotSupport<S>, S> void useSnapshot(
            DynamicMenu<T, C> menu,
            String name) {

        restoreSnapshot(menu, getRegisteredSnapshot(menu, name));
    }

    /**
     * Desa el snapshot compost actual a la pila del menú.
     *
     * <p>
     * Si el límit de pila està actiu i la pila és plena, s'elimina abans el
     * snapshot més antic.
     * </p>
     *
     * @param menu menú a capturar
     * @param contextSnapshotFactory funció que crea el snapshot intern del context
     * @return aquest mateix suport
     */
    public <T, C, S> CompositeMenuSnapshotSupport pushSnapshot(
            DynamicMenu<T, C> menu,
            Function<? super C, ? extends S> contextSnapshotFactory) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        pushSnapshotWithConfiguredLimit(menu, createSnapshot(menu, contextSnapshotFactory));
        return this;
    }

    /**
     * Desa el snapshot compost actual a la pila del menú utilitzant el mateix
     * context.
     *
     * <p>
     * Si el límit de pila està actiu i la pila és plena, s'elimina abans el
     * snapshot més antic.
     * </p>
     *
     * @param menu menú a capturar
     * @return aquest mateix suport
     */
    public <T, C extends ContextStateSnapshotSupport<S>, S> CompositeMenuSnapshotSupport pushSnapshot(
            DynamicMenu<T, C> menu) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        pushSnapshotWithConfiguredLimit(menu, createSnapshot(menu));
        return this;
    }

    /**
     * Desa l'estat compost actual a la pila i restaura un snapshot registrat.
     *
     * <p>
     * Si el límit de pila està actiu i la pila és plena, s'elimina abans el
     * snapshot més antic.
     * </p>
     *
     * @param menu menú a modificar
     * @param name nom del snapshot registrat
     * @param contextSnapshotFactory funció que crea el snapshot intern del context
     * @param contextRestorer funció que restaura l'estat intern del context
     * @return aquest mateix suport
     */
    public <T, C, S> CompositeMenuSnapshotSupport pushSnapshot(
            DynamicMenu<T, C> menu,
            String name,
            Function<? super C, ? extends S> contextSnapshotFactory,
            BiConsumer<? super C, ? super S> contextRestorer) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(
                contextSnapshotFactory,
                "La funció de snapshot del context no pot ser nul·la");
        Objects.requireNonNull(
                contextRestorer,
                "La funció de restauració del context no pot ser nul·la");

        CompositeMenuSnapshot<T, C, S> snapshotToUse = getRegisteredSnapshot(menu, name);
        pushSnapshotWithConfiguredLimit(menu, createSnapshot(menu, contextSnapshotFactory));
        restoreSnapshot(menu, snapshotToUse, contextRestorer);
        return this;
    }

    /**
     * Desa l'estat compost actual a la pila i restaura un snapshot registrat
     * utilitzant el mateix context.
     *
     * <p>
     * Si el límit de pila està actiu i la pila és plena, s'elimina abans el
     * snapshot més antic.
     * </p>
     *
     * @param menu menú a modificar
     * @param name nom del snapshot registrat
     * @return aquest mateix suport
     */
    public <T, C extends ContextStateSnapshotSupport<S>, S> CompositeMenuSnapshotSupport pushSnapshot(
            DynamicMenu<T, C> menu,
            String name) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");

        CompositeMenuSnapshot<T, C, S> snapshotToUse = getRegisteredSnapshot(menu, name);
        pushSnapshotWithConfiguredLimit(menu, createSnapshot(menu));
        restoreSnapshot(menu, snapshotToUse);
        return this;
    }

    /**
     * Restaura l'últim snapshot compost desat a la pila del menú.
     *
     * @param menu menú a restaurar
     * @param contextRestorer funció que restaura l'estat intern del context
     */
    public <T, C, S> void popSnapshot(
            DynamicMenu<T, C> menu,
            BiConsumer<? super C, ? super S> contextRestorer) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(
                contextRestorer,
                "La funció de restauració del context no pot ser nul·la");

        restoreSnapshot(menu, castSnapshot(popStoredSnapshot(menu)), contextRestorer);
    }

    /**
     * Restaura l'últim snapshot compost desat a la pila del menú utilitzant el
     * mateix context.
     *
     * @param menu menú a restaurar
     */
    public <T, C extends ContextStateSnapshotSupport<S>, S> void popSnapshot(
            DynamicMenu<T, C> menu) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        restoreSnapshot(menu, castSnapshot(popStoredSnapshot(menu)));
    }

    /**
     * Elimina tots els snapshots compostos de la pila del menú indicat.
     *
     * @param menu menú a netejar
     * @return aquest mateix suport
     */
    public CompositeMenuSnapshotSupport clearSnapshotStack(DynamicMenu<?, ?> menu) {
        Objects.requireNonNull(menu, "El menú no pot ser nul");

        MenuSnapshotStore store = storeForRead(menu);
        if (store != null) {
            store.snapshotStack = null;
            removeStoreIfEmpty(menu, store);
        }

        return this;
    }

    /**
     * Retorna la mida actual de la pila de snapshots compostos del menú indicat.
     *
     * @param menu menú a consultar
     * @return mida actual de la pila
     */
    public int snapshotStackSize(DynamicMenu<?, ?> menu) {
        Objects.requireNonNull(menu, "El menú no pot ser nul");

        Deque<CompositeMenuSnapshot<?, ?, ?>> stack = snapshotStackForRead(menu);
        return stack == null ? 0 : stack.size();
    }

    /**
     * Elimina tots els registres i piles de tots els menús gestionats.
     *
     * @return aquest mateix suport
     */
    public CompositeMenuSnapshotSupport clearAll() {
        storesByMenu.clear();
        return this;
    }

    /**
     * Retorna un snapshot compost registrat pel seu nom.
     *
     * @param menu menú propietari del registre
     * @param name nom de registre
     * @return snapshot compost registrat
     */
    public <T, C, S> CompositeMenuSnapshot<T, C, S> getRegisteredSnapshot(
            DynamicMenu<T, C> menu,
            String name) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        validateSnapshotName(name);

        Map<String, CompositeMenuSnapshot<?, ?, ?>> registered = registeredSnapshotsForRead(menu);
        CompositeMenuSnapshot<?, ?, ?> snapshot =
                registered == null ? null : registered.get(name);

        if (snapshot == null) {
            throw new IllegalStateException(
                    "No existeix cap snapshot compost registrat amb el nom '" + name + "'");
        }

        return castSnapshot(snapshot);
    }

    /**
     * Retorna el contenidor del menú sense crear-lo.
     *
     * @param menu menú a consultar
     * @return contenidor del menú o {@code null} si no existeix
     */
    private MenuSnapshotStore storeForRead(DynamicMenu<?, ?> menu) {
        return storesByMenu.get(menu);
    }

    /**
     * Retorna el contenidor del menú, creant-lo si cal.
     *
     * @param menu menú a preparar
     * @return contenidor del menú
     */
    private MenuSnapshotStore storeForWrite(DynamicMenu<?, ?> menu) {
        return storesByMenu.computeIfAbsent(menu, ignored -> new MenuSnapshotStore());
    }

    /**
     * Elimina el contenidor del menú si ha quedat buit.
     *
     * @param menu menú propietari del contenidor
     * @param store contenidor a comprovar
     */
    private void removeStoreIfEmpty(DynamicMenu<?, ?> menu, MenuSnapshotStore store) {
        boolean noRegisteredSnapshots =
                store.registeredSnapshots == null || store.registeredSnapshots.isEmpty();
        boolean noSnapshotStack =
                store.snapshotStack == null || store.snapshotStack.isEmpty();

        if (noRegisteredSnapshots && noSnapshotStack) {
            storesByMenu.remove(menu);
        }
    }

    /**
     * Retorna els snapshots registrats del menú sense crear-los.
     *
     * @param menu menú a consultar
     * @return mapa de snapshots o {@code null} si no n'hi ha
     */
    private Map<String, CompositeMenuSnapshot<?, ?, ?>> registeredSnapshotsForRead(
            DynamicMenu<?, ?> menu) {
        MenuSnapshotStore store = storeForRead(menu);
        return store == null ? null : store.registeredSnapshots;
    }

    /**
     * Retorna els snapshots registrats del menú, creant-los si cal.
     *
     * @param menu menú a preparar
     * @return mapa de snapshots del menú
     */
    private Map<String, CompositeMenuSnapshot<?, ?, ?>> registeredSnapshotsForWrite(
            DynamicMenu<?, ?> menu) {
        MenuSnapshotStore store = storeForWrite(menu);
        if (store.registeredSnapshots == null) {
            store.registeredSnapshots = new HashMap<>();
        }
        return store.registeredSnapshots;
    }

    /**
     * Retorna la pila de snapshots del menú sense crear-la.
     *
     * @param menu menú a consultar
     * @return pila de snapshots o {@code null} si no n'hi ha
     */
    private Deque<CompositeMenuSnapshot<?, ?, ?>> snapshotStackForRead(
            DynamicMenu<?, ?> menu) {
        MenuSnapshotStore store = storeForRead(menu);
        return store == null ? null : store.snapshotStack;
    }

    /**
     * Retorna la pila de snapshots del menú, creant-la si cal.
     *
     * @param menu menú a preparar
     * @return pila de snapshots del menú
     */
    private Deque<CompositeMenuSnapshot<?, ?, ?>> snapshotStackForWrite(
            DynamicMenu<?, ?> menu) {
        MenuSnapshotStore store = storeForWrite(menu);
        if (store.snapshotStack == null) {
            store.snapshotStack = new ArrayDeque<>();
        }
        return store.snapshotStack;
    }

    /**
     * Afegeix un snapshot compost a la pila del menú respectant el límit
     * configurat.
     *
     * <p>
     * Si el límit està actiu i la pila ja ha arribat al màxim, s'eliminen els
     * snapshots més antics necessaris abans d'afegir el nou snapshot.
     * </p>
     *
     * @param menu menú propietari de la pila
     * @param snapshot snapshot compost a desar
     */
    private void pushSnapshotWithConfiguredLimit(
            DynamicMenu<?, ?> menu,
            CompositeMenuSnapshot<?, ?, ?> snapshot) {

        Deque<CompositeMenuSnapshot<?, ?, ?>> stack = snapshotStackForWrite(menu);

        if (isSnapshotStackLimitEnabled()) {
            while (stack.size() >= snapshotStackLimit) {
                stack.removeLast();
            }
        }

        stack.push(snapshot);
    }

    /**
     * Retalla totes les piles existents al límit actual.
     *
     * <p>
     * Si el límit està desactivat, no es modifica cap pila.
     * </p>
     *
     * <p>
     * Si una pila supera el nou límit, s'eliminen els snapshots més antics i es
     * conserven els més recents.
     * </p>
     */
    private void trimAllStacksToCurrentLimit() {
        if (!isSnapshotStackLimitEnabled()) {
            return;
        }

        for (Map.Entry<DynamicMenu<?, ?>, MenuSnapshotStore> entry : storesByMenu.entrySet()) {
            MenuSnapshotStore store = entry.getValue();
            if (store.snapshotStack != null) {
                trimStackToCurrentLimit(store.snapshotStack);
            }
        }
    }

    /**
     * Retalla una pila al límit actual conservant els snapshots més recents.
     *
     * @param stack pila a retallar
     */
    private void trimStackToCurrentLimit(Deque<CompositeMenuSnapshot<?, ?, ?>> stack) {
        while (stack.size() > snapshotStackLimit) {
            stack.removeLast();
        }
    }

    /**
     * Extreu l'últim snapshot compost desat a la pila del menú indicat.
     *
     * <p>
     * Si la pila queda buida després de l'extracció, s'elimina també
     * l'entrada del menú del registre intern.
     * </p>
     *
     * @param menu menú propietari de la pila
     * @return snapshot compost extret de la pila
     * @throws IllegalStateException si el menú no té cap snapshot desat
     */
    private CompositeMenuSnapshot<?, ?, ?> popStoredSnapshot(DynamicMenu<?, ?> menu) {
        Deque<CompositeMenuSnapshot<?, ?, ?>> stack = snapshotStackForRead(menu);

        if (stack == null || stack.isEmpty()) {
            throw new IllegalStateException(
                    "No hi ha cap snapshot compost desat a la pila d'aquest menú");
        }

        CompositeMenuSnapshot<?, ?, ?> snapshot = stack.pop();
        removeSnapshotStackIfEmpty(menu, stack);
        return snapshot;
    }

    /**
     * Elimina el registre del menú si ha quedat buit.
     *
     * @param menu menú propietari del registre
     * @param registered mapa de snapshots registrats
     */
    private void removeRegisteredSnapshotsIfEmpty(
            DynamicMenu<?, ?> menu,
            Map<String, CompositeMenuSnapshot<?, ?, ?>> registered) {
        if (registered.isEmpty()) {
            MenuSnapshotStore store = storeForRead(menu);
            if (store != null) {
                store.registeredSnapshots = null;
                removeStoreIfEmpty(menu, store);
            }
        }
    }

    /**
     * Elimina la pila del menú si ha quedat buida.
     *
     * @param menu menú propietari de la pila
     * @param stack pila de snapshots compostos
     */
    private void removeSnapshotStackIfEmpty(
            DynamicMenu<?, ?> menu,
            Deque<CompositeMenuSnapshot<?, ?, ?>> stack) {
        if (stack.isEmpty()) {
            MenuSnapshotStore store = storeForRead(menu);
            if (store != null) {
                store.snapshotStack = null;
                removeStoreIfEmpty(menu, store);
            }
        }
    }

    /**
     * Valida el nom d'un snapshot compost.
     *
     * @param name nom a validar
     */
    private void validateSnapshotName(String name) {
        Objects.requireNonNull(name, "El nom del snapshot compost no pot ser nul");
        if (name.isBlank()) {
            throw new IllegalArgumentException(
                    "El nom del snapshot compost no pot estar buit");
        }
    }

    /**
     * Valida el límit de la pila de snapshots.
     *
     * @param snapshotStackLimit límit a validar
     * @throws IllegalArgumentException si el valor és 0 o inferior a
     *                                  {@link #UNLIMITED_SNAPSHOT_STACK_LIMIT}
     */
    private void validateSnapshotStackLimit(int snapshotStackLimit) {
        if (snapshotStackLimit == 0 || snapshotStackLimit < UNLIMITED_SNAPSHOT_STACK_LIMIT) {
            throw new IllegalArgumentException(
                    "El límit de la pila de snapshots compostos ha de ser positiu o "
                            + UNLIMITED_SNAPSHOT_STACK_LIMIT + " per indicar que no té límit");
        }
    }

    /**
     * Retorna el context del menú i comprova que existeixi.
     *
     * @param menu menú a consultar
     * @return context del menú
     * @throws IllegalStateException si el menú no té context
     */
    private <T, C> C ensureContextPresent(DynamicMenu<T, C> menu) {
        C context = menu.getContext();
        if (context == null) {
            throw new IllegalStateException(
                    "El menú no té context i no es pot gestionar el seu estat intern");
        }
        return context;
    }

    @SuppressWarnings("unchecked")
    private <T, C, S> CompositeMenuSnapshot<T, C, S> castSnapshot(CompositeMenuSnapshot<?, ?, ?> snapshot) {
        return (CompositeMenuSnapshot<T, C, S>) snapshot;
    }
}