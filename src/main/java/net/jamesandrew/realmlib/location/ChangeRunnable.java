package net.jamesandrew.realmlib.location;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

public class ChangeRunnable extends BukkitRunnable {

    private final Changer c;
    private final Queue<Change> changes;
    private boolean done = false;

    public ChangeRunnable(final Changer changer) {
        this.c = changer;
        this.changes = new ArrayDeque<>(changer.getChanges());
    }

    @Override
    public void run() {
        if (!changes.isEmpty()) {
            for (int i = 0; i < c.getMaxChanges(); i++) {
                if (changes.isEmpty()) {
                    done = true;
                    break;
                }
                changes.poll().change(c);
            }
        }

        if (!done) return;

        cancel();
        c.cancel();
        if (c.getCallback() != null) c.getCallback().run();
    }

    public Collection<Change> getChanges() {
        return changes;
    }



}
