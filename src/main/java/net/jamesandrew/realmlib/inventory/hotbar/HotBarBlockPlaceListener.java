package net.jamesandrew.realmlib.inventory.hotbar;

import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public final class HotBarBlockPlaceListener implements Listener {

    @EventHandler
    public void on(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        String id = p.getUniqueId().toString();

        boolean hasHotBar = HotBarManager.getRegistered().stream().map(HotBar::getPlayerIds).anyMatch(s -> s.isRegistered(id));

        if (!hasHotBar) return;

        ItemStack clicked = e.getItemInHand();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        HotBar hotBar = HotBarManager.getRegistered().stream().filter(h -> h.getPlayerIds().isRegistered(id)).findFirst().orElseThrow(NullPointerException::new);

        int slot = p.getInventory().getHeldItemSlot();
        Optional<? extends HotBarIcon> optional = hotBar.getIcons().stream().filter(i -> i.getSlot() == slot).filter(i -> !i.canRemove()).findFirst();

        if (optional.isPresent()) e.setCancelled(true);
    }

}
