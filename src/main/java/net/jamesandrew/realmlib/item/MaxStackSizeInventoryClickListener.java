package net.jamesandrew.realmlib.item;

import net.jamesandrew.realmlib.RealmLib;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MaxStackSizeInventoryClickListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(InventoryClickEvent e) {
        if (e.isCancelled() || !(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player) e.getWhoClicked();

        ItemStack i = e.getCurrentItem() == null ? e.getCursor() : e.getCurrentItem();

        if (i == null || !Item.hasModifiedStackSize(i.getType())) return;

        if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
            Bukkit.getScheduler().runTask(RealmLib.get(), p::updateInventory);
        }
    }

}
