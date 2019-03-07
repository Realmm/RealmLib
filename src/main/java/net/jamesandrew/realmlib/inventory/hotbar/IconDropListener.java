package net.jamesandrew.realmlib.inventory.hotbar;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class IconDropListener implements Listener {

    @EventHandler
    public void on(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        String id = p.getUniqueId().toString();

        boolean hasHotBar = HotBarManager.getRegistered().stream().map(HotBar::getPlayerIds).anyMatch(s -> s.isRegistered(id));

        if (!hasHotBar) return;

        ItemStack hand = e.getItemDrop().getItemStack(); // <--1.8.8 compatibility

        if (hand == null || hand.getType() == Material.AIR) return;

        HotBar hotBar = HotBarManager.getRegistered().stream().filter(h -> h.getPlayerIds().isRegistered(id)).findFirst().orElseThrow(NullPointerException::new);

        int slotDropped = p.getInventory().getHeldItemSlot();

        Optional<? extends HotBarIcon> optional = hotBar.getIcons().stream().filter(i -> i.getSlot() == slotDropped).filter(i -> !i.canRemove()).findFirst();

        if (optional.isPresent()) e.setCancelled(true);
    }

}
