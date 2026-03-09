package menu.snapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import menu.action.MenuAction;
import menu.action.MenuRuntimeAction;
import menu.action.SimpleMenuAction;
import menu.cache.MenuOptionSnapshotCache;
import menu.hook.MenuLoopHook;
import menu.model.MenuOption;

/**
 * Representa un set complet de menú reutilitzable o un snapshot restaurable.
 *
 * <p>
 * Un snapshot conté títol, context, opcions i hooks. També es pot utilitzar com
 * a base per crear menús fills derivats d'un altre snapshot.
 * </p>
 *
 * <p>
 * El context es desa com a <b>referència</b>. Restaurar un snapshot no desfà les
 * mutacions internes d'un objecte de context mutable.
 * </p>
 *
 * @param <T> tipus del valor de retorn del menú
 * @param <C> tipus del context del menú
 */
public class MenuSnapshot<T, C> {

    private String title;
    private C context;
    private final List<MenuOption<T, C>> options;

    /**
     * Índex de la primera aparició per etiqueta.
     *
     * <p>
     * Important: aquest mapa continua sent compatible amb duplicats; només
     * registra la primera posició trobada per cada label.
     * </p>
     */
    private final Map<String, Integer> firstIndexByLabel;

    private MenuLoopHook<T, C> beforeEachDisplay;
    private MenuLoopHook<T, C> beforeEachAction;
    private MenuLoopHook<T, C> afterEachAction;

    private final MenuOptionSnapshotCache<T, C> optionSnapshotCache = new MenuOptionSnapshotCache<>();

    /**
     * Crea un snapshot amb el títol indicat.
     *
     * @param title títol inicial del snapshot
     * @throws NullPointerException si {@code title} és {@code null}
     */
    public MenuSnapshot(String title) {
        this(title, null);
    }

    /**
     * Crea un snapshot amb títol i context.
     *
     * @param title   títol inicial del snapshot
     * @param context context inicial del snapshot; pot ser {@code null}
     * @throws NullPointerException si {@code title} és {@code null}
     */
    public MenuSnapshot(String title, C context) {
        this.title = Objects.requireNonNull(title, "El títol del snapshot no pot ser nul");
        this.context = context;
        this.options = new ArrayList<>();
        this.firstIndexByLabel = new HashMap<>();
    }

    /**
     * Crea un menú fill a partir del snapshot actual.
     *
     * <p>
     * El menú fill és una còpia independent del snapshot pare. Es pot donar
     * un nou títol o conservar el mateix.
     * </p>
     *
     * @param childTitle nou títol del menú fill; si és {@code null}, es conserva el
     *                   títol actual
     * @return nou snapshot independent derivat de l'actual
     */
    public MenuSnapshot<T, C> createChild(String childTitle) {
        MenuSnapshot<T, C> child = copy();
        if (childTitle != null) {
            child.setTitle(childTitle);
        }
        return child;
    }

    /**
     * Crea un snapshot fill mantenint el títol actual.
     *
     * @return nou snapshot independent derivat de l'actual
     */
    public MenuSnapshot<T, C> createChild() {
        return createChild(null);
    }

    /**
     * Crea un snapshot fill amb un context propi.
     *
     * @param childTitle   nou títol del snapshot fill; si és {@code null}, conserva
     *                     el títol actual
     * @param childContext context del snapshot fill; pot ser {@code null}
     * @return nou snapshot independent derivat de l'actual
     */
    public MenuSnapshot<T, C> createChild(String childTitle, C childContext) {
        MenuSnapshot<T, C> child = createChild(childTitle);
        child.setContext(childContext);
        return child;
    }

    /**
     * Crea un snapshot fill copiant el context actual.
     *
     * @param childTitle    nou títol del snapshot fill; si és {@code null}, conserva
     *                      el títol actual
     * @param contextCopier funció que copia el context actual
     * @return nou snapshot independent derivat de l'actual
     */
    public MenuSnapshot<T, C> createChildCopyingContext(
            String childTitle,
            Function<? super C, ? extends C> contextCopier) {

        Objects.requireNonNull(contextCopier, "La funció de còpia del context no pot ser nul·la");
        MenuSnapshot<T, C> child = createChild(childTitle);
        child.setContext(contextCopier.apply(this.context));
        return child;
    }

