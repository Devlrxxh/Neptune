package dev.lrxh.blockChanger.wrapper.impl.lighting.impl.v1_21;

import dev.lrxh.blockChanger.wrapper.impl.lighting.ThreadedLevelLightEngine;
import dev.lrxh.blockChanger.wrapper.impl.world.CraftWorld;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class ThreadedLevelLightEngine_1_21 extends ThreadedLevelLightEngine {
    public ThreadedLevelLightEngine_1_21(World input) {
        super(input);
    }

    @Override
    protected Object apply(World input) {
        try {
            Class<?> serverLevel = nms("server.level.ServerLevel");
            CraftWorld craftWorld = new CraftWorld(input);
            Object world = getMethod(craftWorld.nms().getClass(), "getHandle", serverLevel)
                    .invoke(craftWorld.nms());
            Class<?> chunkProvider = nms("server.level.ChunkProviderServer");
            Object chunkProviderInstance = getMethod(world.getClass(), "m", chunkProvider).invoke(world);
            Class<?> threadedLevelLightEngine = nms("server.level.ThreadedLevelLightEngine");
            Object result = getMethod(chunkProviderInstance.getClass(), "a", threadedLevelLightEngine).invoke(chunkProviderInstance);
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get StarLightLightingProvider", e);
        }
    }

    @Override
    public void relightChunks(Set<Chunk> chunks) {
        try {
            Class<?> lightEngineClass = Class.forName("ca.spottedleaf.moonrise.patches.starlight.light.StarLightLightingProvider");

            Object lightEngine = lightEngineClass.cast(nms());
            Collection<Object> positions = new ArrayList<>();
            Method lightChunksMethod = getReflectiveMethod(
                    lightEngine.getClass(),
                    "starlight$serverRelightChunks",
                    Collection.class,
                    Consumer.class,
                    IntConsumer.class
            );

            for (Chunk chunk : chunks) {
                Class<?> chunkPosClass = Class.forName("net.minecraft.world.level.ChunkPos");
                Constructor<?> constructor = chunkPosClass.getConstructor(int.class, int.class);

                Object chunkPos = constructor.newInstance(chunk.getX(), chunk.getZ());
                positions.add(chunkPos);
            }

            lightChunksMethod.invoke(lightEngine, positions,
                    (Consumer<Object>) (chunkPos) -> {
                    },
                    (IntConsumer) (intValue) -> {
                    }
            );
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to bind method handle for lightChunk", e);
        }
    }

}
