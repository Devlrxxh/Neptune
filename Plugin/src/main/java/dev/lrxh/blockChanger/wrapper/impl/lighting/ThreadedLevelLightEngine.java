package dev.lrxh.blockChanger.wrapper.impl.lighting;

import dev.lrxh.blockChanger.BlockChanger;
import dev.lrxh.blockChanger.wrapper.CraftWrapper;
import dev.lrxh.blockChanger.wrapper.impl.lighting.impl.v1_21.ThreadedLevelLightEngine_1_21;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Set;

public abstract class ThreadedLevelLightEngine extends CraftWrapper<World> {
    public ThreadedLevelLightEngine(World input) {
        super(input);
    }

    public static ThreadedLevelLightEngine from(World world) {
        return switch (BlockChanger.getMinorVersion()) {
            case 21 -> new ThreadedLevelLightEngine_1_21(world);
            default -> throw new IllegalStateException("Unexpected minor version: " + BlockChanger.getMinorVersion());
        };
    }

    @Override
    protected abstract Object apply(World input);

    public abstract void relightChunks(Set<Chunk> chunks);
}
