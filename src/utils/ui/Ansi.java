package utils.ui;

public final class Ansi {
    private Ansi() {
    }

    // * ESC
    private static final String ESC = "\u001b[";

    public static final String RESET = ESC + "0m";
    public static final String BOLD = ESC + "1m";
    public static final String DIM = ESC + "2m";

    public static final String RED = ESC + "31m";
    public static final String GREEN = ESC + "32m";
    public static final String YELLOW = ESC + "33m";
    public static final String BLUE = ESC + "34m";
    public static final String MAGENTA = ESC + "35m";
    public static final String CYAN = ESC + "36m";
    public static final String WHITE = ESC + "37m";

    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_BLUE = "\u001B[94m";

    public static final String ORANGE = ESC + "38;2;255;165;0m";
    public static final String DARK_GRAY = ESC + "38;2;120;120;120m";

}
