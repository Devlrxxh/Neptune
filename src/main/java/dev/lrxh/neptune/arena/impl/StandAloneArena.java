package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public class StandAloneArena extends Arena {
    private transient LinkedHashMap<Chunk, ChunkSnapshot> chunkSnapshots = new LinkedHashMap<>();
    private Location min;
    private Location max;
    private double deathY;
    private double limit;
    private boolean used;
    private HashSet<StandAloneArena> copies;
    private boolean duplicate;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, HashSet<StandAloneArena> copies, double deathY, double limit, boolean enabled, boolean duplicate) {
        super(name, displayName, redSpawn, blueSpawn, enabled);
        this.min = min;
        this.max = max;
        this.copies = copies;
        this.limit = limit;
        this.deathY = deathY;
        this.used = false;
        this.duplicate = duplicate;

        takeSnapshot();
    }

    public List<String> getCopiesAsString() {
        List<String> copiesString = null;
        if (copies != null && !copies.isEmpty()) {
            copiesString = new ArrayList<>();
            for (Arena arena : copies) {
                copiesString.add(arena.getName());
            }
        }
        return copiesString;
    }

    public void takeSnapshot() {
        chunkSnapshots.clear();
        chunkSnapshots = Neptune.get().getVersionHandler().getChunk().takeSnapshot(min, max);
    }

    public void restoreSnapshot() {
        Neptune.get().getVersionHandler().getChunk().restoreSnapshot(chunkSnapshots);
    }
}