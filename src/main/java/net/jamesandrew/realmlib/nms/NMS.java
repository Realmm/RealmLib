package net.jamesandrew.realmlib.nms;

import org.bukkit.Bukkit;

public final class NMS {

    private NMS(){}

    public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + getVersion() + "." + name);
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

}
