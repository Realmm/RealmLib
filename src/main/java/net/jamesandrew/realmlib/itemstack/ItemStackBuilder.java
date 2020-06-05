package net.jamesandrew.realmlib.itemstack;

import net.jamesandrew.commons.logging.Logger;
import net.jamesandrew.realmlib.lang.Lang;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.*;

public class ItemStackBuilder {

    private final Material m;
    private int amount;
    private DyeColor dyeColor;
    private List<String> lore;
    private String name;
    private final Material[] colored = {GLASS, INK_SACK, STAINED_GLASS_PANE, STAINED_GLASS, STAINED_CLAY, WOOL};

    public ItemStackBuilder(Material material) {
        this.m = material;
    }

    public ItemStackBuilder(Material material, int amount) {
        this(material);
        this.amount = amount;
    }

    public ItemStackBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStackBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemStackBuilder setLore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public ItemStackBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemStackBuilder setColor(DyeColor dyeColor) {
        if (!isColorable()) {
            Logger.warn("Attempted to set color on a non-colorable material, color not set");
            return this;
        }
        this.dyeColor = dyeColor;
        return this;
    }

    private boolean isColorable() {
        return Arrays.stream(colored).anyMatch(c -> c == m);
    }

    public ItemStack build() {
        ItemStack i = new ItemStack(m, amount);
        i.setAmount(amount);

        if (isColorable()) {
            if (m == INK_SACK) {
                i.setDurability(dyeColor.getDyeData());
            } else i.setDurability(dyeColor.getWoolData());
        }

        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(Lang.color(name));
        lore.replaceAll(Lang::color);
        meta.setLore(lore);
        i.setItemMeta(meta);

        return i;
    }
}
