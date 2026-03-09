package utils.ui;

import static utils.ui.Ansi.*;

public class Prettier {
    private Prettier() {
    }
    
    private static final String INFO_ICO = CYAN + BOLD + "[i]" + RESET;
    private static final String WARN_ICO = ORANGE + BOLD + "[WARN]" + RESET;
    private static final String ERROR_ICO = RED + BOLD + "[ERR]" + RESET;
    
    // #region Format Helpers

    private static String format(String message, Object... args) {
        return args.length == 0 ? message : String.format(message, args);
    }

    private static void print(String icon, String message) {
        System.out.printf("%n%s %s%n%n", icon, message);
    }

    // #endregion

    public static void info(String message) {
        print(INFO_ICO, message);
    }

    public static void info(String message, Object... args) {
        print(INFO_ICO, format(message, args));
    }

    public static void warn(String message) {
        print(WARN_ICO, message);
    }

    public static void warn(String message, Object... args) {
        print(WARN_ICO, format(message, args));
    }

    public static void error(String message) {
        print(ERROR_ICO, message);
    }

    public static void error(String message, Object... args) {
        print(ERROR_ICO, format(message, args));
    }

    public static void printTitle(String title) {
        String baseFormat = BOLD + MAGENTA;
        System.out.printf("%s=== %s ===%s%n", baseFormat, title, RESET);
    }

    public static void printTitle(String title, Object... args) {
        printTitle(format(title, args));
    }

    public static void appendTitle(StringBuilder sb, String title) {
        sb.append(BOLD).append(MAGENTA);
        sb.append("=== ").append(title).append(" ===");
        sb.append(RESET).append("\n");
    }

}
