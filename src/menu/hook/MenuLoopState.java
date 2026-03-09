package menu.hook;

import menu.model.MenuResult;

/**
 * Estat informatiu d'una iteració del bucle del menú.
 *
 * @param context context del menú
 * @param iteration número d'iteració actual
 * @param lastResult últim resultat obtingut, si n'hi ha
 * @param willContinue indica si el menú continuarà després de l'acció actual
 * @param selectedOptionNumber número de l'opció seleccionada, si n'hi ha
 * @param selectedOptionText text de l'opció seleccionada, si n'hi ha
 * @param <T> tipus del valor de retorn del menú
 * @param <C> tipus del context del menú
 */
public record MenuLoopState<T, C>(
        C context,
        int iteration,
        MenuResult<T> lastResult,
        boolean willContinue,
        Integer selectedOptionNumber,
        String selectedOptionText) {

    /**
     * Indica si hi ha un resultat previ disponible.
     *
     * @return {@code true} si {@code lastResult} no és {@code null}
     */
    public boolean hasLastResult() {
        return lastResult != null;
    }

    /**
     * Indica si aquesta és la primera iteració del menú.
     *
     * @return {@code true} si la iteració és 1
     */
    public boolean isFirstIteration() {
        return iteration == 1;
    }

    /**
     * Indica si el menú finalitzarà després de l'acció actual.
     *
     * @return {@code true} si no continuarà
     */
    public boolean willEnd() {
        return !willContinue;
    }

    /**
     * Indica si hi ha una opció seleccionada disponible.
     *
     * @return {@code true} si hi ha número d'opció seleccionada
     */
    public boolean hasSelectedOption() {
        return selectedOptionNumber != null;
    }
}
