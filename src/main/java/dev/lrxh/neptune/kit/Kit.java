package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.arena.Arena;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Kit {
    private String name;
    private String displayName;
    private boolean ranked;
    private List<ItemStack> items;
    private HashSet<Arena> arenas;
    private ItemStack icon;
    //RULES
    private boolean build;
    private boolean hunger;
    private boolean sumo;
    private boolean fallDamage;
    private boolean denyMovement;
    private boolean boxing;
    private boolean damage;

    //VALUES
    private int unrankedQueue, rankedQueue, unrankedPlaying, rankedPlaying;

    public Kit(String name, String displayName, boolean ranked, List<ItemStack> items, HashSet<Arena> arenas, ItemStack icon, boolean build, boolean hunger, boolean sumo, boolean fallDamage, boolean denyMovement, boolean boxing, boolean damage) {
        this.name = name;
        this.displayName = displayName;
        this.ranked = ranked;
        this.items = items;
        this.arenas = arenas;
        this.icon = icon;
        this.build = build;
        this.hunger = hunger;
        this.sumo = sumo;
        this.fallDamage = fallDamage;
        this.denyMovement = denyMovement;
        this.boxing = boxing;
        this.damage = damage;
        this.unrankedQueue = 0;
        this.rankedQueue = 0;
        this.rankedPlaying = 0;
        this.unrankedPlaying = 0;
    }

    public List<String> getArenasAsString() {
        List<String> arenasString = null;
        if (arenas != null && !arenas.isEmpty()) {
            arenasString = new ArrayList<>();
            for (Arena arena : arenas) {
                arenasString.add(arena.getName());
            }
        }
        return arenasString;
    }

    public void removeQueue(boolean ranked) {
        if (ranked) {
            rankedQueue--;
        } else {
            unrankedQueue--;
        }
    }

    public void addQueue(boolean ranked) {
        if (ranked) {
            rankedQueue++;
        } else {
            unrankedQueue++;
        }
    }

    public void removePlaying(boolean ranked) {
        if (ranked) {
            rankedPlaying--;
        } else {
            unrankedPlaying--;
        }
    }

    public void addPlaying(boolean ranked) {
        if (ranked) {
            rankedPlaying++;
        } else {
            unrankedPlaying++;
        }
    }

}

