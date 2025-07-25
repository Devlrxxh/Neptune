package dev.lrxh.blockChanger;

import dev.lrxh.blockChanger.lighting.LightingService;
import dev.lrxh.blockChanger.wrapper.impl.chunk.CraftChunk;
import dev.lrxh.blockChanger.wrapper.impl.snapshot.ChunkSectionSnapshot;
import dev.lrxh.blockChanger.wrapper.impl.snapshot.CuboidSnapshot;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BlockChanger {
    public static int MINOR_VERSION;

    public static ChunkSectionSnapshot createChunkBlockSnapshot(Chunk chunk) {
        CraftChunk craftChunk = CraftChunk.from(chunk);
        return new ChunkSectionSnapshot(craftChunk.getHandle().getSectionsCopy());
    }

    public static void restoreChunkBlockSnapshot(Chunk chunk, ChunkSectionSnapshot snapshot) {
        CraftChunk craftChunk = CraftChunk.from(chunk);
        craftChunk.getHandle().setSections(snapshot.getSections());
        chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
    }

    public static CompletableFuture<Void> restoreCuboidSnapshot(CuboidSnapshot snapshot) {
        return CompletableFuture.runAsync(() -> {
            for (Map.Entry<Chunk, ChunkSectionSnapshot> entry : snapshot.getSnapshots().entrySet()) {
                restoreChunkBlockSnapshot(entry.getKey(), entry.getValue());
            }

            LightingService.updateLighting(snapshot.getSnapshots().keySet());
        });
    }


    public static int getMinorVersion() {
        if (MINOR_VERSION != 0) {
            return MINOR_VERSION;
        }

        String[] versionParts = Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.");
        if (versionParts.length >= 2) {
            MINOR_VERSION = Integer.parseInt(versionParts[1]);
        } else {
            MINOR_VERSION = 0;
        }

        return MINOR_VERSION;
    }
}