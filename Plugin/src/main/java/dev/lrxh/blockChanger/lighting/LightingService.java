package dev.lrxh.blockChanger.lighting;

import dev.lrxh.blockChanger.wrapper.impl.lighting.ThreadedLevelLightEngine;
import org.bukkit.Chunk;

import java.util.Set;

public class LightingService {
    public static void updateLighting(Set<Chunk> chunks) {
        ThreadedLevelLightEngine lightEngine = ThreadedLevelLightEngine.from(chunks.iterator().next().getWorld());
        lightEngine.relightChunks(chunks);
    }
}
