package net.jamesandrew.realmlib.location;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class BlockChange implements Change {

    private final BlockVector blockVector;
    private final MaterialData materialData;

    public BlockChange(BlockVector blockVector, MaterialData materialData) {
        this.blockVector = blockVector;
        this.materialData = materialData;
    }

    public BlockChange(BlockVector blockVector, Material material, byte data) {
        this.blockVector = blockVector;
        this.materialData = new MaterialData(material, data);
    }

    public BlockVector getBlockVector() {
        return blockVector;
    }

    public MaterialData getMaterialData() {
        return materialData;
    }

    public boolean equals(BlockChange c) {
        return this.blockVector.equals(c.blockVector) && materialData.equals(c.materialData);
    }

    @Override
    public void change(Changer l) {
        throw new UnsupportedOperationException("Unsupported in this version");
//        final net.minecraft.server.v1_8_R3.World w = ((CraftWorld) l.getWorld()).getHandle();
//        final net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(blockVector.getX() >> 4, blockVector.getZ() >> 4);
//
//        final BlockPosition bp = new BlockPosition(blockVector.getX(), blockVector.getY(), blockVector.getZ());
//        final int combined = materialData.getItemTypeId() + (materialData.getData() << 12);
//        final IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(combined);
//        w.setTypeAndData(bp, ibd, 2);
//        chunk.a(bp, ibd);
    }
}