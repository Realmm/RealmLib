package net.jamesandrew.realmlib.register;

import net.jamesandrew.realmlib.RealmLib;
import net.jamesandrew.realmlib.command.BaseCommand;
import net.jamesandrew.realmlib.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class Register {

    private Register(){}

    public static void command(CommandExecutor commandExecutor, String command) {
        PluginCommand c = RealmLib.get().getCommand(command);
        if (c == null) throw new NullPointerException("Command '" + command + "' is not registered in plugin.yml");
        c.setExecutor(commandExecutor);
    }

    public static void listener(Listener listener, Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public static void baseCommand(BaseCommand baseCommand) {
        CommandManager.register(baseCommand);
    }

    public static void callEvent(Event e) {
        Bukkit.getPluginManager().callEvent(e);
    }

}
