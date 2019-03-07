package net.jamesandrew.realmlib.command;

import org.bukkit.command.CommandSender;

public class SubCommand extends CommandNode {

    private String placeHolder;
    private PlaceHolderExecution placeHolderExecution;

    public SubCommand(String subCommand, CommandExecution execution) {
        super(subCommand, execution);
    }

    public SubCommand(String subCommand) {
        super(subCommand, null);
    }

    public static SubCommand empty(String name) {
        return new SubCommand(name);
    }

    void runPlaceHolderExecution(CommandSender sender, String[] args) {
        String s = placeHolderExecution.executePlaceHolder(sender, args);
        setPlaceHolder(s);
    }

    public void setPlaceHolder(PlaceHolderExecution placeHolderExecution) {
        this.placeHolderExecution = placeHolderExecution;
    }

    <T> void setPlaceHolder(T obj) {
        this.placeHolder = obj.toString();
    }

    boolean hasPlaceHolderExecution() {
        return placeHolderExecution != null;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void removePlaceholder() {
        this.placeHolder = null;
    }

    public boolean hasPlaceHolder() {
        return placeHolder != null;
    }

}
