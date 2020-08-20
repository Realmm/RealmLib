package net.jamesandrew.realmlib.shapes;

import net.jamesandrew.realmlib.location.BlockVector;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Collection;

public interface Shape {

    boolean contains(Location loc);

    Collection<Block> getBlocks();

    Collection<BlockVector> getBlockVectors();

}
