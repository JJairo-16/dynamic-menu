package utils.input;

import java.util.Scanner;
import java.util.Set;

import utils.ui.Prettier;

public class Getters {

    private final Scanner scanner = new Scanner(System.in);
    private boolean acceptCommaAsDecimalSeparator = false;

    /**
     * Indica si s'accepta la coma (,) com a separador decimal en les entrades numèriques amb decimals.
     *
     * @return {@code true} si s'accepta la coma com a separador decimal; {@code false} si només s'accepta el punt (.)
     */
    public boolean isAcceptCommaAsDecimalSeparator() {
        return acceptCommaAsDecimalSeparator;
    }

    /**
     * Activa o desactiva l'acceptació de la coma (,) com a separador decimal en les entrades numèriques amb decimals.
     *
     * @param value {@code true} per acceptar la coma com a separador decimal; {@code false} per acceptar només el punt (.)
     */
    public void setAcceptCommaAsDecimalSeparator(boolean value) {
        acceptCommaAsDecimalSeparator = value;
    }

    private void pause() {
        System.out.print("Prem la tecla Enter per continuar... ");
        scanner.nextLine();
    }

    private String readLineNormalizedForDouble(String input) {
        if (acceptCommaAsDecimalSeparator) {
            return input.replace(',', '.');
        }
        return input;
    }

    // #region Single-Getters

    /**
     * Llegeix un text no buit.
     *
     * @param prompt missatge a mostrar abans de llegir
     * @param name   nom del camp (per als missatges d'error)
     * @return text introduït (retallat amb {@code trim()})
     */
    public String getString(String prompt, String name) {
        return getString(prompt, name, 0, Integer.MAX_VALUE, Set.of());
    }

    /**
     * Llegeix un text no buit amb restricció de longitud.
     *
     * @param prompt missatge a mostrar abans de llegir
     * @param name   nom del camp (per als missatges d'error)
     * @param minLen longitud mínima (inclos)
     * @param maxLen longitud màxima (inclos)
     * @return text introduït (retallat amb {@code trim()})
     */
    public String getString(String prompt, String name, int minLen, int maxLen) {
        return getString(prompt, name, minLen, maxLen, Set.of());
    }

    /**
     * Llegeix un text no buit amb restricció de longitud i un valor prohibit.
     *
     * @param prompt           missatge a mostrar abans de llegir
     * @param name             nom del camp (per als missatges d'error)
     * @param minLen           longitud mínima (inclos)
     * @param maxLen           longitud màxima (inclos)
     * @param singleProhibited valor que no s'acceptarà (pot ser {@code null})
     * @return text introduït (retallat amb {@code trim()})
     */
    public String getString(String prompt, String name, int minLen, int maxLen, String singleProhibited) {
        Set<String> p = singleProhibited == null ? Set.of() : Set.of(singleProhibited);
        return getString(prompt, name, minLen, maxLen, p);
    }

    /**
     * Llegeix un text no buit amb restricció de longitud i un conjunt de valors prohibits.
     *
     * @param prompt      missatge a mostrar abans de llegir
     * @param name        nom del camp (per als missatges d'error)
     * @param minLen      longitud mínima (inclos)
     * @param maxLen      longitud màxima (inclos)
     * @param prohibiteds conjunt de valors no permesos
     * @return text introduït (retallat amb {@code trim()})
     */
    public String getString(String prompt, String name, int minLen, int maxLen, Set<String> prohibiteds) {
        String text = "";
        boolean loop = true;

        do {
            System.out.print(prompt);
            text = scanner.nextLine().trim();

            if (text.isEmpty()) {
                Prettier.warn("%s no pot estar en blanc. Si us plau, torni a intentar-ho.", name);
            } else if (text.length() < minLen || text.length() > maxLen) {
                Prettier.warn("%s ha de tenir una longitud d'entre %d i %d caràcters. Si us plau, torni a intentar-ho.",
                        name, minLen, maxLen);
            } else if (prohibiteds.contains(text)) {
                Prettier.warn("%s no està disponible. Si us plau, torni a intentar-ho.", name);
            } else {
                loop = false;
            }

            if (loop) {
                pause();
                System.out.println();
            }
        } while (loop);

        return text;
    }

