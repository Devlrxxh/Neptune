package dev.lrxh.neptune.game.arena.impl;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.utils.BlockChanger;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class StandAloneArena extends Arena {
    private final List<String> copies;
    private final boolean copy;
    private Location min;
    private Location max;
    private double limit;
    private boolean used;
    private int deathY;
    private List<Material> whitelistedBlocks;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, double limit, boolean enabled, boolean copy, List<String> copies, List<Material> whitelistedBlocks) {
        super(name, displayName, redSpawn, blueSpawn, enabled);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.copy = copy;
        this.used = false;
        this.copies = copies;
        this.whitelistedBlocks = whitelistedBlocks;
    }

    public StandAloneArena(String arenaName) {
        super(arenaName, arenaName, null, null, false);
        this.min = null;
        this.max = null;
        this.limit = 68321;
        this.used = false;
        this.copy = false;
        this.copies = new ArrayList<>();
        this.whitelistedBlocks = new ArrayList<>();
    }

    @Override
    public boolean isSetup() {
        return !(getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

    public void deleteAllCopies() {
        for (String name : copies) {
            StandAloneArena arena = (StandAloneArena) ArenaService.get().getArenaByName(name);
            if (arena == null) continue;

            BlockChanger.setBlocksAsync(arena.getMin(), arena.getMax(), Material.AIR);

            arena.delete();
        }
        copies.clear();
    }

    public CompletableFuture<Void> generateCopies(int amount) {
        int initialSize = copies.size();

        return BlockChanger.captureBlocksAsync(min, max, true).thenCompose(blocks -> {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int i = 1; i < amount; i++) {
                int offset = (initialSize + i) * 500;
                Set<BlockChanger.BlockSnapshot> newBlocks = new HashSet<>();

                for (BlockChanger.BlockSnapshot block : blocks) {
                    BlockChanger.BlockSnapshot newBlock = block.clone();
                    newBlock.getLocation().add(offset, 0, 0);
                    newBlocks.add(newBlock);
                }

                final int copyIndex = i;
                final Location minCopy = LocationUtil.addOffsetX(getMin(), offset);
                final Location maxCopy = LocationUtil.addOffsetX(getMax(), offset);
                final Location redSpawnCopy = LocationUtil.addOffsetX(getRedSpawn(), offset);
                final Location blueSpawnCopy = LocationUtil.addOffsetX(getBlueSpawn(), offset);

                CompletableFuture<Void> future = BlockChanger.setBlocksAsync(getWorld(), newBlocks).thenAccept(unused -> {
                    StandAloneArena copy = new StandAloneArena(
                            getName() + "#" + (initialSize + copyIndex),
                            getDisplayName(),
                            redSpawnCopy,
                            blueSpawnCopy,
                            minCopy,
                            maxCopy,
                            getLimit(),
                            isEnabled(),
                            true,
                            null,
                            whitelistedBlocks
                    );

                    copies.add(copy.getName());
                    ArenaService.get().getArenas().add(copy);
                    ServerUtils.info("#" + copyIndex + " Created copy " + redSpawnCopy);
                });

                futures.add(future);
            }

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenRun(ArenaService.get()::saveArenas);
        });
    }

    public List<String> getWhitelistedBlocksAsString() {
        List<String> r = new ArrayList<>();

        for (Material material : whitelistedBlocks) {
            r.add(material.name());
        }

        return r;
    }
}