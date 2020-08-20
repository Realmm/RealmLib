package net.jamesandrew.realmlib.location;

import net.jamesandrew.realmlib.RealmLib;
import org.bukkit.World;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Changer {

    private final World world;
    private long startTime;
    private int tick, total, maxChanges = 100;
    private Runnable callback;
    private ChangeRunnable runnable;
    private final Queue<Change> changes = new ArrayDeque<>();

    public Changer(World world) {
        this.world = world;
    }

    public Changer setBlockChanges(Queue<Change> queue) {
        this.changes.clear();
        this.changes.addAll(queue);
        return this;
    }

    public Changer addChange(Change c) {
        changes.add(c);
        return this;
    }

    public Changer removeChange(Change c) {
        changes.remove(c);
        return this;
    }

    public Collection<Change> getChanges() {
        return Collections.unmodifiableCollection(changes);
    }

    public World getWorld() {
        return world;
    }

    public Changer setTick(int tick) {
        this.tick = tick;
        return this;
    }

    public int getTick() {
        return tick;
    }

    public int getMaxChanges() {
        return maxChanges;
    }

    public Changer setMaxChanges(int maxChanges) {
        this.maxChanges = maxChanges;
        return this;
    }

    public double getApproximateSecondsLeft() {
        if (!isStarted()) return 0;
        long duration = System.currentTimeMillis() - startTime;
        double percentUsed = 100 - getPercentageLeft();
        return TimeUnit.MILLISECONDS.toSeconds((long) (duration / percentUsed) * 100) * (getPercentageLeft() / 100);
    }

    public double getPercentageLeft() {
        return total == 0 ? 0 : Math.max(runnable.getChanges().size() / (double) total, 0) * 100;
    }
    public Changer callback(Runnable callback) {
        this.callback = callback;
        return this;
    }

    public Runnable getCallback() {
        return callback;
    }

    protected ChangeRunnable getRunnable() {
        return runnable;
    }

    public void start(ChangeRunnable runnable) {
        if (tick <= 0) throw new IllegalArgumentException("Tick rate not set");
        this.total = changes.size();
        if (this.runnable != null) this.runnable.cancel();
        this.runnable = runnable == null ? new ChangeRunnable(this) : runnable;
        this.runnable.runTaskTimer(RealmLib.get(), 0, tick);
        this.startTime = System.currentTimeMillis();
    }

    public void start() {
        start(null);
    }

    public void cancel() {
        this.runnable.cancel();
        this.runnable = null;
        this.startTime = 0;
    }

    public boolean isStarted() {
        return runnable != null;
    }

}
