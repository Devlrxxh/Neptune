package dev.lrxh.neptune.game.kit;

import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.kit.IKitRule;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.PotionEffectUtils;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@AllArgsConstructor
public class Kit implements IKit {

    private String name;
    private String displayName;
    private List<ItemStack> items;
    private HashSet<Arena> arenas;
    private ItemStack icon;
    private HashMap<KitRule, Boolean> rules;
    private int queue;
    private int playing;
    private int slot;
    private int kitEditorSlot;
    private double health;
    private List<PotionEffect> potionEffects;
    private double damageMultiplier;

    public Kit(String name, String displayName, List<ItemStack> items, HashSet<Arena> arenas,
               ItemStack icon, HashMap<KitRule, Boolean> rules, int slot, double health,
               int kitEditorSlot, List<PotionEffect> potionEffects, double damageMultiplier) {
        this.name = name;
        this.displayName = displayName;
        this.items = items;
        this.arenas = arenas;
        this.icon = icon;
        this.rules = rules;
        this.queue = 0;
        this.playing = 0;
        this.slot = slot;
        this.health = health;
        this.kitEditorSlot = kitEditorSlot;
        this.potionEffects = potionEffects;
        this.damageMultiplier = damageMultiplier;

        addToProfiles();
    }

    /**
     * Create a kit from a player’s current inventory and potion effects.
     */
    public Kit(String name, Player player) {
        this.name = name;
        this.displayName = "&7" + name;
        this.items = Arrays.stream(player.getInventory().getContents()).toList();
        this.arenas = new HashSet<>();
        this.icon = new ItemStack(Material.DIAMOND_SWORD);
        this.rules = defaultRules();
        this.queue = 0;
        this.playing = 0;
        this.slot = KitService.get().kits.size() + 1;
        this.health = 20;
        this.kitEditorSlot = slot;
        this.damageMultiplier = 1.0;
        this.potionEffects = new ArrayList<>();

        for (PotionEffect effect : player.getActivePotionEffects()) {
            int maxDuration = PlayerUtil.getMaxDuration(player, effect.getType());
            potionEffects.add(new PotionEffect(effect.getType(),
                    Math.min(effect.getDuration(), maxDuration),
                    effect.getAmplifier(),
                    effect.isAmbient(),
                    effect.hasParticles(),
                    effect.hasIcon()));
        }

        addToProfiles();
    }

    /**
     * Minimal constructor using name, items, and icon.
     */
    public Kit(String name, List<ItemStack> items, ItemStack icon) {
        this.name = name;
        this.displayName = name;
        this.items = items;
        this.arenas = new HashSet<>();
        this.rules = defaultRules();
        this.icon = icon.getType().equals(Material.AIR) ? new ItemStack(Material.BARRIER) : new ItemStack(icon);
        this.queue = 0;
        this.playing = 0;
        this.slot = KitService.get().kits.size() + 1;
        this.health = 20;
        this.kitEditorSlot = slot;
        this.potionEffects = new ArrayList<>();
        this.damageMultiplier = 1.0;

        addToProfiles();
    }

    private HashMap<KitRule, Boolean> defaultRules() {
        HashMap<KitRule, Boolean> rules = new HashMap<>();
        for (KitRule rule : KitRule.values()) rules.put(rule, false);
        return rules;
    }

    public boolean is(KitRule rule) {
        return rules.getOrDefault(rule, false);
    }

    public void toggle(KitRule rule) {
        rules.put(rule, !rules.getOrDefault(rule, false));
    }

    @Override
    public HashMap<IKitRule, Boolean> getRule() {
        HashMap<IKitRule, Boolean> map = new HashMap<>();
        rules.forEach(map::put);
        return map;
    }

    public void toggleArena(Arena arena) {
        if (arenas.contains(arena)) arenas.remove(arena);
        else arenas.add(arena);
    }

    public boolean isArenaAdded(Arena arena) {
        return arenas.contains(arena);
    }

    public List<String> getArenasAsString() {
        List<String> arenaNames = new ArrayList<>();
        arenas.stream().filter(Objects::nonNull).forEach(a -> arenaNames.add(a.getName()));
        return arenaNames;
    }

    public void addQueue() {
        queue++;
    }

    public void removeQueue() {
        if (queue > 0) queue--;
    }

    public void addPlaying() {
        playing++;
    }

    public void removePlaying() {
        if (playing > 0) playing--;
    }

    public void giveLoadout(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        Profile profile = API.getProfile(playerUUID);
        GameData gameData = profile.getGameData();

        ItemStack[] contents = (gameData.get(this) != null && !gameData.get(this).getKitLoadout().isEmpty())
                ? gameData.get(this).getKitLoadout().toArray(new ItemStack[0])
                : items.toArray(new ItemStack[0]);

        player.getInventory().setContents(contents);
        player.addPotionEffects(potionEffects);
        player.updateInventory();
    }

    public void giveLoadout(Participant participant) {
        Player player = participant.getPlayer();
        if (player == null) return;

        Profile profile = API.getProfile(player);
        GameData gameData = profile.getGameData();

        ItemStack[] contents = (gameData.get(this) != null && !gameData.get(this).getKitLoadout().isEmpty())
                ? ItemUtils.color(gameData.get(this).getKitLoadout().toArray(new ItemStack[0]), participant.getColor().getContentColor())
                : ItemUtils.color(items.toArray(new ItemStack[0]), participant.getColor().getContentColor());

        player.getInventory().setContents(contents);
        player.addPotionEffects(potionEffects);
        player.updateInventory();
    }

    public CompletableFuture<Arena> getRandomArena() {
        List<Arena> validArenas = new ArrayList<>();
        for (Arena arena : arenas) {
            if (arena.isEnabled() && arena.isSetup()) validArenas.add(arena);
        }

        if (validArenas.isEmpty()) return CompletableFuture.completedFuture(null);

        Arena selected = validArenas.get(ThreadLocalRandom.current().nextInt(validArenas.size()));
        return selected.createDuplicate();
    }

    private void addToProfiles() {
        for (Profile profile : ProfileService.get().profiles.values()) {
            profile.getGameData().getKitDataInternal().put(this, new KitData());
        }
    }

    public List<String> getPotionsAsString() {
        List<String> potions = new ArrayList<>();
        potionEffects.stream().filter(Objects::nonNull).forEach(effect -> potions.add(PotionEffectUtils.serialize(effect)));
        return potions;
    }

    public void delete() {
        KitService.get().kits.remove(this);
        KitService.get().save();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Kit kit) return kit.getName().equals(name);
        return false;
    }
}