package net.jamesandrew.realmlib.util.nms;

import net.jamesandrew.realmlib.RealmLib;
import net.jamesandrew.realmlib.util.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MaxStackSizeInventoryClickListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(InventoryClickEvent e) {
        if (e.isCancelled() || !(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null || !Item.hasModifiedStackSize(e.getCurrentItem().getType())) return;

        if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
            Bukkit.getScheduler().runTask(RealmLib.get(), p::updateInventory);
        }
    }

}
