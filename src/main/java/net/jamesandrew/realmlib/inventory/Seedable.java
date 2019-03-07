package net.jamesandrew.realmlib.inventory;

import org.bukkit.ChatColor;

import java.util.concurrent.ThreadLocalRandom;

public interface Seedable {

    default String generateInvisibleSeed(int length) {
        StringBuilder sb = new StringBuilder();
        ChatColor[] colors = ChatColor.values();

        for (int i = 0; i < length; i++) {
            sb.append(colors[ThreadLocalRandom.current().nextInt(colors.length)]);
        }
        return sb.toString();
    }

    String getSeed();

}
