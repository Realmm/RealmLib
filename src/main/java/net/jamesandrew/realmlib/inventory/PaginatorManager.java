package net.jamesandrew.realmlib.inventory;

import net.jamesandrew.commons.manager.ManagedHashSet;

import java.util.Collection;

public final class PaginatorManager {

    private PaginatorManager(){}

    private static ManagedHashSet<Paginator> paginators = new ManagedHashSet<>();

    protected static void register(Paginator paginator) {
        paginators.register(paginator);
    }

    protected static void unRegister(Paginator paginator) {
        paginators.unregister(paginator);
    }

    protected static boolean isRegistered(Paginator paginator) {
        return paginators.isRegistered(paginator);
    }

    protected static Collection<? extends Paginator> getRegistered() {
        return paginators.getRegistered();
    }

}
