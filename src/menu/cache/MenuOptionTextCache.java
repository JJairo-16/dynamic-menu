package menu.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import menu.model.MenuOption;

/**
 * Cachea els textos visibles d'un conjunt d'opcions.
 */
public final class MenuOptionTextCache<T, C> {

    private Object optionSnapshotIdentity;
    private List<String> cachedTexts;

    public List<String> get(Object optionSnapshotKey, List<MenuOption<T, C>> options) {
        if (optionSnapshotIdentity == optionSnapshotKey && cachedTexts != null) {
            return cachedTexts;
        }

        List<String> labels = new ArrayList<>(options.size());

        for (MenuOption<T, C> option : options) {
            Objects.requireNonNull(option, "La llista d'opcions no pot contenir elements nuls");

            labels.add(Objects.requireNonNull(
                    option.label(),
                    "L'etiqueta d'una opció no pot ser nul·la"));
        }

        cachedTexts = List.copyOf(labels);
        optionSnapshotIdentity = optionSnapshotKey;

        return cachedTexts;
    }

    public void invalidate() {
        optionSnapshotIdentity = null;
        cachedTexts = null;
    }
}
