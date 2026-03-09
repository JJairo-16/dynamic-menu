package utils.ui;

public class Cleaner {
    public Cleaner() {
        cls = getCls();
    }

    private ProcessBuilder cls;
    private static final int DEFAULT_AUX = 20;

    /** Obté el método per netejar la consola. */
    private static ProcessBuilder getCls() {
        boolean isWindows = System.getProperty("os.name", "").startsWith("Windows");
        ProcessBuilder processBuilder;

        if (isWindows) {
            processBuilder = new ProcessBuilder("cmd.exe", "/d", "/q", "/c", "cls");
        } else {
            processBuilder = new ProcessBuilder("clear");
        }

        return processBuilder.inheritIO();
    }

    /** Neteja la consola. En cas d'error, escriu líneas per emular-ho. */
    public void clear(int aux) {
        try {
            cls.start().waitFor();
            return;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ignored) { // ? ignored
        }

        emulate(aux); // ? Emulació
    }

    /**
     * Neteja la consola. En cas d'error, escriu {@value Cleaner#DEFAULT_AUX} líneas
     * per emular-ho.
     */
    public void clear() {
        clear(DEFAULT_AUX);
    }

    private void emulate(int space) {
        if (space <= 0)
            return;
        System.out.print("\n".repeat(space));
    }
}