    /**
     * Llegeix un enter.
     *
     * @param prompt missatge a mostrar abans de llegir
     * @param name   nom del camp (per als missatges d'error)
     * @return valor enter introduït
     */
    public int getInteger(String prompt, String name) {
        return getInteger(prompt, name, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Llegeix un enter dins d'un rang.
     *
     * @param prompt missatge a mostrar abans de llegir
     * @param name   nom del camp (per als missatges d'error)
     * @param min    valor mínim (inclos)
     * @param max    valor màxim (inclos)
     * @return valor enter introduït
     */
    public int getInteger(String prompt, String name, int min, int max) {
        int value = -1;
        boolean loop = true;

        do {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            boolean skip = true;
            if (input.isEmpty()) {
                Prettier.warn("%s no pot estar en blanc. Si us plau, torni a intentar-ho.", name);
            } else if (!isInteger(input)) {
                Prettier.warn("%s ha de ser un nombre enter. Si us plau, torni a intentar-ho.", name);
            } else {
                skip = false;
            }

            if (skip) {
                pause();
                System.out.println();
                continue;
            }

            value = Integer.parseInt(input);

            if (value >= min && value <= max) {
                loop = false;
            } else {
                Prettier.warn("%s ha d'estar entre %d i %d. Si us plau, torni a intentar-ho.", name, min, max);
                pause();
                System.out.println();
            }
        } while (loop);

        return value;
    }

    /**
     * Llegeix un nombre decimal.
     *
     * @param prompt missatge a mostrar abans de llegir
     * @param name   nom del camp (per als missatges d'error)
     * @return valor decimal introduït
     */
    public double getDouble(String prompt, String name) {
        return getDouble(prompt, name, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Llegeix un nombre decimal dins d'un rang.
     * Si {@link #isAcceptCommaAsDecimalSeparator()} és {@code true}, també s'accepta la coma (,) com a separador decimal.
     *
     * @param prompt missatge a mostrar abans de llegir
     * @param name   nom del camp (per als missatges d'error)
     * @param min    valor mínim (inclos)
     * @param max    valor màxim (inclos)
     * @return valor decimal introduït
     */
    public double getDouble(String prompt, String name, double min, double max) {
        double value = 0;
        boolean loop = true;

        do {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            input = readLineNormalizedForDouble(input);

            boolean skip = true;
            if (input.isEmpty()) {
                Prettier.warn("%s no pot estar en blanc. Si us plau, torni a intentar-ho.", name);
            } else if (!isDouble(input)) {
                Prettier.warn("%s ha de ser un nombre decimal. Si us plau, torni a intentar-ho.", name);
            } else {
                skip = false;
            }

            if (skip) {
                pause();
                System.out.println();
                continue;
            }

            value = Double.parseDouble(input);

            if (value >= min && value <= max) {
                loop = false;
            } else {
                Prettier.warn("%s ha d'estar entre %f i %f. Si us plau, torni a intentar-ho.", name, min, max);
                pause();
                System.out.println();
            }
        } while (loop);

        return value;
    }

    /**
     * Llegeix un enter llarg (long).
     *
     * @param prompt missatge a mostrar abans de llegir
     * @param name   nom del camp (per als missatges d'error)
     * @return valor {@code long} introduït
     */
    public long getLong(String prompt, String name) {
        return getLong(prompt, name, -Long.MAX_VALUE, Long.MAX_VALUE);
    }

    /**
     * Llegeix un enter llarg (long) dins d'un rang.
     *
     * @param prompt missatge a mostrar abans de llegir
     * @param name   nom del camp (per als missatges d'error)
     * @param min    valor mínim (inclos)
     * @param max    valor màxim (inclos)
     * @return valor {@code long} introduït
     */
    public long getLong(String prompt, String name, long min, long max) {
        long value = 0;
        boolean loop = true;

        do {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            boolean skip = true;
            if (input.isEmpty()) {
                Prettier.warn("%s no pot estar en blanc. Si us plau, torni a intentar-ho.", name);
            } else if (!isLong(input)) {
                Prettier.warn("%s ha de ser un nombre enter (long). Si us plau, torni a intentar-ho.", name);
            } else {
                skip = false;
            }

            if (skip) {
                pause();
                System.out.println();
                continue;
            }

            value = Long.parseLong(input);

            if (value >= min && value <= max) {
                loop = false;
            } else {
                Prettier.warn("%s ha d'estar entre %d i %d. Si us plau, torni a intentar-ho.", name, min, max);
                pause();
                System.out.println();
            }
        } while (loop);

        return value;
    }

    /**
     * Llegeix un valor booleà a partir de dos textos (p. ex. "s" i "n").
     * Si l'usuari prem Enter, es retorna el valor per defecte.
     *
     * @param prompt        missatge a mostrar abans de llegir
     * @param defaultValue  valor per defecte si l'usuari prem Enter
     * @param trueText      text que representa {@code true}
     * @param falseText     text que representa {@code false}
     * @return el valor booleà llegit
     */
    public boolean getBoolean(String prompt, boolean defaultValue, String trueText, String falseText) {
        boolean result = defaultValue;
        boolean loop = true;

        do {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            boolean stop = true;
            if (input.isEmpty()) {
                loop = false;
                continue;
            } else if (input.equalsIgnoreCase(trueText)) {
                result = true;
            } else if (input.equalsIgnoreCase(falseText)) {
                result = false;
            } else {
                stop = false;
                Prettier.warn("Valor no vàlid. Escrigui \"%s\" o \"%s\" (o premi Enter per defecte).", trueText,
                        falseText);
                pause();
                System.out.println();
            }

            loop = !stop;
        } while (loop);

        return result;
    }

    // #endregion

    // #region Default-Getters

    /**
     * Llegeix un text; si l'usuari deixa el camp en blanc, retorna el valor per defecte.
     *
     * @param prompt       missatge a mostrar abans de llegir
     * @param name         nom del camp (per als missatges d'error)
     * @param defaultValue valor per defecte si l'usuari prem Enter
     * @return text introduït (retallat amb {@code trim()}) o el valor per defecte
     */
    public String getStringOrDefault(String prompt, String name, String defaultValue) {
        return getStringOrDefault(prompt, name, defaultValue, 0, Integer.MAX_VALUE, Set.of());
    }

    /**
     * Llegeix un text; si l'usuari deixa el camp en blanc, retorna el valor per defecte.
     * També valida la longitud.
     *
     * @param prompt       missatge a mostrar abans de llegir
     * @param name         nom del camp (per als missatges d'error)
     * @param defaultValue valor per defecte si l'usuari prem Enter
     * @param minLen       longitud mínima (inclos)
     * @param maxLen       longitud màxima (inclos)
     * @return text introduït (retallat amb {@code trim()}) o el valor per defecte
     */
    public String getStringOrDefault(String prompt, String name, String defaultValue, int minLen, int maxLen) {
        return getStringOrDefault(prompt, name, defaultValue, minLen, maxLen, Set.of());
    }

    /**
     * Llegeix un text; si l'usuari deixa el camp en blanc, retorna el valor per defecte.
     * També valida la longitud i un valor prohibit.
     *
     * @param prompt           missatge a mostrar abans de llegir
     * @param name             nom del camp (per als missatges d'error)
     * @param defaultValue     valor per defecte si l'usuari prem Enter
     * @param minLen           longitud mínima (inclos)
     * @param maxLen           longitud màxima (inclos)
     * @param singleProhibited valor que no s'acceptarà (pot ser {@code null})
     * @return text introduït (retallat amb {@code trim()}) o el valor per defecte
     */
    public String getStringOrDefault(String prompt, String name, String defaultValue, int minLen, int maxLen,
            String singleProhibited) {
        Set<String> p = singleProhibited == null ? Set.of() : Set.of(singleProhibited);
        return getStringOrDefault(prompt, name, defaultValue, minLen, maxLen, p);
    }

    /**
     * Llegeix un text; si l'usuari deixa el camp en blanc, retorna el valor per defecte.
     * També valida la longitud i un conjunt de valors prohibits.
     *
     * @param prompt       missatge a mostrar abans de llegir
     * @param name         nom del camp (per als missatges d'error)
     * @param defaultValue valor per defecte si l'usuari prem Enter
     * @param minLen       longitud mínima (inclos)
     * @param maxLen       longitud màxima (inclos)
     * @param prohibiteds  conjunt de valors no permesos
     * @return text introduït (retallat amb {@code trim()}) o el valor per defecte
     */
    public String getStringOrDefault(String prompt, String name, String defaultValue, int minLen, int maxLen,
            Set<String> prohibiteds) {
        String text = "";
        boolean loop = true;

        do {
            System.out.print(prompt);
            text = scanner.nextLine().trim();

            if (text.isEmpty()) {
                loop = false;
                text = defaultValue;
            } else if (text.length() < minLen || text.length() > maxLen) {
                Prettier.warn("%s ha de tenir una longitud d'entre %d i %d caràcters. Si us plau, torni a intentar-ho.",
                        name, minLen, maxLen);
            } else if (prohibiteds.contains(text)) {
                Prettier.warn("%s no està disponible. Si us plau, torni a intentar-ho.", name);
            } else {
                loop = false;
            }

            if (loop) {
                pause();
                System.out.println();
            }
        } while (loop);

        return text;
    }

    /**
     * Llegeix un text; si l'usuari deixa el camp en blanc, retorna la cadena buida.
     *
     * @param prompt missatge a mostrar abans de llegir
     * @param name   nom del camp (per als missatges d'error)
     * @return text introduït (retallat amb {@code trim()}) o la cadena buida
     */
    public String getStringAllowEmpty(String prompt, String name) {
        return getStringAllowEmpty(prompt, name, 0, Integer.MAX_VALUE, Set.of());
    }

    /**
     * Llegeix un text; si l'usuari deixa el camp en blanc, retorna la cadena buida.
     * També valida la longitud.
     *
     * @param prompt missatge a mostrar abans de llegir
     * @param name   nom del camp (per als missatges d'error)
     * @param minLen longitud mínima (inclos)
     * @param maxLen longitud màxima (inclos)
     * @return text introduït (retallat amb {@code trim()}) o la cadena buida
     */
    public String getStringAllowEmpty(String prompt, String name, int minLen, int maxLen) {
        return getStringAllowEmpty(prompt, name, minLen, maxLen, Set.of());
    }

    /**
     * Llegeix un text; si l'usuari deixa el camp en blanc, retorna la cadena buida.
     * També valida la longitud i un valor prohibit.
     *
     * @param prompt           missatge a mostrar abans de llegir
     * @param name             nom del camp (per als missatges d'error)
     * @param minLen           longitud mínima (inclos)
     * @param maxLen           longitud màxima (inclos)
     * @param singleProhibited valor que no s'acceptarà (pot ser {@code null})
     * @return text introduït (retallat amb {@code trim()}) o la cadena buida
     */
    public String getStringAllowEmpty(String prompt, String name, int minLen, int maxLen, String singleProhibited) {
        Set<String> p = singleProhibited == null ? Set.of() : Set.of(singleProhibited);
        return getStringAllowEmpty(prompt, name, minLen, maxLen, p);
    }

    /**
     * Llegeix un text; si l'usuari deixa el camp en blanc, retorna la cadena buida.
     * També valida la longitud i un conjunt de valors prohibits.
     *
     * @param prompt      missatge a mostrar abans de llegir
     * @param name        nom del camp (per als missatges d'error)
     * @param minLen      longitud mínima (inclos)
     * @param maxLen      longitud màxima (inclos)
     * @param prohibiteds conjunt de valors no permesos
     * @return text introduït (retallat amb {@code trim()}) o la cadena buida
     */
    public String getStringAllowEmpty(String prompt, String name, int minLen, int maxLen, Set<String> prohibiteds) {
        String text = "";
        boolean loop = true;

        do {
            System.out.print(prompt);
            text = scanner.nextLine().trim();

            if (text.isEmpty()) {
                loop = false;
                text = "";
            } else if (text.length() < minLen || text.length() > maxLen) {
                Prettier.warn("%s ha de tenir una longitud d'entre %d i %d caràcters. Si us plau, torni a intentar-ho.",
                        name, minLen, maxLen);
            } else if (prohibiteds.contains(text)) {
                Prettier.warn("%s no està disponible. Si us plau, torni a intentar-ho.", name);
            } else {
                loop = false;
            }

            if (loop) {
                pause();
                System.out.println();
            }
        } while (loop);

        return text;
    }

    /**
     * Llegeix un enter; si l'usuari prem Enter, retorna el valor per defecte.
     *
     * @param prompt       missatge a mostrar abans de llegir
     * @param name         nom del camp (per als missatges d'error)
     * @param defaultValue valor per defecte si l'usuari prem Enter
     * @return valor enter introduït o el valor per defecte
     */
    public int getIntegerOrDefault(String prompt, String name, int defaultValue) {
        return getIntegerOrDefault(prompt, name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Llegeix un enter dins d'un rang; si l'usuari prem Enter, retorna el valor per defecte.
     *
     * @param prompt       missatge a mostrar abans de llegir
     * @param name         nom del camp (per als missatges d'error)
     * @param defaultValue valor per defecte si l'usuari prem Enter
     * @param min          valor mínim (inclos)
     * @param max          valor màxim (inclos)
     * @return valor enter introduït o el valor per defecte
     */
    public int getIntegerOrDefault(String prompt, String name, int defaultValue, int min, int max) {
        int value = defaultValue;
        boolean loop = true;

        do {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            boolean skip = true;
            if (input.isEmpty()) {
                value = defaultValue;
                loop = false;
                continue;
            } else if (!isInteger(input)) {
                Prettier.warn("%s ha de ser un nombre enter. Si us plau, torni a intentar-ho.", name);
            } else {
                skip = false;
            }

            if (skip) {
                pause();
                System.out.println();
                continue;
            }

            value = Integer.parseInt(input);

            if (value >= min && value <= max) {
                loop = false;
            } else {
                Prettier.warn("%s ha d'estar entre %d i %d. Si us plau, torni a intentar-ho.", name, min, max);
                pause();
                System.out.println();
            }
        } while (loop);

        return value;
    }

    /**
     * Llegeix un decimal; si l'usuari prem Enter, retorna el valor per defecte.
     *
     * @param prompt       missatge a mostrar abans de llegir
     * @param name         nom del camp (per als missatges d'error)
     * @param defaultValue valor per defecte si l'usuari prem Enter
     * @return valor decimal introduït o el valor per defecte
     */
    public double getDoubleOrDefault(String prompt, String name, double defaultValue) {
        return getDoubleOrDefault(prompt, name, defaultValue, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Llegeix un decimal dins d'un rang; si l'usuari prem Enter, retorna el valor per defecte.
     * Si {@link #isAcceptCommaAsDecimalSeparator()} és {@code true}, també s'accepta la coma (,) com a separador decimal.
     *
     * @param prompt       missatge a mostrar abans de llegir
     * @param name         nom del camp (per als missatges d'error)
     * @param defaultValue valor per defecte si l'usuari prem Enter
     * @param min          valor mínim (inclos)
     * @param max          valor màxim (inclos)
     * @return valor decimal introduït o el valor per defecte
     */
    public double getDoubleOrDefault(String prompt, String name, double defaultValue, double min, double max) {
        double value = defaultValue;
        boolean loop = true;

        do {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            input = readLineNormalizedForDouble(input);

            boolean skip = true;
            if (input.isEmpty()) {
                value = defaultValue;
                loop = false;
                continue;
            } else if (!isDouble(input)) {
                Prettier.warn("%s ha de ser un nombre decimal. Si us plau, torni a intentar-ho.", name);
            } else {
                skip = false;
            }

            if (skip) {
                pause();
                System.out.println();
                continue;
            }

            value = Double.parseDouble(input);

            if (value >= min && value <= max) {
                loop = false;
            } else {
                Prettier.warn("%s ha d'estar entre %f i %f. Si us plau, torni a intentar-ho.", name, min, max);
                pause();
                System.out.println();
            }
        } while (loop);

        return value;
    }

    /**
     * Llegeix un {@code long}; si l'usuari prem Enter, retorna el valor per defecte.
     *
     * @param prompt       missatge a mostrar abans de llegir
     * @param name         nom del camp (per als missatges d'error)
     * @param defaultValue valor per defecte si l'usuari prem Enter
     * @return valor {@code long} introduït o el valor per defecte
     */
    public long getLongOrDefault(String prompt, String name, long defaultValue) {
        return getLongOrDefault(prompt, name, defaultValue, -Long.MAX_VALUE, Long.MAX_VALUE);
    }

    /**
     * Llegeix un {@code long} dins d'un rang; si l'usuari prem Enter, retorna el valor per defecte.
     *
     * @param prompt       missatge a mostrar abans de llegir
     * @param name         nom del camp (per als missatges d'error)
     * @param defaultValue valor per defecte si l'usuari prem Enter
     * @param min          valor mínim (inclos)
     * @param max          valor màxim (inclos)
     * @return valor {@code long} introduït o el valor per defecte
     */
    public long getLongOrDefault(String prompt, String name, long defaultValue, long min, long max) {
        long value = defaultValue;
        boolean loop = true;

        do {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            boolean skip = true;
            if (input.isEmpty()) {
                value = defaultValue;
                loop = false;
                continue;
            } else if (!isLong(input)) {
                Prettier.warn("%s ha de ser un nombre enter (long). Si us plau, torni a intentar-ho.", name);
            } else {
                skip = false;
            }

            if (skip) {
                pause();
                System.out.println();
                continue;
            }

            value = Long.parseLong(input);

            if (value >= min && value <= max) {
                loop = false;
            } else {
                Prettier.warn("%s ha d'estar entre %d i %d. Si us plau, torni a intentar-ho.", name, min, max);
                pause();
                System.out.println();
            }
        } while (loop);

        return value;
    }

    /**
     * Llegeix un booleà; si l'usuari prem Enter, retorna el valor per defecte.
     *
     * @param prompt       missatge a mostrar abans de llegir
     * @param defaultValue valor per defecte si l'usuari prem Enter
     * @param trueText     text que representa {@code true}
     * @param falseText    text que representa {@code false}
     * @return el valor booleà llegit o el valor per defecte
     */
    public boolean getBooleanOrDefault(String prompt, boolean defaultValue, String trueText, String falseText) {
        boolean result = defaultValue;
        boolean loop = true;

        do {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            boolean stop = true;
            if (input.isEmpty()) {
                return defaultValue;
            } else if (input.equalsIgnoreCase(trueText)) {
                result = true;
            } else if (input.equalsIgnoreCase(falseText)) {
                result = false;
            } else {
                stop = false;
                Prettier.warn("Valor no vàlid. Escrigui \"%s\" o \"%s\" (o premi Enter per defecte).", trueText,
                        falseText);
                pause();
                System.out.println();
            }

            loop = !stop;
        } while (loop);

        return result;
    }

    // #endregion

    // #region IsType()

    private boolean isInteger(String input) {
        if (input == null || input.isBlank())
            return false;

        try {
            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String input) {
        if (input == null || input.isBlank())
            return false;

        try {
            Double.parseDouble(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isLong(String input) {
        if (input == null || input.isBlank())
            return false;
        try {
            Long.parseLong(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // #endregion
}
