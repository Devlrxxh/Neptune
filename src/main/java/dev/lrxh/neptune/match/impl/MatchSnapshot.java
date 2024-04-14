package dev.lrxh.neptune.match.impl;

import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.UUID;

@Data
@Getter
public class MatchSnapshot {
    private final UUID uuid;
    private final String username;
    private final double health;
    private final int hunger;
    private final ItemStack[] armor;
    private final ItemStack[] contents;
    private final Collection<PotionEffect> effects;
    private UUID opponent;
    private int potionsThrown;
    private int potionsMissed;
    private int longestCombo;
    private int totalHits;

    public MatchSnapshot(Player player, boolean dead) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
        this.health = dead ? 0 : (player.getHealth() == 0 ? 0 : Math.round(player.getHealth() / 2));
        this.hunger = player.getFoodLevel();
        this.armor = player.getInventory().getArmorContents();
        this.contents = player.getInventory().getContents();
        this.effects = player.getActivePotionEffects();
    }
}
