package net.jamesandrew.realmlib.inventory.hotbar;

import net.jamesandrew.commons.manager.ManagedHashSet;
import net.jamesandrew.realmlib.inventory.Icon;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class HotBar {

    private List<HotBarIcon> icons = new ArrayList<>();
    private ManagedHashSet<String> uuid = new ManagedHashSet<>();

    public HotBar(HotBarIcon... hotBarIcons) {
        Validate.isTrue(hotBarIcons.length <= 9, "Amount of menu icons in hot bar exceeds 9 (" + hotBarIcons.length + ")");
        icons.addAll(Arrays.asList(hotBarIcons));
        HotBarManager.register(this);
    }

    public boolean isSetToPlayer(Player p) {
        return uuid.isRegistered(p.getUniqueId().toString());
    }

    public void setToPlayer(Player p) {
//        if (HotBarManager.getRegistered().stream().filter(h -> h != this).map(HotBar::getPlayerIds)
//                .anyMatch(s -> s.isRegistered(p.getUniqueId().toString()))) throw new IllegalArgumentException("Player has a hot bar set already");
        String id = p.getUniqueId().toString();
        if (uuid.isRegistered(id)) throw new IllegalArgumentException("Player already has this hot bar set");
        uuid.register(id);
        icons.forEach(i -> i.getManager().register(this));

        for (int x = 0; x < 9; x++) {
            final int i = x;
            if (icons.stream().map(Icon::getSlot).noneMatch(n -> n == i)) p.getInventory().setItem(i, null);
        }

        icons.stream().filter(i -> (i.getSlot() < 9 && i.getSlot() >= 0)).forEach(i -> p.getInventory().setItem(i.getSlot(), i.getItem()));
    }

    public void setIcon(HotBarIcon icon, int slot) {
        icons.stream().filter(i -> i.getSlot() == slot).forEach(i -> i = icon);
    }

    public Collection<? extends HotBarIcon> getIcons() {
        return icons;
    }

    protected ManagedHashSet<String> getPlayerIds() {
        return uuid;
    }

    public void removeFromPlayer(Player p) {
        String id = p.getUniqueId().toString();
        uuid.unregister(id);
        icons.forEach(i -> i.getManager().unregister(this));
        for (int x = 0; x < 9; x++) p.getInventory().setItem(x, null);
    }

    public void update(Player p) {
        icons.forEach(i -> {
            int slot = i.getSlot();
            p.getInventory().setItem(slot, i.getItem());
        });
    }

    public void update() {
        uuid.getRegistered().stream().map(Bukkit::getPlayer).filter(OfflinePlayer::isOnline).forEach(this::update);
    }

}
