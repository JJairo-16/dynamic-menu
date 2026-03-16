package menu.editor;

/** Rang d'índexs del tipus [fromInclusive, toExclusive). */
public record Range(int fromInclusive, int toExclusive) {

    /**
     * Crea un rang complet.
     *
     * @return rang complet
     */
    public static Range all() {
        return new Range(0, Integer.MAX_VALUE);
    }

    /**
     * Crea un rang específic.
     *
     * @param fromInclusive inici inclòs
     * @param toExclusive   final exclòs
     * @return nou rang
     */
    public static Range of(int fromInclusive, int toExclusive) {
        return new Range(fromInclusive, toExclusive);
    }

    /**
     * Crea un rang específic resolent índexs negatius segons la mida indicada.
     *
     * Els índexs negatius es compten des del final:
     * -1 és l'últim element, -2 el penúltim, etc.
     *
     * @param fromInclusive inici inclòs
     * @param toExclusive   final exclòs
     * @param size          mida usada per resoldre índexs relatius
     * @return nou rang ja resolt
     */
    public static Range of(int fromInclusive, int toExclusive, int size) {
        int resolvedFrom = resolveIndex(fromInclusive, size);
        int resolvedTo = resolveIndex(toExclusive, size);

        int clampedFrom = Math.clamp(resolvedFrom, 0, size);
        int clampedTo = Math.clamp(resolvedTo, 0, size);

        return new Range(clampedFrom, clampedTo);
    }

    public Range {
        if (toExclusive < fromInclusive) {
            throw new IndexOutOfBoundsException(
                    "L'índex final no pot ser menor que l'inicial: "
                            + toExclusive + " < " + fromInclusive);
        }
    }

    /**
     * Indica si un índex està dins del rang.
     *
     * @param index índex a comprovar
     * @return {@code true} si hi està inclòs
     */
    public boolean contains(int index) {
        return index >= fromInclusive && index < toExclusive;
    }

    /**
     * Ajusta el rang a una mida concreta.
     *
     * @param size mida actual
     * @return rang ajustat
     */
    public Range clamp(int size) {
        int clampedFrom = Math.min(fromInclusive, size);
        int clampedTo = Math.min(toExclusive, size);
        return new Range(clampedFrom, clampedTo);
    }

    private static int resolveIndex(int index, int size) {
        return index < 0 ? size + index : index;
    }
}