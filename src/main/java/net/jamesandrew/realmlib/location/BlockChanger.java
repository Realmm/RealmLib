package net.jamesandrew.realmlib.location;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

public final class BlockChanger extends Changer {

    public BlockChanger(World world) {
        super(world);
    }

    public BlockChanger addBlockChange(BlockVector blockVector, MaterialData materialData) {
        addChange(new BlockChange(blockVector, materialData));
        return this;
    }

    public BlockChanger addBlockChanges(Material material, byte data, BlockVector... blockVectors) {
        for (final BlockVector blockVector : blockVectors) {
            addBlockChange(blockVector, material, data);
        }
        return this;
    }

    public BlockChanger addBlockChange(BlockVector blockVector, Material material, byte data) {
        return addBlockChange(blockVector, new MaterialData(material, data));
    }

    public BlockChanger addBlockChange(Location location, Material material, byte data) {
        return addBlockChange(BlockVector.toBlockVector(location), material, data);
    }

    public BlockChanger addBlockChange(Location location, MaterialData materialData) {
        return addBlockChange(BlockVector.toBlockVector(location), materialData);
    }

    public BlockChanger addBlockChange(Block block, MaterialData materialData) {
        return addBlockChange(block.getLocation(), materialData);
    }

    public BlockChanger addBlockChange(Block block, Material material, byte data) {
        return addBlockChange(block.getLocation(), material, data);
    }

    public BlockChanger addBlockChanges(Material material, byte data, Location... locations) {
        for (final Location location : locations) {
            addBlockChange(location, material, data);
        }
        return this;
    }

    public BlockChanger addBlockChanges(Material material, byte data, Block... blocks) {
        for (final Block block : blocks) {
            addBlockChange(block.getLocation(), material, data);
        }
        return this;
    }

}
