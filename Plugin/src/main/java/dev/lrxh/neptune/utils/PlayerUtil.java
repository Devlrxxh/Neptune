package dev.lrxh.neptune.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.experimental.UtilityClass;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.ExtraConversionUtil;
import me.tofaa.entitylib.wrapper.WrapperPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
        player.setSaturation(20.0F);
        player.setFallDistance(0.0F);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setMaximumNoDamageTicks(20);
        player.setExp(0.0F);
        player.setLevel(0);
        player.setAllowFlight(false);
        player.setFlying(false);

        Profile profile = API.getProfile(player);
        if (profile.getState().equals(ProfileState.IN_LOBBY)
                || profile.getState().equals(ProfileState.IN_KIT_EDITOR)
                || profile.getState().equals(ProfileState.IN_PARTY)
                || profile.getState().equals(ProfileState.IN_QUEUE)) {
            player.setGameMode(GameMode.ADVENTURE);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }

        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setContents(new ItemStack[36]);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.updateInventory();
        player.resetTitle();
        player.setMaxHealth(20.0f);
        player.setHealth(20.0D);
        resetActionbar(player);
    }

    public void playDeathAnimation(Player player, List<Player> watchers) {
        if (player == null) return;

        WrapperPlayer p = new WrapperPlayer(new UserProfile(UUID.randomUUID(), player.getName()),
                EntityLib.getPlatform().getEntityIdProvider().provide(UUID.randomUUID(), EntityTypes.PLAYER));
        p.setInTablist(false);
        p.setTextureProperties(ExtraConversionUtil.getProfileFromBukkitPlayer(player).getTextureProperties());
        p.spawn(SpigotConversionUtil.fromBukkitLocation(player.getLocation()));

        WrapperPlayServerEntityMetadata healthPacket = new WrapperPlayServerEntityMetadata(
                p.getEntityId(), List.of(new EntityData(9, EntityDataTypes.FLOAT, 0.0f))
        );

        for (Player watcher : watchers) {
            if (player.getUniqueId().equals(watcher.getUniqueId())) continue;
            p.addViewer(watcher.getUniqueId());
            PacketEvents.getAPI().getPlayerManager().sendPacket(watcher, healthPacket);
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(Neptune.get(), () -> {
            p.despawn();
            p.remove();
        }, 40L);
    }


    public void resetActionbar(Player player) {
        player.sendActionBar(" ");
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
