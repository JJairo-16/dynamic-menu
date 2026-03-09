package menu.wrappers;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import menu.DynamicMenu;

public final class MenuMutationSupport<T, C> {

    private final DynamicMenu<T, C> menu;
    private final Runnable ensureMutable;
    private final Runnable invalidateVisibleCaches;

    public MenuMutationSupport(
            DynamicMenu<T, C> menu,
            Runnable ensureMutable,
            Runnable invalidateVisibleCaches) {

        this.menu = menu;
        this.ensureMutable = ensureMutable;
        this.invalidateVisibleCaches = invalidateVisibleCaches;
    }

    public DynamicMenu<T, C> mutate(Runnable action) {
        ensureMutable.run();
        action.run();
        invalidateVisibleCaches.run();
        return menu;
    }

    public boolean mutateBoolean(BooleanSupplier action) {
        ensureMutable.run();
        boolean changed = action.getAsBoolean();
        if (changed) {
            invalidateVisibleCaches.run();
        }
        return changed;
    }

    public int mutateInt(IntSupplier action) {
        ensureMutable.run();
        int changed = action.getAsInt();
        if (changed > 0) {
            invalidateVisibleCaches.run();
        }
        return changed;
    }

    public DynamicMenu<T, C> mutateNoCache(Runnable action) {
        ensureMutable.run();
        action.run();
        return menu;
    }
}