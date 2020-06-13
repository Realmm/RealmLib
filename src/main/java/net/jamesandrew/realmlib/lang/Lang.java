package net.jamesandrew.realmlib.lang;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public final class Lang {

    private Lang(){}

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String stripColor(String s) {
        return ChatColor.stripColor(s);
    }

    public static String translateString(FileConfiguration file, String path) {
        StringBuilder sb = new StringBuilder();

        if (file.isList(path)) {
            List<String> list = file.getStringList(path);
            for (int i = 0; i < list.size(); i++) {
                sb.append(color(list.get(i)));
                if (i != list.size() - 1) sb.append("\n");
            }
        } else sb.append(color(file.getString(path)));

        return sb.toString();
    }

    public static TextComponent toTextComponent(String s, HoverEvent h, ClickEvent c) {
        TextComponent t = new TextComponent(s);
        t.setClickEvent(c);
        t.setHoverEvent(h);
        return t;
    }

    public static TextComponent toClickableText(String s, String cmd, String hoverMsg) {
        return toTextComponent(s,
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMsg).create()),
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd)
                );
    }

}
