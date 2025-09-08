package dev.lrxh.api.kit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import dev.lrxh.api.arena.IArena;

public interface IKit {
    String getName();
    String getDisplayName();
    List<ItemStack> getItems();
    HashSet<? extends IArena> getArenas();
    ItemStack getIcon();
    HashMap<? extends IKitRule, Boolean> getRules();
    int getQueue();
    int getPlaying();
    int getSlot();
    int getKitEditorSlot();
    double getHealth();
    List<PotionEffect> getPotionEffects();
    double getDamageMultiplier();
    List<String> getArenasAsString();
    List<String> getPotionsAsString();
    CompletableFuture<? extends IArena> getRandomArena();

    void giveLoadout(UUID uuid);
    void toggleArena(IArena arena);
    boolean is(IKitRule kitRule);
}
