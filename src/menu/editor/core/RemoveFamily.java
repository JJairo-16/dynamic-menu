package menu.editor.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import menu.DynamicMenu;
import menu.editor.EditConfig;
import menu.editor.Range;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;
import menu.snapshot.MenuSnapshot;

import static menu.editor.core.MenuEditorSupport.*;

/**
 * Família d'operacions d'eliminació sobre un {@link DynamicMenu}.
 *
 * <p>Aquesta classe combina dos estils d'ús:
 * <ul>
 *     <li><b>Mètodes estàtics</b> per als casos simples i de conveniència.</li>
 *     <li><b>API fluïda</b> per construir operacions d'eliminació més avançades
 *     de manera escalable i expressiva.</li>
 * </ul>
 *
 * <p>La lògica real d'eliminació es centralitza en un únic nucli intern per evitar
 * duplicació i facilitar l'evolució del projecte.
 */
public final class RemoveFamily {
    private RemoveFamily() {
    }

    // -------------------------------------------------------------------------
    // Fluent entry points
    // -------------------------------------------------------------------------

    /**
     * Inicia una operació fluïda d'eliminació sobre el menú indicat.
     *
     * @param menu menú objectiu
     * @param <T>  tipus del context principal
     * @param <C>  tipus del context secundari
     * @return operador fluent d'eliminació
     */
    public static <T, C> RemoveOperation<T, C> remove(DynamicMenu<T, C> menu) {
        return new RemoveOperation<>(menu);
    }

    /**
     * Inicia una operació fluïda d'eliminació amb una condició inicial.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return operador fluent d'eliminació
     */
    public static <T, C> RemoveOperation<T, C> remove(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return remove(menu).where(selector);
    }

