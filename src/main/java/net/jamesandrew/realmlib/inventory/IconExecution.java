package net.jamesandrew.realmlib.inventory;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface IconExecution {

    void onExec(Player p, Icon icon);

}
