package net.jamesandrew.realmlib.inventory;

import net.jamesandrew.commons.exception.Validator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MenuIcon extends Icon {

    private Set<InventoryPanel> panels = new HashSet<>();
    private boolean close = true;
    private int slot = -1;

    public MenuIcon(ItemStack item, IconExecution execution) {
        super(item, execution);
    }

    public MenuIcon(ItemStack item, IconExecution execution, boolean close) {
        this(item, execution);
        this.close = close;
    }

    public MenuIcon(ItemStack item) {
        this(item, null);
    }

    public MenuIcon(ItemStack item, boolean close) {
        this(item, null, close);
    }

    @Override
    public MenuIcon clone() {
        MenuIcon icon = new MenuIcon(getItem(), getExecution(), close);
        icon.setTitle(getUnseededTitle());
        icon.setSlot(slot);
        panels.forEach(icon::addPanel);
        icon.setLore(getLore());
        icon.setExecution(getExecution());
        return icon;
    }

    protected void click(Player p) {
        if (close) p.closeInventory();
        super.execute(p);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof MenuIcon)) return false;

        final MenuIcon other = (MenuIcon) obj;

        if (!getSeed().equals(other.getSeed())) return false;
        return Objects.equals(getItem(), other.getItem());
    }

    protected void addPanel(InventoryPanel panel) {
        Validator.notContains(panel, panels);
        panels.add(panel);
    }

    protected void removePanel(InventoryPanel panel) {
        panels.remove(panel);
    }

    protected boolean containsPanel(InventoryPanel panel) {
        return panels.contains(panel);
    }

    protected Collection<? extends InventoryPanel> getPanels() {
        return Collections.unmodifiableCollection(panels);
    }

    @Override
    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public void update() {
        panels.forEach(InventoryPanel::update);
    }

}
