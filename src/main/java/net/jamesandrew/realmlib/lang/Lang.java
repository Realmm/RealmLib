package net.jamesandrew.realmlib.lang;

import org.bukkit.ChatColor;

public final class Lang {

    private Lang(){}

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String stripColor(String s) {
        return ChatColor.stripColor(s);
    }

}
