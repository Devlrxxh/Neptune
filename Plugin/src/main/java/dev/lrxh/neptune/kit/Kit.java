package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.leaderboard.LeaderboardManager;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.ProfileManager;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@AllArgsConstructor
public class Kit {
    private String name;
    private String displayName;
    private List<ItemStack> items;
    private HashSet<Arena> arenas;
    private ItemStack icon;
    private HashMap<KitRule, Boolean> rules;
    private int queue, playing, slot;

    public Kit(String name, String displayName, List<ItemStack> items, HashSet<Arena> arenas, ItemStack icon, HashMap<KitRule, Boolean> rules, int slot) {
        this.name = name;
        this.displayName = displayName;
        this.items = items;
        this.arenas = arenas;
        this.icon = icon;
        this.rules = rules;
        this.queue = 0;
        this.playing = 0;
        this.slot = slot;

        checkMissing();
    }

    public Kit(String name, List<ItemStack> items, ItemStack icon) {
        this.name = name;
        this.displayName = name;
        this.items = items;
        this.arenas = new HashSet<>();
        this.rules = rules();
        this.icon = icon.getType().equals(Material.AIR) ? new ItemStack(Material.BARRIER) : new ItemStack(icon);
        this.queue = 0;
        this.playing = 0;
        this.slot = KitManager.get().kits.size() + 1;

        checkMissing();
    }

    private HashMap<KitRule, Boolean> rules() {
        HashMap<KitRule, Boolean> rules = new HashMap<>();
        for (KitRule kitRule : KitRule.values()) {
            rules.put(kitRule, false);
        }

        return rules;
    }


    private void checkMissing() {
        LeaderboardManager.get().getLeaderboards().put(this, new ArrayList<>());

        addToProfiles();
    }

    private void addToProfiles() {
        for (Map.Entry<UUID, Profile> profile : ProfileManager.get().profiles.entrySet()) {
            profile.getValue().getGameData().getKitData().put(this, new KitData());
        }
    }

    public List<String> getArenasAsString() {
        List<String> arenasString = new ArrayList<>();
        if (!arenas.isEmpty()) {
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

    @Nullable
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

    public void giveLoadout(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        Profile profile = API.getProfile(playerUUID);
        GameData gameData = profile.getGameData();
        if (gameData.getKitData() == null || gameData.getKitData().get(this) == null ||
                gameData.getKitData().get(this).getKitLoadout().isEmpty()) {
            player.getInventory().setContents(items.toArray(new ItemStack[0]));
        } else {
            player.getInventory().setContents(gameData.getKitData().get(this).getKitLoadout().toArray(new ItemStack[0]));
        }

        player.updateInventory();
    }

    public void giveLoadout(Participant participant) {
        Player player = Bukkit.getPlayer(participant.getPlayerUUID());
        if (player == null) return;
        Profile profile = API.getProfile(participant.getPlayerUUID());
        GameData gameData = profile.getGameData();
        if (gameData.getKitData() == null || gameData.getKitData().get(this) == null ||
                gameData.getKitData().get(this).getKitLoadout().isEmpty()) {
            player.getInventory().setContents(ItemUtils.color(items.toArray(new ItemStack[0]), participant.getColor().getContentColor()));
        } else {
            player.getInventory().setContents(ItemUtils.color(gameData.getKitData().get(this).getKitLoadout().toArray(new ItemStack[0]), participant.getColor().getContentColor()));
        }

        player.updateInventory();
    }

    public void addPlaying() {
        playing++;
    }

    public void delete() {
        KitManager.get().kits.remove(this);
    }
}

