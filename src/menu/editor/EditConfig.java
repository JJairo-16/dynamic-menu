package menu.editor;

import java.util.Objects;

/** Configuració d'edició per a operacions condicionals. */
public final class EditConfig {
    private final Range range;
    private final int limit;
    private final boolean reverse;

    private EditConfig(Range range, int limit, boolean reverse) {
        this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
        if (limit < 0) {
            throw new IllegalArgumentException("El límit no pot ser negatiu");
        }
        this.limit = limit;
        this.reverse = reverse;
    }

    /**
     * Retorna la configuració per defecte.
     *
     * @return configuració per defecte
     */
    public static EditConfig defaults() {
        return new EditConfig(Range.all(), Integer.MAX_VALUE, false);
    }

    /**
     * Crea una configuració amb un rang.
     *
     * @param range rang a aplicar
     * @return configuració nova
     */
    public static EditConfig of(Range range) {
        return builder().range(range).build();
    }

    /**
     * Crea un builder.
     *
     * @return builder nou
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Retorna el rang.
     *
     * @return rang
     */
    public Range range() {
        return range;
    }

    /**
     * Retorna el límit.
     *
     * @return límit
     */
    public int limit() {
        return limit;
    }

    /**
     * Indica si s'ha de recórrer en sentit invers.
     *
     * @return {@code true} si és invers
     */
    public boolean reverse() {
        return reverse;
    }

    /** Builder de {@link EditConfig}. */
    public static final class Builder {
        private Range range = Range.all();
        private int limit = Integer.MAX_VALUE;
        private boolean reverse = false;

        private Builder() {
        }

        /**
         * Defineix el rang.
         *
         * @param range rang
         * @return aquest builder
         */
        public Builder range(Range range) {
            this.range = Objects.requireNonNull(range, "El rang no pot ser nul");
            return this;
        }

        /**
         * Defineix el rang.
         *
         * @param fromInclusive inici inclòs
         * @param toExclusive   final exclòs
         * @return aquest builder
         */
        public Builder range(int fromInclusive, int toExclusive) {
            return range(Range.of(fromInclusive, toExclusive));
        }

        /**
         * Defineix el límit.
         *
         * @param limit límit
         * @return aquest builder
         */
        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Defineix si s'ha de recórrer en sentit invers.
         *
         * @param reverse valor nou
         * @return aquest builder
         */
        public Builder reverse(boolean reverse) {
            this.reverse = reverse;
            return this;
        }

        /**
         * Construeix la configuració.
         *
         * @return configuració nova
         */
        public EditConfig build() {
            return new EditConfig(range, limit, reverse);
        }
    }
}
