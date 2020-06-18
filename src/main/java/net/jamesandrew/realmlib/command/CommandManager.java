package net.jamesandrew.realmlib.command;

import net.jamesandrew.commons.exception.Validator;
import net.jamesandrew.realmlib.register.Register;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CommandManager implements CommandExecutor, TabCompleter {

    private Set<BaseCommand> baseCommands = new HashSet<>();
    private static CommandManager manager = new CommandManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (baseCommands.size() <= 0) return true;
        String[] command = new String[args.length + 1];
        command[0] = cmd.getName();
        for (int i = 0; i < args.length; i++) command[i+1] = args[i];
        baseCommands.forEach(c -> c.callAppropriateCommand(sender, command));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        return null;
    }

    void addBaseCommand(BaseCommand baseCommand) {
        Validator.notContains(baseCommand, baseCommands);
        baseCommands.add(baseCommand);
    }

    void removeBaseCommand(BaseCommand baseCommand) {
        baseCommands.remove(baseCommand);
    }

    boolean containsBaseCommand(BaseCommand baseCommand) {
        return baseCommands.contains(baseCommand);
    }

    boolean containsCommand(String command) {
        return baseCommands.stream().map(CommandNode::getNode).anyMatch(c -> c.equalsIgnoreCase(command));
    }

    public CommandManager get() {
        return manager;
    }

    public static void register(BaseCommand baseCommand) {
        String command = baseCommand.getNode();
        Validate.isTrue(!manager.containsCommand(command), "Command \'" + command + "\' is already registered");
        Validate.isTrue(!manager.containsBaseCommand(baseCommand), "Command \'" + command + "\' is already registered");
        manager.addBaseCommand(baseCommand);
        Register.command(manager, command);
    }

}
