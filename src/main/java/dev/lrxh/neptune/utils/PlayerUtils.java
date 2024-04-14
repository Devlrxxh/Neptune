package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;

import java.lang.reflect.Field;
import java.util.*;

@UtilityClass
public class PlayerUtils {
    private final Neptune plugin = Neptune.get();
    private Field STATUS_PACKET_ID_FIELD;
    private Field STATUS_PACKET_STATUS_FIELD;
    private Field SPAWN_PACKET_ID_FIELD;

    static {
        try {
            STATUS_PACKET_ID_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("a");
            STATUS_PACKET_ID_FIELD.setAccessible(true);

            STATUS_PACKET_STATUS_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("b");
            STATUS_PACKET_STATUS_FIELD.setAccessible(true);

            SPAWN_PACKET_ID_FIELD = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
            SPAWN_PACKET_ID_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

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

    public void teleportToSpawn(UUID playerUUID) {
        if (Bukkit.getPlayer(playerUUID) == null) return;
        Player player = Bukkit.getPlayer(playerUUID);
        if (plugin.getCache().getSpawn() != null) {
            player.teleport(plugin.getCache().getSpawn());
        } else {
            player.sendMessage(CC.error("Make sure to set spawn location using /neptune setspawn!"));
        }
    }

    public void sendMessage(Player player, List<Object> content) {
        if (((CraftPlayer) player).getHandle().playerConnection == null) return;

        List<BaseComponent[]> combinedComponents = new ArrayList<>();

        for (Object obj : ColorUtil.addLastColorToNext(content)) {

            if (obj instanceof String) {
                String message = CC.translate((String) obj);
                combinedComponents.add(TextComponent.fromLegacyText(message));
            } else if (obj instanceof BaseComponent) {
                combinedComponents.add(new BaseComponent[]{(BaseComponent) obj});
            }
        }

        PacketPlayOutChat packet = new PacketPlayOutChat();

        List<BaseComponent> flattenedComponents = new ArrayList<>();
        for (BaseComponent[] components : combinedComponents) {
            flattenedComponents.addAll(Arrays.asList(components));
        }

        packet.components = flattenedComponents.toArray(new BaseComponent[0]);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public int getPing(UUID playerUUID) {
        if (Bukkit.getPlayer(playerUUID) == null) return 0;
        Player player = Bukkit.getPlayer(playerUUID);
        return ((CraftPlayer) player).getHandle().ping;
    }

    public void sendMessage(Player player, Object... content) {
        if (((CraftPlayer) player).getHandle().playerConnection == null) return;

        List<BaseComponent> combinedComponents = new ArrayList<>();

        for (Object obj : content) {
            if (obj instanceof String) {
                String message = (String) obj;
                combinedComponents.addAll(Arrays.asList(TextComponent.fromLegacyText(message)));
            } else if (obj instanceof BaseComponent) {
                combinedComponents.add((BaseComponent) obj);
            }
        }

        BaseComponent[] combinedArray = combinedComponents.toArray(new BaseComponent[0]);

        PacketPlayOutChat packet = new PacketPlayOutChat();
        packet.components = combinedArray;
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }


    public void denyMovement(UUID playerUUID) {
        if (Bukkit.getPlayer(playerUUID) == null) return;
        Player player = Bukkit.getPlayer(playerUUID);

        if (player == null) {
            return;
        }
        player.setFlying(false);
        player.setWalkSpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);

        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 250));
    }

    public void allowMovement(UUID playerUUID) {
        if (Bukkit.getPlayer(playerUUID) == null) return;
        Player player = Bukkit.getPlayer(playerUUID);

        if (player == null) {
            return;
        }
        player.setFlying(false);
        player.setWalkSpeed(0.2F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
    }

    public static void sendTitle(UUID playerUUID, String header, String footer, int duration) {
        if (Bukkit.getPlayer(playerUUID) == null) return;

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.sendTitle(new Title(CC.translate(header), CC.translate(footer), 1, duration, 10));

        }
    }

    public void doVelocityChange(UUID playerUUID) {
        if (Bukkit.getPlayer(playerUUID) == null) return;
        Player player = Bukkit.getPlayer(playerUUID);

        player.setVelocity(player.getVelocity().add(new Vector(0, 0.25, 0)));
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setVelocity(player.getVelocity().add(new Vector(0, 0.15, 0)));
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void animateDeath(UUID playerUUID) {
        if (Bukkit.getPlayer(playerUUID) == null) return;
        Player player = Bukkit.getPlayer(playerUUID);

        int entityId = EntityUtils.getFakeEntityId();
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
        PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();

        try {
            SPAWN_PACKET_ID_FIELD.set(spawnPacket, entityId);
            STATUS_PACKET_ID_FIELD.set(statusPacket, entityId);
            STATUS_PACKET_STATUS_FIELD.set(statusPacket, (byte) 3);
            int radius = MinecraftServer.getServer().getPlayerList().d();
            Set<Player> sentTo = new HashSet<>();
            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof Player) {
                    Player watcher = (Player) entity;
                    if (!watcher.getUniqueId().equals(player.getUniqueId())) {
                        ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(spawnPacket);
                        ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(statusPacket);
                        sentTo.add(watcher);
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(Neptune.get(), () -> {
                for (Player watcher : sentTo) {
                    ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
                }
            }, 10L);
        } catch (IllegalAccessException ignored) {
        }
    }
}
