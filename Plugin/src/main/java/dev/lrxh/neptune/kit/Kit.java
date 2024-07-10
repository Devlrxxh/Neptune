package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.data.KitData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
    private HashMap<KitRule, Boolean> rules;
    private int queue, playing;

    public Kit(String name, String displayName, List<ItemStack> items, HashSet<Arena> arenas, ItemStack icon, HashMap<KitRule, Boolean> rules) {
        this.name = name;
        this.displayName = displayName;
        this.items = items;
        this.arenas = arenas;
        this.icon = icon;
        this.rules = rules;
        this.queue = 0;
        this.playing = 0;

        if (plugin.getLeaderboardManager() != null) {
            plugin.getLeaderboardManager().getLeaderboards().put(this, new ArrayList<>());
        }

        if (plugin.getProfileManager() != null) {
            addToProfiles();
        }
    }

    public Kit(String name, List<ItemStack> items, ItemStack icon) {
        this.name = name;
        this.displayName = name;
        this.items = items;
        this.arenas = new HashSet<>();
        this.rules = new HashMap<>();
        this.icon = icon.getType().equals(Material.AIR) ? new ItemStack(Material.BARRIER) : new ItemStack(icon);
        this.queue = 0;
        this.playing = 0;

        if (plugin.getLeaderboardManager() != null) {
            plugin.getLeaderboardManager().getLeaderboards().put(this, new ArrayList<>());
        }

        if (plugin.getProfileManager() != null) {
            addToProfiles();
        }
    }

    private void addToProfiles() {
        for (Map.Entry<UUID, Profile> profile : plugin.getProfileManager().profiles.entrySet()) {
            profile.getValue().getGameData().getKitData().put(this, new KitData());
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

    public boolean is(KitRule kitRule) {
        return rules.get(kitRule);
    }

    public void set(KitRule kitRule) {
        rules.put(kitRule, !rules.get(kitRule));
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

    public Arena getRandomArena() {
        List<Arena> kitArenas = new ArrayList<>();
        for (Arena arena : arenas) {
            if (!arena.isEnabled()) continue;
            if (is(KitRule.BUILD)) {
                if ((arena instanceof StandAloneArena && !((StandAloneArena) arena).isUsed())) {
                    kitArenas.add(arena);
                }
            } else {
                kitArenas.add(arena);
            }
        }
        Collections.shuffle(kitArenas);
        return kitArenas.isEmpty() ? null : kitArenas.get(ThreadLocalRandom.current().nextInt(kitArenas.size()));
    }

    public void addPlaying() {
        playing++;
    }

    public void delete() {
        Neptune.get().getKitManager().kits.remove(this);
    }
}

