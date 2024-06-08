package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Kit {
    private final Neptune plugin = Neptune.get();
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
    private boolean bestOfThree;
    private boolean saturationHeal;
    private boolean showHP;
    //VALUES
    private int queue, playing;

    public Kit(String name, String displayName, List<ItemStack> items, HashSet<Arena> arenas, ItemStack icon, boolean build, boolean arenaBreak, boolean hunger, boolean sumo, boolean fallDamage, boolean denyMovement, boolean boxing, boolean damage, boolean bestOfThree, boolean saturationHeal, boolean showHP) {
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
        this.bestOfThree = bestOfThree;
        this.saturationHeal = saturationHeal;
        this.showHP = showHP;
        this.queue = 0;
        this.playing = 0;

        if (plugin.getLeaderboardManager() != null) {
            plugin.getLeaderboardManager().getLeaderboards().put(this, new ArrayList<>());
        }
    }

    public Kit(String name, List<ItemStack> items, ItemStack icon) {
        this.name = name;
        this.displayName = name;
        this.items = items;
        this.arenas = new HashSet<>();
        this.icon = icon.getType().equals(Material.AIR) ? new ItemStack(Material.BARRIER) : icon;
        this.build = false;
        this.arenaBreak = false;
        this.hunger = false;
        this.sumo = false;
        this.fallDamage = false;
        this.denyMovement = false;
        this.boxing = false;
        this.damage = false;
        this.bestOfThree = false;
        this.saturationHeal = false;
        this.showHP = false;
        this.queue = 0;
        this.playing = 0;

        if (plugin.getLeaderboardManager() != null) {
            plugin.getLeaderboardManager().getLeaderboards().put(this, new ArrayList<>());
        }
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
        if (!(queue == 0)) {
            queue--;
        }
    }

    public void addQueue() {
        queue++;
    }

    public void removePlaying() {
        if (!(playing == 0)) {
            playing--;
        }
    }

    public void addPlaying() {
        playing++;
    }

    public void delete() {
        Neptune.get().getKitManager().kits.remove(this);
    }
}

