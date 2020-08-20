package net.jamesandrew.realmlib.shapes;

import net.jamesandrew.realmlib.location.BlockVector;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Cuboid implements Shape, Parallelogram {

    private Location l1, l2;

    private Collection<BlockVector> blockVectors;
    private Collection<Block> blocks;

    public Cuboid(Location l1, Location l2) {
        if (l1.getWorld() != l2.getWorld()) throw new IllegalArgumentException("Must be in same world to create bounding box for shape");
        this.l1 = l1;
        this.l2 = l2;
    }

    protected Cuboid(){}

    @Override
    public boolean contains(Location loc) {
        BlockVector minV = minVector();
        BlockVector maxV = maxVector();

        return loc.getX() >= minV.getX() && loc.getX() <= maxV.getX() && loc.getY() >= minV.getY() && loc.getY() <= maxV.getY() && loc.getZ() >= minV.getZ() && loc.getZ() <= maxV.getZ();
    }

//    @Override
//    public CompletableFuture<Void> load(boolean loadBlocks) {
//        return CompletableFuture.runAsync(() -> {
//            Logger.debug("getting block vectors");
//            this.blockVectors = getBlockVectors();
//            Logger.debug("got block vectors null??: " + (this.blockVectors == null));
//            Logger.debug("null? " + (getBlockVectors() == null));
//            if (loadBlocks) this.blocks = getBlocks();
//            altered = false;
//        });
//    }

    /**
     * Thread safe alternative for {@code getBlocks()}
     * @return A collection of {@link BlockVector} this cuboid has
     */
    @Override
    public Collection<BlockVector> getBlockVectors() {
//        Logger.debug("altered: " + altered + " null? " + (blockVectors == null));
        if (blockVectors != null) return blockVectors;
        BlockVector minV = minVector();
        BlockVector maxV = maxVector();
        Set<BlockVector> blocks = new HashSet<>();

        for (int x = minV.getX(); x <= maxV.getX(); x++) {
            for (int y = minV.getY(); y <= maxV.getY(); y++) {
                for (int z = minV.getZ(); z <= maxV.getZ(); z++) {
                    blocks.add(new BlockVector(x, y, z));
                }
            }
        }

        blockVectors = blocks;
        return Collections.unmodifiableCollection(blocks);
    }

    private long getBlocksEffected() {
        BlockVector minV = minVector();
        BlockVector maxV = maxVector();

        return (maxV.getX() - minV.getX()) * (maxV.getY() - minV.getY()) * (maxV.getZ() - minV.getZ());
    }

    /**
     * Not thread safe
     * @return A collection of {@link Block} this cuboid has
     */
    @Override
    public Collection<Block> getBlocks() {
            if (blocks != null) return blocks;
            BlockVector minV = minVector();
            BlockVector maxV = maxVector();
            Set<Block> blocks = new HashSet<>();

            for (int x = minV.getX(); x <= maxV.getX(); x++) {
                for (int y = minV.getY(); y <= maxV.getY(); y++) {
                    for (int z = minV.getZ(); z <= maxV.getZ(); z++) {
                        blocks.add(l1.getWorld().getBlockAt(x, y, z));
                    }
                }
            }

            this.blocks = blocks;
            return Collections.unmodifiableCollection(blocks);
    }

    public Location getLocationOne() {
        return l1;
    }

    public Location getLocationTwo() {
        return l2;
    }

    protected void setLocationOne(Location loc) {
        this.l1 = loc;
        this.blockVectors = null;
        this.blocks = null;
    }

    protected void setLocationTwo(Location loc) {
        this.l2 = loc;
        this.blockVectors = null;
        this.blocks = null;
    }

    @Override
    public Location getHighestCorner() {
        BlockVector v = maxVector();
        return new Location(l1.getWorld(), v.getX(), v.getY(), v.getZ());
    }

    @Override
    public Location getLowestCorner() {
        BlockVector v = minVector();
        return new Location(l1.getWorld(), v.getX(), v.getY(), v.getZ());
    }

    private BlockVector minVector() {
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        return new BlockVector(x1, y1, z1);
    }

    private BlockVector maxVector() {
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
        return new BlockVector(x2, y2, z2);
    }

}
