package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.utils.IPlayerUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class PlayerUtil {
    
    private final IPlayerUtils utils = Neptune.get().getVersionHandler().getPlayerUtils();

    public void reset(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.setHealth(20.0D);
            player.setSaturation(20.0F);
            player.setFallDistance(0.0F);
            player.setFoodLevel(20);
            player.setFireTicks(0);
            player.setMaximumNoDamageTicks(20);
            player.setExp(0.0F);
            player.setLevel(0);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.getInventory().setContents(new ItemStack[36]);
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            player.getInventory().setHeldItemSlot(0);
            player.updateInventory();
            player.resetTitle();
        }
    }

    public void kick(UUID playerUUID, String message) {
        utils.kick(playerUUID, CC.color(message));
    }

    public void teleportToSpawn(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        if (Neptune.get().getCache().getSpawn() != null) {
            player.teleport(Neptune.get().getCache().getSpawn());
        } else {
            player.sendMessage(CC.error("Make sure to set spawn location using /neptune setspawn!"));
        }
    }

    public int getPing(UUID playerUUID) {
        return utils.getPing(playerUUID);
    }

    public ItemStack getPlayerHead(UUID playerUUID) {
        return utils.getPlayerHead(playerUUID);
    }

    public void sendMessage(UUID playerUUID, List<Object> content) {
        utils.sendMessage(playerUUID, content);
    }

    public void sendMessage(UUID playerUUID, Object content) {
        utils.sendMessage(playerUUID, content);
    }

    public ItemStack getItemInHand(UUID playerUUID) {
        return utils.getItemInHand(playerUUID);
    }

    public void sendMessage(UUID playerUUID, String message) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        player.sendMessage(CC.color(message));
    }

    public void sendTitle(UUID playerUUID, String header, String footer, int duration) {
        utils.sendTitle(playerUUID, CC.color(header), CC.color(footer), 1, duration, 5);
    }

    public void doVelocityChange(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        player.setVelocity(player.getVelocity().add(new Vector(0, 0.25, 0)));
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setVelocity(player.getVelocity().add(new Vector(0, 0.15, 0)));
        player.setAllowFlight(true);
        player.setFlying(true);
    }
}
