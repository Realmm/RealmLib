package net.jamesandrew.realmlib.inventory.hotbar;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class HotBarInteractListener implements Listener {

    @EventHandler
    public void on(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        String id = p.getUniqueId().toString();

        boolean hasHotBar = HotBarManager.getRegistered().stream().map(HotBar::getPlayerIds).anyMatch(s -> s.isRegistered(id));

        if (!hasHotBar) return;

//        ItemStack hand = p.getInventory().getItemInMainHand(); <-- uncomment to enable 1.11+ compatibility
//
//        if (e.getHand() != EquipmentSlot.HAND) return;
        ItemStack hand = p.getItemInHand(); // <--1.8.8 compatibility
        if (hand == null || hand.getType() == Material.AIR) return;

        HotBar hotBar = HotBarManager.getRegistered().stream().filter(h -> h.getPlayerIds().isRegistered(id)).findFirst().orElseThrow(NullPointerException::new);

        int slotClicked = p.getInventory().getHeldItemSlot();
        hotBar.getIcons().stream().filter(i -> i.getSlot() == slotClicked).findFirst().ifPresent(i -> i.execute(p));
    }

}
