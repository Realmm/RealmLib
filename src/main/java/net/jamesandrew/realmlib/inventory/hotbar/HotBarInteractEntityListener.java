package net.jamesandrew.realmlib.inventory.hotbar;

import net.jamesandrew.commons.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class HotBarInteractEntityListener implements Listener {

    @EventHandler
    public void on(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        String id = p.getUniqueId().toString();

        boolean hasHotBar = HotBarManager.getRegistered().stream().map(HotBar::getPlayerIds).anyMatch(s -> s.isRegistered(id));

        if (!hasHotBar) return;

        ItemStack hand = p.getItemInHand();
        if (hand == null || hand.getType() == Material.AIR) return;

        HotBar hotBar = HotBarManager.getRegistered().stream().filter(h -> h.getPlayerIds().isRegistered(id)).findFirst().orElseThrow(NullPointerException::new);

        int slotClicked = p.getInventory().getHeldItemSlot();
        hotBar.getIcons().stream().filter(i -> i.getSlot() == slotClicked).findFirst().ifPresent(i -> {
            i.setClickedEntity(e.getRightClicked());
            i.execute(p);
        });
    }

}
