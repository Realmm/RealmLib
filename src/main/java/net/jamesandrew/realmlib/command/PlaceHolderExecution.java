package net.jamesandrew.realmlib.command;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface PlaceHolderExecution {

    String executePlaceHolder(CommandSender sender, String[] args);

}
