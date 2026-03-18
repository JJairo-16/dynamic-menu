package menu.editor;

import java.util.List;
import java.util.Comparator;
import java.util.Random;

import menu.DynamicMenu;
import menu.editor.builders.*;
import menu.editor.core.*;
import menu.editor.helpers.OptionSelector;
import menu.model.MenuOption;

/**
 * Utilitats avançades d'edició i consulta per a {@link DynamicMenu}.
 *
 * <p>
 * Aquesta classe actua com a façana pública del mòdul d'edició. La idea és
 * mantenir una superfície estàtica petita i fàcil de descobrir, reservant la
 * configuració rica i les operacions mutables complexes per als builders.
 *
 * <p>
 * Criteri de disseny actual:
 *
 * <ul>
 * <li>Les operacions d'edició ({@code replace}, {@code remove},
 * {@code sort}, {@code shuffle}) s'exposen mitjançant builders.</li>
 * <li>Les utilitats de selecció i consulta lleugera poden continuar sent
 * estàtiques perquè són útils com a helpers de lectura i introspecció.</li>
 * <li>S'eviten les grans famílies de sobrecàrregues estàtiques quan ja hi
 * ha una API fluïda equivalent.</li>
 * </ul>
 *
 * <p>
 * Això redueix duplicació, evita una classe amb massa variants i manté la
 * descoberta de l'API més clara: la mutació passa pels builders i la lectura
 * ràpida es pot fer amb helpers directes o amb {@link QueryBuilder}.
 */
public final class MenuEditor {

    /** Evita la instanciació. */
    private MenuEditor() {
        throw new AssertionError("No es pot instanciar MenuEditor");
    }

    // -------------------------------------------------------------------------
    // Selectors i predicats reutilitzables
    // -------------------------------------------------------------------------

    /**
     * Retorna un selector que mai no coincideix.
     *
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return selector sempre fals
     */
    public static <T, C> OptionSelector<T, C> alwaysFalseSelector() {
        return MenuEditorSupport.alwaysFalseSelector();
    }

    /**
     * Retorna un selector que sempre coincideix.
     *
     * @param <T> tipus de retorn del menú
     * @param <C> tipus del context del menú
     * @return selector sempre cert
     */
    public static <T, C> OptionSelector<T, C> alwaysTrueSelector() {
        return MenuEditorSupport.alwaysTrueSelector();
    }

    /**
     * Retorna sempre {@code true}.
     *
     * <p>
     * Pot ser útil com a referència de mètode quan es necessita un predicat
     * trivial o en proves ràpides.
     *
     * @param index  índex actual
     * @param option opció actual
     * @param <T>    tipus de retorn del menú
     * @param <C>    tipus del context del menú
     * @return sempre {@code true}
     */
    @SuppressWarnings("SameReturnValue")
    public static <T, C> boolean alwaysTrue(int index, MenuOption<T, C> option) {
        return true;
    }

    /**
     * Retorna sempre {@code false}.
     *
     * <p>
     * Pot ser útil com a referència de mètode quan es necessita un predicat
     * nul o en proves ràpides.
     *
     * @param index  índex actual
     * @param option opció actual
     * @param <T>    tipus de retorn del menú
     * @param <C>    tipus del context del menú
     * @return sempre {@code false}
     */
    @SuppressWarnings("SameReturnValue")
    public static <T, C> boolean alwaysFalse(int index, MenuOption<T, C> option) {
        return false;
    }

    // -------------------------------------------------------------------------
    // Replace - API estàtica pública
    // -------------------------------------------------------------------------

    public static <T, C> int replace(DynamicMenu<T, C> menu, OptionSelector<T, C> selector, String newLabel) {
        return replace(menu, selector)
                .label(newLabel)
                .execute();
    }

    // -------------------------------------------------------------------------
    // Replace - API fluïda pública
    // -------------------------------------------------------------------------

    /**
     * Inicia una operació fluïda de reemplaç.
     *
     * <p>
     * El builder resultant permet especificar selectors, rangs i altres
     * opcions abans d'executar l'operació final.
     *
     * @param menu menú objectiu
     * @param <T>  tipus de retorn del menú
     * @param <C>  tipus del context del menú
     * @return operador fluent de reemplaç
     */
    public static <T, C> ReplaceBuilder<T, C> replace(DynamicMenu<T, C> menu) {
        return new ReplaceBuilder<>(menu);
    }

