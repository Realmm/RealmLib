package net.jamesandrew.realmlib.scoreboard;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface LineExecution {

    String execute(Player p);

}
