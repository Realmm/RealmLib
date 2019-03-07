package net.jamesandrew.realmlib.itemstack;

import net.jamesandrew.commons.logging.Logger;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.*;

@Deprecated
public class ItemStackBuilder {

    private Material m;
    private int amount;
    private DyeColor dyeColor;
    private List<String> lore;
    private final Material[] colored = {GLASS, INK_SACK, STAINED_GLASS_PANE, STAINED_GLASS, STAINED_CLAY, WOOL, FIREWORK};

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
        ItemStack i = null;

        i.setAmount(amount);
//        if (isColorable())
        return i;
    }

}