    /**
     * Retorna el títol del snapshot.
     *
     * @return títol actual
     */
    public String getTitle() {
        return title;
    }

    /**
     * Defineix el títol del snapshot.
     *
     * @param title nou títol
     * @return aquest mateix snapshot
     * @throws NullPointerException si {@code title} és {@code null}
     */
    public MenuSnapshot<T, C> setTitle(String title) {
        this.title = Objects.requireNonNull(title, "El títol del snapshot no pot ser nul");
        return this;
    }

    /**
     * Retorna el context del snapshot.
     *
     * @return context actual, que pot ser {@code null}
     */
    public C getContext() {
        return context;
    }

    /**
     * Defineix el context del snapshot.
     *
     * @param context nou context; pot ser {@code null}
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> setContext(C context) {
        this.context = context;
        return this;
    }

    /**
     * Afegeix una opció al final del snapshot.
     *
     * @param label  etiqueta de l'opció
     * @param action acció amb context
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOption(String label, MenuAction<T, C> action) {
        return addOptionToEnd(MenuOption.of(label, action));
    }

    /**
     * Afegeix una opció al final del snapshot.
     *
     * @param label  etiqueta de l'opció
     * @param action acció simple
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOption(String label, SimpleMenuAction<T> action) {
        return addOptionToEnd(MenuOption.of(label, action));
    }

    /**
     * Afegeix una opció al final del snapshot.
     *
     * @param label  etiqueta de l'opció
     * @param action acció amb accés al menú
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOption(String label, MenuRuntimeAction<T, C> action) {
        return addOptionToEnd(MenuOption.ofRuntime(label, action));
    }

    /**
     * Afegeix una opció en una posició concreta.
     *
     * @param index  posició d'inserció, entre 0 i la mida actual inclosa
     * @param label  etiqueta de l'opció
     * @param action acció amb context
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOptionAt(int index, String label, MenuAction<T, C> action) {
        return addOptionAt(index, MenuOption.of(label, action));
    }

    /**
     * Afegeix una opció en una posició concreta.
     *
     * @param index  posició d'inserció, entre 0 i la mida actual inclosa
     * @param label  etiqueta de l'opció
     * @param action acció simple
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOptionAt(int index, String label, SimpleMenuAction<T> action) {
        return addOptionAt(index, MenuOption.of(label, action));
    }

    /**
     * Afegeix una opció en una posició concreta.
     *
     * @param index  posició d'inserció, entre 0 i la mida actual inclosa
     * @param label  etiqueta de l'opció
     * @param action acció amb accés al menú
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOptionAt(int index, String label, MenuRuntimeAction<T, C> action) {
        return addOptionAt(index, MenuOption.ofRuntime(label, action));
    }

    /**
     * Afegeix una opció abans d'una altra opció existent.
     *
     * @param referenceLabel etiqueta de referència
     * @param label          etiqueta de la nova opció
     * @param action         acció amb context
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOptionBefore(String referenceLabel, String label, MenuAction<T, C> action) {
        return addOptionAt(requireExistingOptionIndex(referenceLabel), label, action);
    }

    /**
     * Afegeix una opció abans d'una altra opció existent.
     *
     * @param referenceLabel etiqueta de referència
     * @param label          etiqueta de la nova opció
     * @param action         acció simple
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOptionBefore(String referenceLabel, String label, SimpleMenuAction<T> action) {
        return addOptionAt(requireExistingOptionIndex(referenceLabel), label, action);
    }

    /**
     * Afegeix una opció abans d'una altra opció existent.
     *
     * @param referenceLabel etiqueta de referència
     * @param label          etiqueta de la nova opció
     * @param action         acció amb accés al menú
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOptionBefore(String referenceLabel, String label, MenuRuntimeAction<T, C> action) {
        return addOptionAt(requireExistingOptionIndex(referenceLabel), label, action);
    }

    /**
     * Afegeix una opció després d'una altra opció existent.
     *
     * @param referenceLabel etiqueta de referència
     * @param label          etiqueta de la nova opció
     * @param action         acció amb context
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOptionAfter(String referenceLabel, String label, MenuAction<T, C> action) {
        return addOptionAt(requireExistingOptionIndex(referenceLabel) + 1, label, action);
    }

    /**
     * Afegeix una opció després d'una altra opció existent.
     *
     * @param referenceLabel etiqueta de referència
     * @param label          etiqueta de la nova opció
     * @param action         acció simple
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOptionAfter(String referenceLabel, String label, SimpleMenuAction<T> action) {
        return addOptionAt(requireExistingOptionIndex(referenceLabel) + 1, label, action);
    }

    /**
     * Afegeix una opció després d'una altra opció existent.
     *
     * @param referenceLabel etiqueta de referència
     * @param label          etiqueta de la nova opció
     * @param action         acció amb accés al menú
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> addOptionAfter(String referenceLabel, String label, MenuRuntimeAction<T, C> action) {
        return addOptionAt(requireExistingOptionIndex(referenceLabel) + 1, label, action);
    }

    /**
     * Indica si el snapshot ja conté una opció amb l'etiqueta indicada.
     *
     * @param label etiqueta a cercar
     * @return {@code true} si existeix una opció amb aquesta etiqueta
     */
    public boolean hasOption(String label) {
        Objects.requireNonNull(label, "L'etiqueta de cerca no pot ser nul·la");
        return firstIndexByLabel.containsKey(label);
    }

