package net.jamesandrew.realmlib.inventory.hotbar;

import net.jamesandrew.commons.manager.ManagedHashSet;

import java.util.Collection;

final class HotBarManager {

    private HotBarManager(){}

    private static ManagedHashSet<HotBar> hotBars = new ManagedHashSet<>();

    protected static void register(HotBar hotBar) {
        hotBars.register(hotBar);
    }

    protected static void unRegister(HotBar hotBar) {
        hotBars.unregister(hotBar);
    }

    protected static boolean isRegistered(HotBar hotBar) {
        return hotBars.isRegistered(hotBar);
    }

    protected static Collection<? extends HotBar> getRegistered() {
        return hotBars.getRegistered();
    }

}
