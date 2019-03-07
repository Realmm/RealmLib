package net.jamesandrew.realmlib.command;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandExecution {

    void onCommand(CommandSender sender, String[] args);

}
