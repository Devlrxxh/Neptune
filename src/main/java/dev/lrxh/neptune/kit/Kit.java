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
    private boolean arenaBreak;

    //VALUES
    private int queue, playing;

    public Kit(String name, String displayName, List<ItemStack> items, HashSet<Arena> arenas, ItemStack icon, boolean build, boolean arenaBreak, boolean hunger, boolean sumo, boolean fallDamage, boolean denyMovement, boolean boxing, boolean damage) {
        this.name = name;
        this.displayName = displayName;
        this.items = items;
        this.arenas = arenas;
        this.icon = icon;
        this.build = build;
        this.arenaBreak = arenaBreak;
        this.hunger = hunger;
        this.sumo = sumo;
        this.fallDamage = fallDamage;
        this.denyMovement = denyMovement;
        this.boxing = boxing;
        this.damage = damage;
        this.queue = 0;
        this.playing = 0;
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

    public void removeQueue() {
        queue--;
    }

    public void addQueue() {
        queue++;
    }

    public void removePlaying() {
        playing--;
    }

    public void addPlaying() {
        playing++;
    }

}

