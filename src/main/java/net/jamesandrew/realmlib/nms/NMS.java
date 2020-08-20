package net.jamesandrew.realmlib.nms;

import org.bukkit.Bukkit;

import java.lang.reflect.Method;

public final class NMS {

    private NMS(){}

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static Class<?> getNMSClass(String nmsClass) {
        try {
            return Class.forName(String.format("net.minecraft.server." + getVersion() + "%s", nmsClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getNMSMethod(String nmsClass, String method) {
        Class<?> clazz = getNMSClass(nmsClass);
        try {
            return clazz.getMethod(method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

}
