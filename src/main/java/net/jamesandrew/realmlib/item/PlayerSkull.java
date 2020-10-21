package net.jamesandrew.realmlib.item;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerSkull extends ItemStack {

    public PlayerSkull(OfflinePlayer p) {
        super(Material.LEGACY_SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) getItemMeta();
        meta.setOwner(p.getName());
        setItemMeta(meta);
    }

    public PlayerSkull(Player p) {
        super(Material.LEGACY_SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) getItemMeta();
        meta.setOwner(p.getName());
        setItemMeta(meta);
    }

}
