package net.jamesandrew.realmlib.nbt;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public final class NBT {

    private NBT(){}

    public static ItemStack addTag(ItemStack i, String key, String val) {
        NBTItem nbtItem = new NBTItem(i);
        nbtItem.setString(key, val);
        return nbtItem.getItem();
    }

    public static void removeTag(ItemStack i, String key) {
        new NBTItem(i).removeKey(key);
    }

    public static String getTag(ItemStack i, String key) {
        if (!hasTag(i, key)) return "";
        return new NBTItem(i).getString(key);
    }

    public static boolean hasTag(ItemStack i, String key) {
        return new NBTItem(i).hasKey(key);
    }

    public static Collection<String> getTags(ItemStack i) {
        return new NBTItem(i).getKeys();
    }

}
