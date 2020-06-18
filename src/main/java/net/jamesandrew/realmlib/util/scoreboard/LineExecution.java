package net.jamesandrew.realmlib.util.scoreboard;

import org.bukkit.entity.Player;

/**
 * This should be considered a line on a scoreboard
 * This is to be able to make {@link Player} specific lines
 */
@FunctionalInterface
public interface LineExecution {

    String execute(Player p);

}