    /**
     * Retorna la posició de la primera opció amb l'etiqueta indicada.
     *
     * @param label etiqueta a cercar
     * @return índex de la primera coincidència, o {@code -1} si no existeix
     */
    public int indexOfOption(String label) {
        Objects.requireNonNull(label, "L'etiqueta de cerca no pot ser nul·la");
        return firstIndexByLabel.getOrDefault(label, -1);
    }

    /**
     * Mou una opció a l'inici.
     *
     * @param index índex actual de l'opció
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> moveOptionToStart(int index) {
        return moveOptionToIndex(index, 0);
    }

    /**
     * Mou una opció al final.
     *
     * @param index índex actual de l'opció
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> moveOptionToEnd(int index) {
        return moveOptionToIndex(index, options.size() - 1);
    }

    /**
     * Mou una opció a una posició concreta.
     *
     * @param fromIndex índex actual
     * @param toIndex   índex de destinació
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> moveOptionToIndex(int fromIndex, int toIndex) {
        validateExistingIndex(fromIndex);
        validateExistingIndex(toIndex);

        if (fromIndex == toIndex) {
            return this;
        }

        MenuOption<T, C> option = options.remove(fromIndex);
        int adjustedTargetIndex = fromIndex < toIndex ? toIndex - 1 : toIndex;
        options.add(adjustedTargetIndex, option);

        rebuildLabelIndex();
        invalidateOptionSnapshotCache();
        return this;
    }

    /**
     * Mou una opció abans d'una altra.
     *
     * @param movingIndex    índex de l'opció a moure
     * @param referenceIndex índex de l'opció de referència
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> moveOptionBefore(int movingIndex, int referenceIndex) {
        validateExistingIndex(movingIndex);
        validateExistingIndex(referenceIndex);

        if (movingIndex == referenceIndex) {
            return this;
        }

        MenuOption<T, C> option = options.remove(movingIndex);
        int adjustedReferenceIndex = movingIndex < referenceIndex ? referenceIndex - 1 : referenceIndex;
        options.add(adjustedReferenceIndex, option);

        rebuildLabelIndex();
        invalidateOptionSnapshotCache();
        return this;
    }

    /**
     * Mou una opció després d'una altra.
     *
     * @param movingIndex    índex de l'opció a moure
     * @param referenceIndex índex de l'opció de referència
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> moveOptionAfter(int movingIndex, int referenceIndex) {
        validateExistingIndex(movingIndex);
        validateExistingIndex(referenceIndex);

        if (movingIndex == referenceIndex) {
            return this;
        }

        MenuOption<T, C> option = options.remove(movingIndex);
        int adjustedReferenceIndex = movingIndex < referenceIndex ? referenceIndex : referenceIndex + 1;
        options.add(adjustedReferenceIndex, option);

        rebuildLabelIndex();
        invalidateOptionSnapshotCache();
        return this;
    }

    /**
     * Elimina la primera opció amb l'etiqueta indicada.
     *
     * @param label etiqueta de l'opció a eliminar
     * @return {@code true} si s'ha eliminat una opció; {@code false} altrament
     */
    public boolean removeOption(String label) {
        int index = indexOfOption(label);
        if (index < 0) {
            return false;
        }

        removeOptionAt(index);
        return true;
    }

