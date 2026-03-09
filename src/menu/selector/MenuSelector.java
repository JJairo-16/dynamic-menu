package menu.selector;

import java.util.List;

/**
 * Component encarregat de presentar les opcions d'un menú i retornar
 * el número de l'opció triada.
 */
@FunctionalInterface
public interface MenuSelector {

    /**
     * Sol·licita o resol l'opció escollida.
     *
     * @param options textos de les opcions disponibles
     * @param title títol actual del menú
     * @return número d'opció seleccionat, començant a 1
     */
    int getOption(List<String> options, String title);
}
