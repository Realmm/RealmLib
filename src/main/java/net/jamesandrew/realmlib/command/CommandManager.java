package net.jamesandrew.realmlib.command;

import net.jamesandrew.commons.exception.Validator;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CommandManager implements CommandExecutor, TabCompleter {

    private final Set<BaseCommand> baseCommands = new HashSet<>();
    private static final CommandManager manager = new CommandManager();

    private CommandManager(){}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (baseCommands.size() <= 0) return true;
        String[] command = new String[args.length + 1];
        command[0] = cmd.getName();
        for (int i = 0; i < args.length; i++) command[i+1] = args[i];
        BaseCommand baseCommand = baseCommands.stream().filter(b -> b.getName().equalsIgnoreCase(cmd.getName())).findFirst().orElse(null);
        if (baseCommand == null) return true;
        return baseCommand.callAppropriateCommand(sender, command);
//        baseCommands.forEach(c -> c.callAppropriateCommand(sender, command));
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

    public static CommandManager get() {
        return manager;
    }

    public static void register(BaseCommand baseCommand) {
        String command = baseCommand.getNode();
        Validate.isTrue(!manager.containsCommand(command), "Command \'" + command + "\' is already registered");
        Validate.isTrue(!manager.containsBaseCommand(baseCommand), "Command \'" + command + "\' is already registered");
        manager.addBaseCommand(baseCommand);

        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);
                CommandMap commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
                commandMap.register(baseCommand.getNode(), baseCommand);
                f.setAccessible(false);
            }

        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
