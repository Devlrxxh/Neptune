package dev.lrxh.blockChanger.wrapper.impl.chunk;

import dev.lrxh.blockChanger.BlockChanger;
import dev.lrxh.blockChanger.wrapper.CraftWrapper;
import dev.lrxh.blockChanger.wrapper.impl.chunk.impl.v1_21.CraftChunk_1_21;
import org.bukkit.Chunk;

public abstract class CraftChunk extends CraftWrapper<Chunk> {

    public CraftChunk(Chunk chunk) {
        super(chunk);
    }

    public static CraftChunk from(Chunk chunk) {
        return switch (BlockChanger.getMinorVersion()) {
            case 21 -> new CraftChunk_1_21(chunk);
            default -> throw new IllegalStateException("Unexpected minor version: " + BlockChanger.getMinorVersion());
        };
    }

    @Override
    protected abstract Object apply(Chunk input);

    public abstract IChunkAccess getHandle();
}