    /**
     * Inicia una operació fluïda de reemplaç amb selector inicial.
     *
     * @param menu     menú objectiu
     * @param selector condició inicial
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return operador fluent de reemplaç
     */
    public static <T, C> ReplaceBuilder<T, C> replace(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return replace(menu).where(selector);
    }

    // -------------------------------------------------------------------------
    // Remove - API fluïda pública
    // -------------------------------------------------------------------------

    /**
     * Inicia una operació fluïda d'eliminació.
     *
     * <p>
     * Les variants estàtiques d'eliminació s'han eliminat deliberadament per
     * evitar duplicar la capacitat dels builders. La configuració avançada s'ha
     * de canalitzar des de {@link RemoveBuilder}.
     *
     * @param menu menú objectiu
     * @param <T>  tipus de retorn del menú
     * @param <C>  tipus del context del menú
     * @return operador fluent d'eliminació
     */
    public static <T, C> RemoveBuilder<T, C> remove(DynamicMenu<T, C> menu) {
        return new RemoveBuilder<>(menu);
    }

    /**
     * Inicia una operació fluïda d'eliminació amb selector inicial.
     *
     * @param menu     menú objectiu
     * @param selector condició inicial
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return operador fluent d'eliminació
     */
    public static <T, C> RemoveBuilder<T, C> remove(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return remove(menu).where(selector);
    }

    // -------------------------------------------------------------------------
    // Sort - API fluïda pública
    // -------------------------------------------------------------------------

    /**
     * Inicia una operació fluïda d'ordenació.
     *
     * <p>
     * La configuració d'ordenació pot incloure comparador, rang i opcions de
     * pinning mitjançant el builder corresponent. Les antigues sobrecàrregues
     * estàtiques s'han retirat per reduir soroll de l'API.
     *
     * @param menu menú objectiu
     * @param <T>  tipus de retorn del menú
     * @param <C>  tipus del context del menú
     * @return operador fluent d'ordenació
     */
    public static <T, C> SortBuilder<T, C> sort(DynamicMenu<T, C> menu) {
        return new SortBuilder<>(menu);
    }

    /**
     * Inicia una operació fluïda d'ordenació amb comparador inicial.
     *
     * @param menu       menú objectiu
     * @param comparator comparador inicial
     * @param <T>        tipus de retorn del menú
     * @param <C>        tipus del context del menú
     * @return operador fluent d'ordenació
     */
    public static <T, C> SortBuilder<T, C> sort(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator) {

        return sort(menu).comparator(comparator);
    }

    // -------------------------------------------------------------------------
    // Shuffle - API fluïda pública
    // -------------------------------------------------------------------------

    /**
     * Inicia una operació fluïda de barreja.
     *
     * @param menu menú objectiu
     * @param <T>  tipus de retorn del menú
     * @param <C>  tipus del context del menú
     * @return operador fluent de barreja
     */
    public static <T, C> ShuffleBuilder<T, C> shuffle(DynamicMenu<T, C> menu) {
        return new ShuffleBuilder<>(menu);
    }

    /**
     * Inicia una operació fluïda de barreja amb un random inicial.
     *
     * @param menu   menú objectiu
     * @param random random inicial
     * @param <T>    tipus de retorn del menú
     * @param <C>    tipus del context del menú
     * @return operador fluent de barreja
     */
    public static <T, C> ShuffleBuilder<T, C> shuffle(
            DynamicMenu<T, C> menu,
            Random random) {

        return shuffle(menu).random(random);
    }

    // -------------------------------------------------------------------------
    // Sorting - shortcuts estàtics còmodes
    // -------------------------------------------------------------------------

