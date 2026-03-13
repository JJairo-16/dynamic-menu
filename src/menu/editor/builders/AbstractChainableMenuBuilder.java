package menu.editor.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import menu.DynamicMenu;

/**
 * Contracte base per als builders encadenables de {@code MenuEditor}.
 *
 * <p>Aquesta classe centralitza la gestió del menú objectiu i de la cadena
 * d'operacions pendents. Les crides {@code thenX()} dels builders concrets no
 * executen canvis immediatament: simplement afegeixen l'operació actual a la
 * cadena perquè el builder terminal l'apliqui en ordre quan es cridi una
 * operació final com ara {@code execute()} o {@code apply()}.
 *
 * @param <T> tipus de retorn del menú
 * @param <C> tipus del context del menú
 */
public abstract class AbstractChainableMenuBuilder<T, C> {
    private final DynamicMenu<T, C> menu;
    private final List<Consumer<DynamicMenu<T, C>>> pendingOperations;

    protected AbstractChainableMenuBuilder(DynamicMenu<T, C> menu) {
        this(menu, List.of());
    }

    protected AbstractChainableMenuBuilder(
            DynamicMenu<T, C> menu,
            List<Consumer<DynamicMenu<T, C>>> pendingOperations) {

        this.menu = Objects.requireNonNull(menu, "El menú no pot ser nul");
        this.pendingOperations = new ArrayList<>(Objects.requireNonNull(
                pendingOperations,
                "La cadena d'operacions no pot ser nul·la"));
    }

    /**
     * Retorna el menú objectiu de la cadena.
     *
     * @return menú objectiu
     */
    public final DynamicMenu<T, C> menu() {
        return menu;
    }

    /**
     * Aplica totes les operacions pendents acumulades abans de l'operació final.
     */
    protected final void applyPendingOperations() {
        for (Consumer<DynamicMenu<T, C>> operation : pendingOperations) {
            operation.accept(menu);
        }
    }

    /**
     * Retorna una nova llista amb les operacions pendents i l'operació actual al final.
     *
     * @param currentOperation operació actual a afegir
     * @return nova cadena d'operacions pendents
     */
    protected final List<Consumer<DynamicMenu<T, C>>> pendingPlus(
            Consumer<DynamicMenu<T, C>> currentOperation) {

        Objects.requireNonNull(currentOperation, "L'operació actual no pot ser nul·la");
        List<Consumer<DynamicMenu<T, C>>> operations = new ArrayList<>(pendingOperations);
        operations.add(currentOperation);
        return operations;
    }
}
