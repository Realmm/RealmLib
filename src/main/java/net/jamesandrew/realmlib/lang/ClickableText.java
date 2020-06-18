package net.jamesandrew.realmlib.lang;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ClickableText extends Text {

    private final String cmd, hoverMsg;

    public ClickableText(String s, String cmd, String hoverMsg) {
        super(s);
        this.cmd = cmd;
        this.hoverMsg = hoverMsg;
        setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + cmd));
        setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverMsg.replace("&", "\\u00a7"))));
    }

    public String getCommand() {
        return cmd;
    }

    public String getHoverMessage() {
        return hoverMsg;
    }

}