    /**
     * Ordena totes les opcions per label amb l'ordre natural.
     *
     * <p>
     * Aquest shortcut es manté perquè és una operació molt comuna i evita
     * haver d'obrir sempre el builder quan no cal configuració addicional.
     *
     * @param menu menú objectiu
     * @param <T>  tipus de retorn del menú
     * @param <C>  tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> sortByLabel(DynamicMenu<T, C> menu) {
        return SortFamily.sortByLabel(menu);
    }

    /**
     * Ordena totes les opcions per label amb un comparador concret.
     *
     * @param menu       menú objectiu
     * @param comparator comparador a aplicar
     * @param <T>        tipus de retorn del menú
     * @param <C>        tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator) {

        return SortFamily.sortByLabel(menu, comparator);
    }

    /**
     * Ordena les opcions per label dins d'un rang.
     *
     * @param menu  menú objectiu
     * @param range rang a ordenar
     * @param <T>   tipus de retorn del menú
     * @param <C>   tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Range range) {

        return SortFamily.sortByLabel(menu, range);
    }

    /**
     * Ordena les opcions per label dins d'un rang amb un comparador concret.
     *
     * @param menu       menú objectiu
     * @param comparator comparador a aplicar
     * @param range      rang a ordenar
     * @param <T>        tipus de retorn del menú
     * @param <C>        tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> sortByLabel(
            DynamicMenu<T, C> menu,
            Comparator<MenuOption<T, C>> comparator,
            Range range) {

        return SortFamily.sortByLabel(menu, comparator, range);
    }

    // -------------------------------------------------------------------------
    // Shuffle - shortcuts estàtics còmodes
    // -------------------------------------------------------------------------

    /**
     * Barreja totes les opcions utilitzant un {@link Random} nou.
     *
     * @param menu menú objectiu
     * @param <T>  tipus de retorn del menú
     * @param <C>  tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffleOptions(DynamicMenu<T, C> menu) {
        return ShuffleFamily.shuffle(menu);
    }

    /**
     * Barreja totes les opcions utilitzant el {@link Random} indicat.
     *
     * @param menu   menú objectiu
     * @param random generador aleatori
     * @param <T>    tipus de retorn del menú
     * @param <C>    tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffleOptions(
            DynamicMenu<T, C> menu,
            Random random) {

        return ShuffleFamily.shuffle(menu, random);
    }

    /**
     * Barreja les opcions dins d'un rang amb un {@link Random} nou.
     *
     * @param menu  menú objectiu
     * @param range rang a barrejar
     * @param <T>   tipus de retorn del menú
     * @param <C>   tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffleOptions(
            DynamicMenu<T, C> menu,
            Range range) {

        return ShuffleFamily.shuffle(menu, range);
    }

    /**
     * Barreja les opcions dins d'un rang utilitzant el {@link Random} indicat.
     *
     * @param menu   menú objectiu
     * @param random generador aleatori
     * @param range  rang a barrejar
     * @param <T>    tipus de retorn del menú
     * @param <C>    tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffleOptions(
            DynamicMenu<T, C> menu,
            Random random,
            Range range) {

        return ShuffleFamily.shuffle(menu, random, range);
    }

    /**
     * Barreja totes les opcions a partir d'una llavor fixa.
     *
     * @param menu menú objectiu
     * @param seed llavor aleatòria
     * @param <T>  tipus de retorn del menú
     * @param <C>  tipus del context del menú
     * @return menú modificat
     */
    public static <T, C> DynamicMenu<T, C> shuffleOptions(
            DynamicMenu<T, C> menu,
            long seed) {

        return ShuffleFamily.shuffle(menu, new Random(seed));
    }

    // -------------------------------------------------------------------------
    // Query helpers - API estàtica lleugera
    // -------------------------------------------------------------------------

