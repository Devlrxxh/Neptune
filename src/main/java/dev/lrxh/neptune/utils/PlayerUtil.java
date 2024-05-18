package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.utils.IPlayerUtils;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class PlayerUtil {
    private final Neptune plugin = Neptune.get();
    private final IPlayerUtils utils = plugin.getVersionHandler().getPlayerUtils();

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
            allowMovement(playerUUID);
            player.resetTitle();
        }
    }

    public void kick(UUID playerUUID, String message) {
        utils.kick(playerUUID, CC.color(message));
    }

    public void giveKit(UUID playerUUID, Kit kit) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        Profile profile = plugin.getProfileManager().getByUUID(playerUUID);
        GameData gameData = profile.getGameData();
        if (gameData.getKitData() == null || gameData.getKitData().get(kit) == null ||
                gameData.getKitData().get(kit).getKit().isEmpty()) {
            player.getInventory().setContents(kit.getItems().toArray(new ItemStack[0]));
        } else {
            player.getInventory().setContents(gameData.getKitData().get(kit).getKit().toArray(new ItemStack[0]));
        }

        player.updateInventory();
    }

    public void teleportToSpawn(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        if (plugin.getCache().getSpawn() != null) {
            player.teleport(plugin.getCache().getSpawn());
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

        TextComponent.Builder builder = Component.text();

        for (Object obj : ColorUtil.addColors(content)) {
            if (obj instanceof String) {
                String message = CC.color((String) obj);
                builder.append(Component.text(message));
            } else if (obj instanceof TextComponent) {
                builder.append((TextComponent) obj);
            }
        }

        System.out.println(builder.toString());
        utils.sendMessage(playerUUID, builder);
    }


    public void sendMessage(UUID playerUUID, TextComponent content) {
        utils.sendMessage(playerUUID, content);
    }

    public ItemStack getItemInHand(UUID playerUUID) {
        return utils.getItemInHand(playerUUID);
    }


    public void sendMessage(UUID playerUUID, String message) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        player.sendMessage(message);
    }


    public void denyMovement(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        player.setFlying(false);
        player.setWalkSpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);

        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 250));
    }

    public void allowMovement(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        player.setFlying(false);
        player.setWalkSpeed(0.2F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
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
