package net.jamesandrew.realmlib.concurrency;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import org.bukkit.plugin.Plugin;

public final class Task {

    private static TaskChainFactory taskChainFactory;

    private Task(){}

    public static void setTaskChainFactory(Plugin plugin) {
        taskChainFactory = BukkitTaskChainFactory.create(plugin);
    }

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

}
