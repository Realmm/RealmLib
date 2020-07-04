package net.jamesandrew.realmlib.command;

import org.bukkit.command.CommandSender;

import java.util.Set;

public interface CommandNode extends CommandExecutionable {

    String getNode();

    void execute(CommandSender sender, String[] args);

    @Override
    void setExecution(CommandExecution commandExecution);

    @Override
    CommandExecution getExecution();

    @Override
    boolean isExecutable();

    boolean isChild(String s);

    SubCommand getChild(String s);

    CommandNode getParent(int index);

    CommandNode getParent(String node);

    boolean isParent(int index);

    boolean isParent(String node);

    Set<SubCommand> getChildren();

    void addSubCommands(SubCommand... subCommands);

    boolean hasChildren();

    int getAmountOfChildren();

}