    /**
     * Elimina una opció pel seu índex.
     *
     * @param index índex de l'opció a eliminar
     * @return l'opció eliminada
     */
    public MenuOption<T, C> removeOptionAt(int index) {
        validateExistingIndex(index);

        String removedLabel = options.get(index).label();
        boolean removedWasFirst = Objects.equals(firstIndexByLabel.get(removedLabel), index);

        MenuOption<T, C> removed = options.remove(index);
        onRemove(index, removedWasFirst);

        invalidateOptionSnapshotCache();
        return removed;
    }

    /**
     * Elimina totes les opcions amb l'etiqueta indicada.
     *
     * @param label etiqueta a eliminar
     * @return nombre d'opcions eliminades
     */
    public int removeAllOptions(String label) {
        Objects.requireNonNull(label, "L'etiqueta de cerca no pot ser nul·la");

        int initialSize = options.size();
        options.removeIf(o -> label.equals(o.label()));
        int removedCount = initialSize - options.size();

        if (removedCount > 0) {
            rebuildLabelIndex();
            invalidateOptionSnapshotCache();
        }

        return removedCount;
    }

    /**
     * Elimina totes les opcions del snapshot.
     *
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> clearOptions() {
        if (!options.isEmpty()) {
            options.clear();
            firstIndexByLabel.clear();
            invalidateOptionSnapshotCache();
        }
        return this;
    }

    /**
     * Retorna el nombre d'opcions del snapshot.
     *
     * @return nombre d'opcions
     */
    public int optionCount() {
        return options.size();
    }

    /**
     * Retorna una instantània immutable de les opcions del snapshot.
     *
     * @return còpia immutable de les opcions
     */
    public List<MenuOption<T, C>> getOptionSnapshot() {
        return optionSnapshotCache.get(options);
    }

    /**
     * Defineix el hook executat abans de mostrar les opcions.
     *
     * @param hook hook a assignar; pot ser {@code null}
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> beforeEachDisplay(MenuLoopHook<T, C> hook) {
        this.beforeEachDisplay = hook;
        return this;
    }

    /**
     * Defineix el hook executat abans de l'acció triada.
     *
     * @param hook hook a assignar; pot ser {@code null}
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> beforeEachAction(MenuLoopHook<T, C> hook) {
        this.beforeEachAction = hook;
        return this;
    }

    /**
     * Defineix el hook executat després de l'acció triada.
     *
     * @param hook hook a assignar; pot ser {@code null}
     * @return aquest mateix snapshot
     */
    public MenuSnapshot<T, C> afterEachAction(MenuLoopHook<T, C> hook) {
        this.afterEachAction = hook;
        return this;
    }

    /**
     * Retorna el hook anterior a la visualització.
     *
     * @return hook actual, o {@code null}
     */
    public MenuLoopHook<T, C> getBeforeEachDisplay() {
        return beforeEachDisplay;
    }

    /**
     * Retorna el hook anterior a l'acció.
     *
     * @return hook actual, o {@code null}
     */
    public MenuLoopHook<T, C> getBeforeEachAction() {
        return beforeEachAction;
    }

