package net.jamesandrew.realmlib.inventory;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

public final class InventoryListener implements Listener {

    private Collection<? extends Paginator> paginators = PaginatorManager.getRegistered();

    @EventHandler
    public void on(InventoryClickEvent e) {
        if (e.getWhoClicked().getType() != EntityType.PLAYER) return;
        if (e.getCurrentItem() == null) return;

        Player player = (Player) e.getWhoClicked();

        if (paginators.size() <= 0) return;

        paginators.stream().filter(Paginator::hasPanels).forEach(p ->
            p.getPanels().forEach(panel -> {
                String currentTitle = e.getView().getTitle();
                if (!panel.getSeededTitle().equals(currentTitle)) return;
                e.setCancelled(true);
                panel.getIcons().stream().filter(Objects::nonNull)
                        .filter(icon -> e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName())
                        .filter(icon -> icon.getSlot() == e.getSlot())
                        .filter(icon -> icon.getSeededTitle().equals(e.getCurrentItem().getItemMeta().getDisplayName()))
                        .findFirst().ifPresent(icon -> {
                    if (icon.getSlot() != e.getSlot()) return;
                    if (icon.requiresPermission()) {
                        if (!icon.hasPermission(player)) return;
                        if (!icon.hasExecution()) return;
                        icon.click(player);
                    } else {
                        if (!icon.hasExecution()) return;
                        icon.click(player);
                    }
                });
            })
        );

    }

}
