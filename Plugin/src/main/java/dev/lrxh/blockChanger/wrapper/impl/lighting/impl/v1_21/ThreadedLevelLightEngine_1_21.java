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
            Class<?> serverLevelClass = nms("server.level.ServerLevel");
            CraftWorld craftWorld = new CraftWorld(input);
            Object world = getMethod(craftWorld.nms().getClass(), "getHandle", serverLevelClass)
                    .invoke(craftWorld.nms());

            Class<?> chunkProviderClass = nms("server.level.ChunkProviderServer");
            Object chunkProvider = getMethod(world.getClass(), "m", chunkProviderClass)
                    .invoke(world);

            Class<?> lightEngineClass = nms("server.level.ThreadedLevelLightEngine");
            return getMethod(chunkProvider.getClass(), "a", lightEngineClass)
                    .invoke(chunkProvider);

        } catch (Throwable e) {
            throw new RuntimeException("Failed to get ThreadedLevelLightEngine instance", e);
        }
    }

    @Override
    public void relightChunks(Set<Chunk> chunks) {
        try {
            Object lightEngine = nms();
            Collection<Object> chunkPositions = new ArrayList<>();

            Method relightMethod = getReflectiveMethod(
                    lightEngine.getClass(),
                    "starlight$serverRelightChunks",
                    Collection.class,
                    Consumer.class,
                    IntConsumer.class
            );

            Class<?> chunkPosClass = nms("world.level.ChunkPos");
            Constructor<?> chunkPosConstructor = chunkPosClass.getConstructor(int.class, int.class);

            for (Chunk chunk : chunks) {
                Object chunkPos = chunkPosConstructor.newInstance(chunk.getX(), chunk.getZ());
                chunkPositions.add(chunkPos);
            }

            relightMethod.invoke(
                    lightEngine,
                    chunkPositions,
                    (Consumer<Object>) chunkPos -> {
                    },
                    (IntConsumer) value -> {
                    }
            );

        } catch (Throwable e) {
            throw new RuntimeException("Failed to relight chunks using StarLightLightingProvider", e);
        }
    }
}