    /**
     * Inicia una operació fluïda d'eliminació per etiqueta exacta.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta a buscar
     * @param <T>   tipus del context principal
     * @param <C>   tipus del context secundari
     * @return operador fluent d'eliminació
     */
    public static <T, C> RemoveOperation<T, C> removeLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return remove(menu).label(label);
    }

    // -------------------------------------------------------------------------
    // Remove if - compatibilitat i conveniència
    // -------------------------------------------------------------------------

    /**
     * Elimina totes les opcions que compleixen la condició.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return remove(menu, selector).execute();
    }

    /**
     * Elimina totes les opcions que compleixen la condició dins d'un rang.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang a aplicar
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return remove(menu, selector)
                .range(range)
                .execute();
    }

    /**
     * Elimina opcions que compleixen la condició dins d'un rang i amb un límit.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang a aplicar
     * @param limit    límit màxim d'eliminacions
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range,
            int limit) {

        return remove(menu, selector)
                .range(range)
                .limit(limit)
                .execute();
    }

    /**
     * Elimina opcions segons una configuració completa.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param config   configuració d'edició
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            EditConfig config) {

        Objects.requireNonNull(menu, "El menú no pot ser nul");
        Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        return executeRemove(menu, selector, config);
    }

    // -------------------------------------------------------------------------
    // Remove all if reverse
    // -------------------------------------------------------------------------

    /**
     * Elimina totes les opcions coincidents recorrent en sentit invers.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return remove(menu, selector)
                .reverse()
                .execute();
    }

    /**
     * Elimina totes les opcions coincidents dins d'un rang, recorrent en sentit invers.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang a aplicar
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return remove(menu, selector)
                .range(range)
                .reverse()
                .execute();
    }

    /**
     * Elimina opcions coincidents dins d'un rang, amb límit i recorrent en sentit invers.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang a aplicar
     * @param limit    límit màxim d'eliminacions
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range,
            int limit) {

        return remove(menu, selector)
                .range(range)
                .limit(limit)
                .reverse()
                .execute();
    }

    /**
     * Elimina opcions recorrent en sentit invers a partir d'una configuració existent.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param config   configuració base
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeAllIfReverse(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            EditConfig config) {

        Objects.requireNonNull(config, "La configuració no pot ser nul·la");

        return remove(menu, selector)
                .config(config)
                .reverse()
                .execute();
    }

    // -------------------------------------------------------------------------
    // Conveniències específiques
    // -------------------------------------------------------------------------

    /**
     * Elimina la primera opció que compleix la condició.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return {@code true} si s'ha eliminat una opció
     */
    public static <T, C> boolean removeFirstIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return remove(menu, selector)
                .first()
                .executeAny();
    }

    /**
     * Elimina l'última opció que compleix la condició.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return {@code true} si s'ha eliminat una opció
     */
    public static <T, C> boolean removeLastIf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return remove(menu, selector)
                .last()
                .executeAny();
    }

    /**
     * Elimina la primera opció amb l'etiqueta exacta indicada.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus del context principal
     * @param <C>   tipus del context secundari
     * @return {@code true} si s'ha eliminat una opció
     */
    public static <T, C> boolean removeFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return removeLabel(menu, label)
                .first()
                .executeAny();
    }

    /**
     * Elimina l'última opció amb l'etiqueta exacta indicada.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus del context principal
     * @param <C>   tipus del context secundari
     * @return {@code true} si s'ha eliminat una opció
     */
    public static <T, C> boolean removeLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return removeLabel(menu, label)
                .last()
                .executeAny();
    }

    /**
     * Elimina totes les opcions amb l'etiqueta exacta indicada.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus del context principal
     * @param <C>   tipus del context secundari
     * @return nombre d'elements eliminats
     */
    public static <T, C> int removeAllLabels(
            DynamicMenu<T, C> menu,
            String label) {

        return removeLabel(menu, label).execute();
    }

    // -------------------------------------------------------------------------
    // Nucli intern únic
    // -------------------------------------------------------------------------

    /**
     * Executa la lògica real d'eliminació.
     *
     * <p>Aquest mètode centralitza el procés complet:
     * <ol>
     *     <li>captura l'estat actual del menú,</li>
     *     <li>valida el rang,</li>
     *     <li>calcula els índexs a eliminar,</li>
     *     <li>reconstrueix el snapshot,</li>
     *     <li>restaura el menú.</li>
     * </ol>
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param config   configuració d'edició
     * @param <T>      tipus del context principal
     * @param <C>      tipus del context secundari
     * @return nombre d'elements eliminats
     */
    private static <T, C> int executeRemove(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            EditConfig config) {

        MenuSnapshot<T, C> snapshot = menu.createSnapshot();
        List<MenuOption<T, C>> options = new ArrayList<>(snapshot.getOptionSnapshot());

        validateRange(config.range(), options.size());

        if (options.isEmpty() || config.limit() == 0) {
            return 0;
        }

        Range effectiveRange = config.range().clamp(options.size());
        Set<Integer> toRemove = QueryFamily.collectMatchingIndexes(
                options,
                selector,
                effectiveRange,
                config.limit(),
                config.reverse());

        if (toRemove.isEmpty()) {
            return 0;
        }

        List<MenuOption<T, C>> rebuilt = new ArrayList<>(options.size() - toRemove.size());
        for (int i = 0; i < options.size(); i++) {
            if (!toRemove.contains(i)) {
                rebuilt.add(options.get(i));
            }
        }

        rebuildSnapshot(snapshot, rebuilt);
        menu.restoreSnapshot(snapshot);
        return toRemove.size();
    }

    // -------------------------------------------------------------------------
    // Fluent operation
    // -------------------------------------------------------------------------

    /**
     * Operació fluïda d'eliminació.
     *
     * <p>Permet construir una eliminació de manera declarativa i escalable:
     *
     * <pre>{@code
     * RemoveFamily.remove(menu)
     *         .label("Sortir")
     *         .last()
     *         .executeAny();
     * }</pre>
     *
     * @param <T> tipus del context principal
     * @param <C> tipus del context secundari
     */
    public static final class RemoveOperation<T, C> {
        private final DynamicMenu<T, C> menu;
        private OptionSelector<T, C> selector = alwaysTrueSelector();
        private Range range = Range.all();
        private int limit = Integer.MAX_VALUE;
        private boolean reverse = false;

        private RemoveOperation(DynamicMenu<T, C> menu) {
            this.menu = Objects.requireNonNull(menu, "El menú no pot ser nul");
        }

        /**
         * Defineix la condició de selecció.
         *
         * @param selector condició a aplicar
         * @return aquesta operació
         */
        public RemoveOperation<T, C> where(OptionSelector<T, C> selector) {
            this.selector = Objects.requireNonNull(selector, "La condició no pot ser nul·la");
            return this;
        }

        /**
         * Defineix una selecció per etiqueta exacta.
         *
         * @param label etiqueta exacta
         * @return aquesta operació
         */
        public RemoveOperation<T, C> label(String label) {
            return where(exactLabelSelector(label));
        }

        /**
         * Defineix el rang d'actuació.
         *
         * @param range rang a aplicar
         * @return aquesta operació
         */
        public RemoveOperation<T, C> range(Range range) {
            this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
            return this;
        }

        /**
         * Defineix el rang d'actuació.
         *
         * @param fromInclusive inici inclòs
         * @param toExclusive   final exclòs
         * @return aquesta operació
         */
        public RemoveOperation<T, C> range(int fromInclusive, int toExclusive) {
            return range(Range.of(fromInclusive, toExclusive));
        }

        /**
         * Defineix el límit màxim d'eliminacions.
         *
         * @param limit límit a aplicar
         * @return aquesta operació
         */
        public RemoveOperation<T, C> limit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Configura el recorregut en sentit invers.
         *
         * @return aquesta operació
         */
        public RemoveOperation<T, C> reverse() {
            this.reverse = true;
            return this;
        }

        /**
         * Defineix explícitament si s'ha de recórrer en sentit invers.
         *
         * @param reverse valor nou
         * @return aquesta operació
         */
        public RemoveOperation<T, C> reverse(boolean reverse) {
            this.reverse = reverse;
            return this;
        }

        /**
         * Limita l'operació a la primera coincidència.
         *
         * @return aquesta operació
         */
        public RemoveOperation<T, C> first() {
            this.limit = 1;
            this.reverse = false;
            return this;
        }

        /**
         * Limita l'operació a l'última coincidència.
         *
         * @return aquesta operació
         */
        public RemoveOperation<T, C> last() {
            this.limit = 1;
            this.reverse = true;
            return this;
        }

        /**
         * Defineix que s'han de considerar totes les coincidències.
         *
         * @return aquesta operació
         */
        public RemoveOperation<T, C> all() {
            this.limit = Integer.MAX_VALUE;
            return this;
        }

        /**
         * Aplica una configuració base sobre l'operació actual.
         *
         * @param config configuració d'edició
         * @return aquesta operació
         */
        public RemoveOperation<T, C> config(EditConfig config) {
            Objects.requireNonNull(config, "La configuració no pot ser nul·la");
            this.range = config.range();
            this.limit = config.limit();
            this.reverse = config.reverse();
            return this;
        }

        /**
         * Construeix la configuració actual.
         *
         * @return configuració equivalent a l'estat de l'operació
         */
        public EditConfig buildConfig() {
            return EditConfig.builder()
                    .range(range)
                    .limit(limit)
                    .reverse(reverse)
                    .build();
        }

        /**
         * Executa l'operació i retorna el nombre d'elements eliminats.
         *
         * @return nombre d'elements eliminats
         */
        public int execute() {
            return RemoveFamily.removeIf(menu, selector, buildConfig());
        }

        /**
         * Executa l'operació i indica si s'ha eliminat almenys un element.
         *
         * @return {@code true} si s'ha eliminat almenys una opció
         */
        public boolean executeAny() {
            return execute() > 0;
        }
    }
}