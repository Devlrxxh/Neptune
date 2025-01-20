package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class PlayerUtil {

    public void reset(Player player) {
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

    public void kick(Player player, String message) {
        player.kick(Component.text(CC.color(message)));
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

    public int getPing(Player player) {
        return player.getPing();
    }

    public int getPing(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) throw new RuntimeException("Player UUID isn't valid");
        return player.getPing();
    }

    public ItemStack getPlayerHead(UUID playerUUID) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getPlayer(playerUUID));
        head.setItemMeta(skullMeta);
        return head;
    }

    public void sendMessage(UUID playerUUID, List<Object> content) {
        TextComponent.Builder builder = Component.text();

        for (Object obj : content) {
            if (obj instanceof String message) {
                builder.append(Component.text(message));
            } else if (obj instanceof TextComponent) {
                builder.append((TextComponent) obj);
            }
        }

        sendMessage(playerUUID, builder);
    }

    public void sendMessage(UUID playerUUID, Object message) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        if (message instanceof String) {
            player.sendMessage((String) message);
        } else if (message instanceof Component) {
            player.sendMessage((Component) message);
        } else if (message instanceof TextComponent.Builder) {
            player.sendMessage((TextComponent.Builder) message);
        }
    }

    public void sendMessage(UUID playerUUID, String message) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        player.sendMessage(CC.color(message));
    }

    public void sendTitle(Player player, String header, String footer, int duration) {
        player.sendTitle(CC.color(header), CC.color(footer), 1, duration, 5);
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
