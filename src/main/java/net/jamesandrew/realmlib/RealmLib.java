package net.jamesandrew.realmlib;

import net.jamesandrew.commons.logging.Logger;
import net.jamesandrew.commons.reflection.Reflect;
import net.jamesandrew.realmlib.inventory.InventoryListener;
import net.jamesandrew.realmlib.inventory.hotbar.HotBarBlockPlaceListener;
import net.jamesandrew.realmlib.inventory.hotbar.HotBarIconMoveListener;
import net.jamesandrew.realmlib.inventory.hotbar.HotBarInteractListener;
import net.jamesandrew.realmlib.inventory.hotbar.IconDropListener;
import net.jamesandrew.realmlib.util.nms.MaxStackSizeInventoryClickListener;
import net.jamesandrew.realmlib.util.concurrency.Task;
import net.jamesandrew.realmlib.util.register.Register;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

public abstract class RealmLib extends JavaPlugin {

    private final String toPrint = "\nDeveloped by Realm\nSpigot profile: https://www.spigotmc.org/members/realm.86904/\n";
    private boolean enable = true;

    @Override
    public void onEnable() {
        onPreStart();
        printEnable(getName());

        Logger.setLogger(getLogger() == null ? java.util.logging.Logger.getLogger("RealmLibLogger") : getLogger());

        Task.setTaskChainFactory(this);

        registerListers();

        onStart();
    }

    @Override
    public void onDisable() {
        printDisable(getName());
        onEnd();
    }

    protected abstract void onStart();

    protected abstract void onEnd();

    protected void onPreStart() {}

    public static JavaPlugin get() {
        return JavaPlugin.getPlugin(RealmLib.class);
    }

    protected void setShouldEnablePrint(boolean enable) {
        this.enable = enable;
    }

    private void printEnable(String name) {
        if (!enable) return;
        try {
            Reflect.getClassesExtending(RealmLib.class).forEach(c ->
                    Logger.log("\n\nInitialising... " + c.getSimpleName() + " (" + c.getCanonicalName() + ")" + toPrint));
        } catch (NoSuchMethodError e) {
            Logger.log("\n\nInitialising... " + name + " (" + getClass().getCanonicalName() + ")" + toPrint);
        }

    }

    private void printDisable(String name) {
        if (!enable) return;
        try {
            Reflect.getClassesExtending(RealmLib.class).forEach(c -> Logger.log("\n\nClosing... " + c.getSimpleName() + " (" + c.getCanonicalName() + ")" + toPrint));
        } catch (NoSuchMethodError e) {
            Logger.log("\n\nClosing... " + name + " (" + getClass().getCanonicalName() + ")" + toPrint);
        }

    }

    private void registerListers() {
        Stream.of(
                new IconDropListener(),
                new InventoryListener(),
                new HotBarInteractListener(),
                new HotBarIconMoveListener(),
                new HotBarBlockPlaceListener(),
                new MaxStackSizeInventoryClickListener()
        ).forEach(l -> Register.listener(l, this));
    }

}
