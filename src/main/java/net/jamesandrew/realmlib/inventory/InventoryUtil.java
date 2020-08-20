package net.jamesandrew.realmlib.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class InventoryUtil {

    private InventoryUtil(){}

    public static boolean canAdd(Player p, ItemStack... itemStacks) {
        return canAdd(p, new HashSet<>(Arrays.asList(itemStacks)));
    }

    public static boolean canAdd(Player p, Material material, int amount, boolean divideByMaxStackSize) {
        int freeSlots = 0;
        for (ItemStack is : p.getInventory().getContents()) {
            if (is != null) continue;
            freeSlots++;
        }

        int slotsNeeded = (int) Math.ceil((float) amount / (divideByMaxStackSize ? material.getMaxStackSize() : 64));

        return slotsNeeded <= freeSlots;
    }

    public static boolean canAdd(Player p, Material material, int amount) {
        return canAdd(p, material, amount, true);
    }

    public static boolean canAdd(Player p, Collection<ItemStack> items, boolean divideByMaxStackSize) {
        int freeSlots = 0;
        for (ItemStack is : p.getInventory().getContents()) {
            if (is != null) continue;
            freeSlots++;
        }

        Map<Material, Integer> map = new HashMap<>();

        for (ItemStack item : items) {
            map.computeIfPresent(item.getType(), (m, i) -> i += item.getAmount());
            map.putIfAbsent(item.getType(), item.getAmount());
        }

        int[] slotsNeeded = {0};

        map.forEach((m, i) -> slotsNeeded[0] += (int) Math.ceil((float) i / (divideByMaxStackSize ? m.getMaxStackSize() : 64)));

        return slotsNeeded[0] <= freeSlots;
    }

    public static boolean canAdd(Player p, Collection<ItemStack> items) {
        return canAdd(p, items, true);
    }

    public static boolean containsAtLeast(Inventory inventory, ItemStack i, int amount) {
        if (inventory.containsAtLeast(i, amount)) return true;

        Map<ItemStack, Integer> map = new HashMap<>();

        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;
            for (int x = 0; x < item.getAmount(); x++) {
                if (map.entrySet().stream().anyMatch(e -> e.getKey().isSimilar(item))) {
                    ItemStack itemStack = map.keySet().stream().filter(it -> it.isSimilar(item)).findFirst().orElseThrow(() -> new IllegalArgumentException("No similar ItemStacks"));
                    map.put(itemStack, map.get(itemStack) + 1);
                } else {
                    map.put(item, 1);
                }
            }
        }

        ItemStack itemStack = map.keySet().stream().filter(it -> it.isSimilar(i)).findFirst().orElse(null);

        return itemStack != null && map.get(itemStack) >= amount;
    }

}