    /**
     * Retorna el hook posterior a l'acció.
     *
     * @return hook actual, o {@code null}
     */
    public MenuLoopHook<T, C> getAfterEachAction() {
        return afterEachAction;
    }

    /**
     * Crea una còpia independent del snapshot actual.
     *
     * @return còpia del snapshot
     */
    public MenuSnapshot<T, C> copy() {
        MenuSnapshot<T, C> copy = new MenuSnapshot<>(title, context);
        copy.options.addAll(this.options);
        copy.beforeEachDisplay = this.beforeEachDisplay;
        copy.beforeEachAction = this.beforeEachAction;
        copy.afterEachAction = this.afterEachAction;
        copy.rebuildLabelIndex();
        return copy;
    }

    private MenuSnapshot<T, C> addOptionAt(int index, MenuOption<T, C> option) {
        Objects.requireNonNull(option, "L'opció a inserir no pot ser nul·la");
        validateInsertIndex(index);

        if (index == options.size()) {
            return addOptionToEnd(option);
        }

        options.add(index, option);
        onInsert(index, option.label());

        invalidateOptionSnapshotCache();
        return this;
    }

    private MenuSnapshot<T, C> addOptionToEnd(MenuOption<T, C> option) {
        Objects.requireNonNull(option, "L'opció a inserir no pot ser nul·la");

        int newIndex = options.size();
        options.add(option);

        /*
         * Si la label no existia, aquesta nova posició és la primera.
         * Si ja existia, el mapa no canvia perquè només guarda la primera aparició.
         */
        firstIndexByLabel.putIfAbsent(option.label(), newIndex);

        invalidateOptionSnapshotCache();
        return this;
    }

    /**
     * Actualitza l'índex auxiliar després d'una inserció enmig de la llista.
     *
     * <p>
     * Compatible amb duplicats: només es manté la primera aparició per label.
     * </p>
     */
    private void onInsert(int index, String insertedLabel) {
        for (Map.Entry<String, Integer> entry : firstIndexByLabel.entrySet()) {
            if (entry.getValue() >= index) {
                entry.setValue(entry.getValue() + 1);
            }
        }

        Integer currentFirst = firstIndexByLabel.get(insertedLabel);
        if (currentFirst == null || index < currentFirst) {
            firstIndexByLabel.put(insertedLabel, index);
        }
    }

    /**
     * Actualitza l'índex auxiliar després d'una eliminació.
     *
     * <p>
     * Si s'elimina la primera aparició d'una label, es reconstrueix completament
     * per garantir coherència amb duplicats.
     * </p>
     */
    private void onRemove(int removedIndex, boolean removedWasFirst) {
        if (removedWasFirst) {
            rebuildLabelIndex();
            return;
        }

        for (Map.Entry<String, Integer> entry : firstIndexByLabel.entrySet()) {
            if (entry.getValue() > removedIndex) {
                entry.setValue(entry.getValue() - 1);
            }
        }
    }

    private void validateInsertIndex(int index) {
        if (index < 0 || index > options.size()) {
            throw new IndexOutOfBoundsException(
                    "L'índex d'inserció " + index + " no és vàlid; ha d'estar entre 0 i " + options.size());
        }
    }

    private void validateExistingIndex(int index) {
        if (index < 0 || index >= options.size()) {
            throw new IndexOutOfBoundsException(
                    "L'índex " + index + " no és vàlid; ha d'estar entre 0 i " + (options.size() - 1));
        }
    }

    private int requireExistingOptionIndex(String label) {
        int index = indexOfOption(label);
        if (index < 0) {
            throw new IllegalArgumentException(
                    "No existeix cap opció amb l'etiqueta indicada: " + label);
        }
        return index;
    }

    private void invalidateOptionSnapshotCache() {
        optionSnapshotCache.invalidate();
    }

    private void rebuildLabelIndex() {
        firstIndexByLabel.clear();

        for (int i = 0; i < options.size(); i++) {
            String label = options.get(i).label();
            firstIndexByLabel.putIfAbsent(label, i);
        }
    }
}
