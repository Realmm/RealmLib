package net.jamesandrew.realmlib.item;

import net.jamesandrew.realmlib.RealmLib;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Item {

    private static final Map<Material, Integer> vanillaStackSizes = new HashMap<>();

    public static void setMaxStackSize(Material m, int amount) {
        if (!vanillaStackSizes.containsKey(m)) vanillaStackSizes.put(m, m.getMaxStackSize());
        try {
            String packageVersion = RealmLib.get().getServer().getClass().getPackage().getName().split("\\.")[3];
            Class<?> magicClass = Class.forName("org.bukkit.craftbukkit." + packageVersion + ".util.CraftMagicNumbers");
            Method method = magicClass.getDeclaredMethod("getItem", Material.class);
            Object item = method.invoke(null, m);
            Class<?> itemClass = Class.forName("net.minecraft.server." + packageVersion + ".Item");
            Field field = itemClass.getDeclaredField("maxStackSize");
            field.setAccessible(true);
            field.setInt(item, amount);
            Field mf = Material.class.getDeclaredField("maxStack");
            mf.setAccessible(true);
            mf.setInt(m, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetMaxStackSize(Material m) {
        if (!hasModifiedStackSize(m)) return;
        setMaxStackSize(m, vanillaStackSizes.get(m));
    }

    public static boolean hasModifiedStackSize(Material m) {
        return vanillaStackSizes.containsKey(m);
    }

}
