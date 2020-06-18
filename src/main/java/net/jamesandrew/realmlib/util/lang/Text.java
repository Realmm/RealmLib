package net.jamesandrew.realmlib.util.lang;

import net.jamesandrew.realmlib.util.placeholder.Placeholder;
import net.jamesandrew.realmlib.util.placeholder.ReplacePattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public abstract class Text {

    private final String s;
    private ChatColor color;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private Placeholder placeholder;
    private String replaced = "";
    private boolean hasBeenReplaced;

    public Text(String s) {
        throw new UnsupportedOperationException("Not yet supported");
//        this.s = s;
    }

    public String getOriginal() {
        return s;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void setColor(org.bukkit.ChatColor color) {
        this.color = ChatColor.getByChar(color.getChar());
    }

    public ChatColor getColor() {
        return color;
    }

    public boolean hasColor() {
        return color != null;
    }

    public void setHoverEvent(HoverEvent e) {
        this.hoverEvent = e;
    }

    public boolean hasHoverEvent() {
        return hoverEvent != null;
    }

    public HoverEvent getHoverEvent() {
        return hoverEvent;
    }

    public void setClickEvent(ClickEvent e) {
        this.clickEvent = e;
    }

    public boolean hasClickEvent() {
        return clickEvent != null;
    }

    public ClickEvent getClickEvent() {
        return clickEvent;
    }

    public void setPlaceholder(ReplacePattern replacePattern) {
        setPlaceholder(replacePattern, getOriginal());
    }

    void setPlaceholder(ReplacePattern replacePattern, String s) {
        this.placeholder = new Placeholder(s).setReplacePattern(replacePattern);
    }

    Placeholder getPlaceholder() {
        return placeholder;
    }

    boolean hasPlaceholder() {
        return getPlaceholder() != null;
    }

    void setReplaced(String s) {
        this.replaced = s;
    }

    String getReplaced() {
        return !replaced.equals("") ? replaced : placeholder == null ? getOriginal() : placeholder.toString();
    }

    void setHasBeenReplaced(boolean b) {
        this.hasBeenReplaced = b;
    }

    boolean hasBeenReplaced() {
        return hasBeenReplaced;
    }



}
