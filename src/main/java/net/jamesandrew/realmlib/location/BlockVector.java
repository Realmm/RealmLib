package net.jamesandrew.realmlib.location;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockVector {

    private final int x, y, z;

    public BlockVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public static BlockVector toBlockVector(Block block) {
        return new BlockVector(block.getX(), block.getY(), block.getZ());
    }

    public static BlockVector toBlockVector(Location location) {
        return new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean equals(BlockVector vector) {
        return x == vector.getX() && y == vector.getY() && z == vector.getZ();
    }

}