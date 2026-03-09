package menu.model;

import java.util.Objects;

/**
 * Representa el resultat d'una acció de menú.
 *
 * <p>
 * Una acció pot indicar que el menú ha de continuar, retornar un valor
 * o finalitzar sense valor.
 * </p>
 *
 * @param <T> tipus del valor de retorn associat al menú
 */
public final class MenuResult<T> {

    /**
     * Tipus de resultat possibles del menú.
     */
    public enum Type {
        /** El menú ha de continuar amb la següent iteració. */
        CONTINUE,
        /** El menú ha de retornar un valor i finalitzar. */
        RETURN,
        /** El menú ha de sortir sense retornar cap valor. */
        EXIT
    }

    private final Type type;
    private final T value;

    private MenuResult(Type type, T value) {
        this.type = Objects.requireNonNull(type, "El tipus de resultat no pot ser nul");
        this.value = value;
    }

    /**
     * Retorna el tipus del resultat.
     *
     * @return tipus del resultat
     */
    public Type getType() {
        return type;
    }

    /**
     * Retorna el valor associat al resultat.
     *
     * <p>
     * Només té sentit quan el tipus és {@link Type#RETURN}.
     * </p>
     *
     * @return valor associat, o {@code null} si no n'hi ha
     */
    public T getValue() {
        return value;
    }

    /**
     * Indica si el resultat fa continuar el bucle del menú.
     *
     * @return {@code true} si el tipus és {@link Type#CONTINUE}
     */
    public boolean continuesLoop() {
        return type == Type.CONTINUE;
    }

    /**
     * Crea un resultat que fa continuar el menú.
     *
     * @param <T> tipus del valor de retorn del menú
     * @return resultat de continuació
     */
    public static <T> MenuResult<T> continueLoop() {
        return new MenuResult<>(Type.CONTINUE, null);
    }

    /**
     * Crea un resultat que retorna un valor i finalitza el menú.
     *
     * @param value valor a retornar
     * @param <T>   tipus del valor de retorn del menú
     * @return resultat de retorn
     */
    public static <T> MenuResult<T> returnValue(T value) {
        return new MenuResult<>(Type.RETURN, value);
    }

    /**
     * Crea un resultat que finalitza el menú sense valor.
     *
     * @param <T> tipus del valor de retorn del menú
     * @return resultat de sortida
     */
    public static <T> MenuResult<T> exitMenu() {
        return new MenuResult<>(Type.EXIT, null);
    }
}
