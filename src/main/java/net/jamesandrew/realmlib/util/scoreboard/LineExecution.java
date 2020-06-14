package net.jamesandrew.realmlib.util.scoreboard;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface LineExecution {

    String execute(Player p);

}
