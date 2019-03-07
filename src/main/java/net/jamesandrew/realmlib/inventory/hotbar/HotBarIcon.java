package net.jamesandrew.realmlib.inventory.hotbar;

import net.jamesandrew.commons.manager.ManagedHashSet;
import net.jamesandrew.realmlib.inventory.Icon;
import net.jamesandrew.realmlib.inventory.IconExecution;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HotBarIcon extends Icon {

    private int slot = -1;
    private boolean canRemove = false;
    private ManagedHashSet<HotBar> hotBarManagedHashSet = new ManagedHashSet<>();

    public HotBarIcon(ItemStack item, IconExecution execution) {
        super(item, execution);
    }

    public HotBarIcon(ItemStack item) {
        super(item);
    }

    @Override
    public HotBarIcon clone() {
        try {
            return (HotBarIcon) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    protected ManagedHashSet<HotBar> getManager() {
        return hotBarManagedHashSet;
    }

    @Override
    public void update() {
        if (!getManager().isEmpty()) getManager().getRegistered().forEach(HotBar::update);
    }

    public void update(Player p) {
        if (!getManager().isEmpty()) getManager().getRegistered().forEach(h -> h.update(p));
    }

    public boolean canRemove() {
        return canRemove;
    }

    public void setCanRemove(boolean canRemove) {
        this.canRemove = canRemove;
    }

}