    /**
     * Retorna l'índex de la primera coincidència.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return índex trobat o -1
     */
    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.indexOfFirst(menu, selector);
    }

    /**
     * Retorna l'índex de la primera coincidència dins d'un rang.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang de cerca
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return índex trobat o -1
     */
    public static <T, C> int indexOfFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.indexOfFirst(menu, selector, range);
    }

    /**
     * Retorna l'índex de l'última coincidència.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return índex trobat o -1
     */
    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.indexOfLast(menu, selector);
    }

    /**
     * Retorna l'índex de l'última coincidència dins d'un rang.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang de cerca
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return índex trobat o -1
     */
    public static <T, C> int indexOfLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.indexOfLast(menu, selector, range);
    }

    /**
     * Indica si existeix alguna coincidència.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return {@code true} si existeix alguna coincidència
     */
    public static <T, C> boolean containsMatch(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.containsMatch(menu, selector);
    }

    /**
     * Compta quantes opcions compleixen una condició.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return nombre de coincidències
     */
    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.countMatches(menu, selector);
    }

    /**
     * Compta quantes opcions compleixen una condició dins d'un rang.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang de cerca
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return nombre de coincidències
     */
    public static <T, C> int countMatches(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.countMatches(menu, selector, range);
    }

    /**
     * Retorna tots els índexs que compleixen una condició.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return índexs coincidents
     */
    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.indexesOf(menu, selector);
    }

    /**
     * Retorna tots els índexs que compleixen una condició dins d'un rang.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang de cerca
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return índexs coincidents
     */
    public static <T, C> List<Integer> indexesOf(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.indexesOf(menu, selector, range);
    }

    /**
     * Retorna totes les opcions que compleixen una condició.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return opcions coincidents
     */
    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.matchingOptions(menu, selector);
    }

    /**
     * Retorna totes les opcions que compleixen una condició dins d'un rang.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param range    rang de cerca
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return opcions coincidents
     */
    public static <T, C> List<MenuOption<T, C>> matchingOptions(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return QueryFamily.matchingOptions(menu, selector, range);
    }

    /**
     * Retorna l'índex de la primera coincidència exacta.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus de retorn del menú
     * @param <C>   tipus del context del menú
     * @return índex trobat o -1
     */
    public static <T, C> int indexOfFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.indexOfFirstLabel(menu, label);
    }

    /**
     * Retorna l'índex de l'última coincidència exacta.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus de retorn del menú
     * @param <C>   tipus del context del menú
     * @return índex trobat o -1
     */
    public static <T, C> int indexOfLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.indexOfLastLabel(menu, label);
    }

    /**
     * Indica si existeix alguna coincidència exacta.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus de retorn del menú
     * @param <C>   tipus del context del menú
     * @return {@code true} si existeix
     */
    public static <T, C> boolean containsLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.containsLabel(menu, label);
    }

    /**
     * Compta quantes coincidències exactes hi ha.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus de retorn del menú
     * @param <C>   tipus del context del menú
     * @return nombre de coincidències
     */
    public static <T, C> int countLabelMatches(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.countLabelMatches(menu, label);
    }

    /**
     * Retorna la primera opció que compleix una condició o {@code null}.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return primera coincidència o {@code null}
     */
    public static <T, C> MenuOption<T, C> findFirst(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.findFirst(menu, selector);
    }

    /**
     * Retorna l'última opció que compleix una condició o {@code null}.
     *
     * @param menu     menú objectiu
     * @param selector condició de selecció
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return última coincidència o {@code null}
     */
    public static <T, C> MenuOption<T, C> findLast(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return QueryFamily.findLast(menu, selector);
    }

    /**
     * Retorna la primera coincidència exacta o {@code null}.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus de retorn del menú
     * @param <C>   tipus del context del menú
     * @return primera coincidència o {@code null}
     */
    public static <T, C> MenuOption<T, C> findFirstLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.findFirstLabel(menu, label);
    }

    /**
     * Retorna l'última coincidència exacta o {@code null}.
     *
     * @param menu  menú objectiu
     * @param label etiqueta exacta
     * @param <T>   tipus de retorn del menú
     * @param <C>   tipus del context del menú
     * @return última coincidència o {@code null}
     */
    public static <T, C> MenuOption<T, C> findLastLabel(
            DynamicMenu<T, C> menu,
            String label) {

        return QueryFamily.findLastLabel(menu, label);
    }

    /**
     * Inicia una operació fluïda de consulta.
     *
     * <p>
     * És l'entrada recomanada quan la consulta necessita composició,
     * reaprofitament de selectors o una expressió més llegible que la cadena de
     * helpers estàtics.
     *
     * @param menu menú objectiu
     * @param <T>  tipus de retorn del menú
     * @param <C>  tipus del context del menú
     * @return operador fluent de consulta
     */
    public static <T, C> QueryBuilder<T, C> query(DynamicMenu<T, C> menu) {
        return new QueryBuilder<>(menu);
    }

    /**
     * Inicia una operació fluïda de consulta amb selector inicial.
     *
     * @param menu     menú objectiu
     * @param selector condició inicial
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return operador fluent de consulta
     */
    public static <T, C> QueryBuilder<T, C> query(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector) {

        return query(menu).where(selector);
    }

    /**
     * Inicia una operació fluïda de consulta limitada a un rang.
     *
     * @param menu  menú objectiu
     * @param range rang inicial de cerca
     * @param <T>   tipus de retorn del menú
     * @param <C>   tipus del context del menú
     * @return operador fluent de consulta
     */
    public static <T, C> QueryBuilder<T, C> query(
            DynamicMenu<T, C> menu,
            Range range) {

        return query(menu).range(range);
    }

    /**
     * Inicia una operació fluïda de consulta amb selector i rang inicials.
     *
     * @param menu     menú objectiu
     * @param selector condició inicial
     * @param range    rang inicial de cerca
     * @param <T>      tipus de retorn del menú
     * @param <C>      tipus del context del menú
     * @return operador fluent de consulta
     */
    public static <T, C> QueryBuilder<T, C> query(
            DynamicMenu<T, C> menu,
            OptionSelector<T, C> selector,
            Range range) {

        return query(menu).where(selector).range(range);
    }
}
