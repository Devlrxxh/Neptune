package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.UUID;

@Data
public class MatchSnapshot {
    private final UUID uuid;
    private final String username;
    private final double health;
    private final int hunger;
    private final int ping;
    private final ItemStack[] contents;
    private final Collection<PotionEffect> effects;
    private String opponent;
    private int longestCombo;
    private int totalHits;

    public MatchSnapshot(Player player, String username) {
        this.uuid = player.getUniqueId();
        this.health = Math.round(player.getHealth() / 2);
        this.hunger = player.getFoodLevel();
        this.ping = PlayerUtil.getPing(player.getUniqueId());
        this.contents = ItemUtils.getContents(player.getUniqueId());
        this.effects = player.getActivePotionEffects();
        this.username = username;
    }
}
