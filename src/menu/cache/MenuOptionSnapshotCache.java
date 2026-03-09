package menu.cache;

import java.util.List;

import menu.model.MenuOption;

/**
 * Cachea una vista inmutable de les opcions d'un snapshot.
 *
 * <p>
 * Evita recrear {@code List.copyOf(options)} en cada iteració del menú.
 * </p>
 */
public final class MenuOptionSnapshotCache<T, C> {

    private List<MenuOption<T, C>> cachedSnapshot;
    private boolean dirty = true;

    public List<MenuOption<T, C>> get(List<MenuOption<T, C>> source) {
        if (!dirty && cachedSnapshot != null) {
            return cachedSnapshot;
        }

        cachedSnapshot = List.copyOf(source);
        dirty = false;
        return cachedSnapshot;
    }

    public void invalidate() {
        dirty = true;
    }

}