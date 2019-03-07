package net.jamesandrew.realmlib.inventory.hotbar;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class HotBarIconMoveListener implements Listener {

    @EventHandler
    public void on(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        String id = p.getUniqueId().toString();

        boolean hasHotBar = HotBarManager.getRegistered().stream().map(HotBar::getPlayerIds).anyMatch(s -> s.isRegistered(id));

        if (!hasHotBar) return;

        ItemStack clicked = e.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) return;

        HotBar hotBar = HotBarManager.getRegistered().stream().filter(h -> h.getPlayerIds().isRegistered(id)).findFirst().orElseThrow(NullPointerException::new);

        int slotClicked = e.getSlot();

        Optional<? extends HotBarIcon> optional = hotBar.getIcons().stream().filter(i -> i.getSlot() == slotClicked).filter(i -> !i.canRemove()).findFirst();

        if (optional.isPresent()) e.setCancelled(true);
    }

}
