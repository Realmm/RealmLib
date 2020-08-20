package net.jamesandrew.realmlib.location;

import org.bukkit.Chunk;

public class ChunkUnloadChange implements Change {

    private final Chunk chunk;

    public ChunkUnloadChange(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void change(Changer loader) {
        chunk.load(true);
    }

    public Chunk getChunk() {
        return chunk;
    }

}
