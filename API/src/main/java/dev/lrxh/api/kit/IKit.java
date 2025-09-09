package dev.lrxh.api.kit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import dev.lrxh.api.arena.IArena;

public interface IKit {
    String getName();
    String getDisplayName();
    List<ItemStack> getItems();
    ItemStack getIcon();
    int getQueue();
    int getPlaying();
    int getSlot();
    int getKitEditorSlot();
    double getHealth();
    List<PotionEffect> getPotionEffects();
    double getDamageMultiplier();

    HashMap<IKitRule, Boolean> getRule();
    HashSet<IArena> getAllArenas();

    void giveLoadout(UUID uuid);
}
