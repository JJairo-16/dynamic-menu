package menu.editor.builders;

import java.util.Objects;

import menu.DynamicMenu;
import menu.editor.EditConfig;
import menu.editor.Range;
import menu.editor.core.RemoveFamily;
import menu.editor.helpers.OptionSelector;

/**
 * Builder fluent per a operacions d'eliminació.
 *
 * <p>Aquesta API actua com a façana pública sobre la família interna
 * {@link RemoveFamily}, de manera que els casos simples es poden resoldre
 * amb wrappers estàtics i els casos avançats amb una construcció fluïda.
 *
 * @param <T> tipus del context principal
 * @param <C> tipus del context secundari
 */
public final class RemoveBuilder<T, C> {
    private final DynamicMenu<T, C> menu;
    private OptionSelector<T, C> selector;
    private Range range = Range.all();
    private int limit = Integer.MAX_VALUE;
    private boolean reverse = false;

    public RemoveBuilder(DynamicMenu<T, C> menu) {
        this.menu = Objects.requireNonNull(menu, "El menú no pot ser nul");
    }

    /**
     * Defineix la condició de selecció.
     *
     * @param selector condició a aplicar
     * @return aquest builder
     */
    public RemoveBuilder<T, C> where(OptionSelector<T, C> selector) {
        this.selector = Objects.requireNonNull(selector, "La condició no pot ser nul·la");
        return this;
    }

    public RemoveBuilder<T, C> whereAny() {
        this.selector = (index, option) -> true;
        return this;
    }

    /**
     * Defineix el rang d'actuació.
     *
     * @param range rang a aplicar
     * @return aquest builder
     */
    public RemoveBuilder<T, C> range(Range range) {
        this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
        return this;
    }

    /**
     * Defineix el rang d'actuació.
     *
     * @param fromInclusive inici inclòs
     * @param toExclusive final exclòs
     * @return aquest builder
     */
    public RemoveBuilder<T, C> range(int fromInclusive, int toExclusive) {
        return range(Range.of(fromInclusive, toExclusive));
    }

    /**
     * Defineix el límit màxim d'eliminacions.
     *
     * @param limit límit a aplicar
     * @return aquest builder
     */
    public RemoveBuilder<T, C> limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Configura el recorregut en sentit invers.
     *
     * @return aquest builder
     */
    public RemoveBuilder<T, C> reverse() {
        this.reverse = true;
        return this;
    }

    /**
     * Defineix explícitament si s'ha de recórrer en sentit invers.
     *
     * @param reverse valor nou
     * @return aquest builder
     */
    public RemoveBuilder<T, C> reverse(boolean reverse) {
        this.reverse = reverse;
        return this;
    }

    /**
     * Limita l'operació a la primera coincidència.
     *
     * @return aquest builder
     */
    public RemoveBuilder<T, C> first() {
        this.limit = 1;
        this.reverse = false;
        return this;
    }

    /**
     * Limita l'operació a l'última coincidència.
     *
     * @return aquest builder
     */
    public RemoveBuilder<T, C> last() {
        this.limit = 1;
        this.reverse = true;
        return this;
    }

    /**
     * Defineix que s'han de considerar totes les coincidències.
     *
     * @return aquest builder
     */
    public RemoveBuilder<T, C> all() {
        this.limit = Integer.MAX_VALUE;
        return this;
    }

    /**
     * Aplica una configuració base.
     *
     * @param config configuració d'edició
     * @return aquest builder
     */
    public RemoveBuilder<T, C> config(EditConfig config) {
        Objects.requireNonNull(config, "La configuració no pot ser nul·la");
        this.range = config.range();
        this.limit = config.limit();
        this.reverse = config.reverse();
        return this;
    }

    /**
     * Construeix la configuració actual.
     *
     * @return configuració equivalent a l'estat actual del builder
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
        return RemoveFamily.removeIf(
                menu,
                Objects.requireNonNull(selector, "La condició no pot ser nul·la"),
                buildConfig());
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