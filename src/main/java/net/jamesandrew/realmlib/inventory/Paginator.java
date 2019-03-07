package net.jamesandrew.realmlib.inventory;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;

/**
 * Represents a collection of {@link InventoryPanel}
 * A paginator should be considered immutable
 */
public class Paginator implements Seedable, Updateable {

    private List<InventoryPanel> panels;
    private InventoryPanel currentPanel;
    private final String seed;

    public Paginator() {
        seed = generateInvisibleSeed(3);
        PaginatorManager.register(this);
    }

    @Override
    public String getSeed() {
        return seed;
    }

    public void open(Player p) {
        Validate.notNull(panels, "No panels set");
        getRootPanel().open(p);
    }

    public void openNext(Player p) {
        Validate.notNull(panels, "No panels set");
        if (panels.size() <= 1) return;

        for (int i = 0; i < panels.size(); i++) {
            InventoryPanel panel = panels.get(i);
            if (panel != currentPanel) continue;
            InventoryPanel next = panels.get(i + 1);
            if (panel == null || next == null) break;
            next.open(p);
            currentPanel = next;
            break;
        }
    }

    public void openPrevious(Player p) {
        Validate.notNull(panels, "No panels set");
        if (currentPanel.equals(getRootPanel())) return;

        for (int i = 1; i < panels.size(); i++) {
            InventoryPanel panel = panels.get(i);
            if (panel != currentPanel) continue;
            InventoryPanel previous = panels.get(i - 1);
            if (panel == null || previous == null) break;
            previous.open(p);
            currentPanel = previous;
        }
    }

    public Collection<? extends InventoryPanel> getPanels() {
        Validate.notNull(panels, "No panels set");
        return Collections.unmodifiableCollection(panels);
    }

    public boolean hasPanels() {
        return panels != null && panels.size() > 0;
    }

    public InventoryPanel getRootPanel() {
        Validate.notNull(panels, "No panels set");
        return panels.stream().findFirst().orElse(null);
    }

    public InventoryPanel find(Predicate<? super InventoryPanel> filter) {
        Validate.notNull(panels, "No panels set");
        return panels.stream().filter(filter).findFirst().orElse(null);
    }

    public void setPanels(InventoryPanel... inventoryPanels) {
        panels = Arrays.asList(inventoryPanels);
        currentPanel = getRootPanel();
    }

    public void setPanels(Collection<InventoryPanel> inventoryPanels) {
        panels = new ArrayList<>(inventoryPanels);
        currentPanel = getRootPanel();
    }

    @Override
    public void update() {
        panels.forEach(InventoryPanel::update);
    }
}
