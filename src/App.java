import menu.DynamicMenu;
import menu.model.MenuResult;

import utils.input.Menu;

public class App {
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        DynamicMenu<String, Void> menu = DynamicMenu.withoutContext("Menu", Menu::getOption);
        menu.autoCleanup(true);

        menu.addOption("Saludar", () -> {
            System.out.println("Hola");

            return MenuResult.continueLoop();
        });

        menu.addOption("Sumar 2+2", () -> MenuResult.returnValue("2 + 2 = 4"));

        menu.beforeEachAction(state -> System.out.println());
        menu.afterEachAction(state -> {
            if (!state.willEnd()) {
                System.out.println();
                Menu.pause();
            }
        });

        DynamicMenu<String, Void> child1 = menu.createChildMenu("A");
        DynamicMenu<String, Void> child2 = menu.createChildMenu("B");

        child1.addOption("carajo", MenuResult::continueLoop);
        child2.addOption("pendejo", MenuResult::continueLoop);

        String r = menu.run();
        System.out.println(r);

        child1.run();
        child2.run();
    }
}