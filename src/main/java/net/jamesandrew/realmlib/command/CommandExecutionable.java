package net.jamesandrew.realmlib.command;

public interface CommandExecutionable {

    void setExecution(CommandExecution commandExecution);

    CommandExecution getExecution();

    boolean isExecutable();

}
