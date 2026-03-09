package utils.input;

import java.util.List;
import java.util.Scanner;

import utils.ui.Cleaner;
import utils.ui.Prettier;

public class Menu {
    private Menu() {
    }

    private static final Scanner scanner = new Scanner(System.in);
    private static final Cleaner cls = new Cleaner();

    public static int getOption(List<String> options, String title) {
        boolean loop = true;
        int option = -1;

        while (loop) {
            cls.clear();
            Prettier.printTitle(title);
            printArr(options);
            System.out.println();

            System.out.print("Seleccioni una opció, si us plau: ");
            option = getInteger();

            if (option == -1) {
                pause();
                continue;
            }

            if (option >= 1 && option <= options.size()) {
                loop = false;
            } else {
                Prettier.warn("L'opció introduïda ha de estar entre 1 i %d. Si us plau, torni a intentar-ho.",
                        options.size());
                pause();
            }
        }

        return option;
    }

    private static void printArr(List<String> arr) {
        for (int i = 0; i < arr.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, arr.get(i));
        }
    }

    private static int getInteger() {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            Prettier.warn("L'opció introduïda no pot estar en blanc. Si us plau, torni a intentar-ho.");
            return -1;
        }

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            Prettier.warn("L'opció introduïda ha de ser un nombre enter positiu. Si us plau, torni a intentar-ho.");
        } catch (Exception e) {
            Prettier.warn("Ha hagut un error. Si us plau, torni a intentar-ho.");
        }

        return -1;
    }

    public static void pause() {
        System.out.print("Prem enter per continuar... ");
        scanner.nextLine();
    }
}
