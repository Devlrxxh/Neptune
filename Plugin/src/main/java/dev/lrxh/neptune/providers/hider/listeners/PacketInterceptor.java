package dev.lrxh.neptune.providers.hider.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import dev.lrxh.neptune.cache.EntityCache;
import dev.lrxh.neptune.cache.ItemCache;
import dev.lrxh.neptune.utils.ServerUtils;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.UUID;

/**
 * Author: Athishh
 * Package: dev.lrxh.neptune.providers.hider.listeners
 * Created on: 1/20/2024
 */
public class PacketInterceptor extends PacketListenerAbstract {

    public PacketInterceptor() {
        super(PacketListenerPriority.MONITOR);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getUser() == null) {
            return;
        }
        if (event.getUser().getUUID() == null) {
            return;
        }
        Player receiver = Bukkit.getPlayer(event.getUser().getUUID());
        if (receiver == null) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            WrapperPlayServerSpawnEntity wrapper = new WrapperPlayServerSpawnEntity(event);

            int entityID = wrapper.getEntityId();
            Entity entity = EntityCache.getEntityById(entityID);
            if (entity == null) return;
            if (entity instanceof Player player) {
                if (receiver.canSee(player)) return;
                event.setCancelled(true);
            } else if (entity instanceof Projectile projectile) {
                if (!(projectile.getShooter() instanceof Player shooter)) return;

                if (receiver.canSee(shooter)) return;
                event.setCancelled(true);
            } else if (entity instanceof Item item) {
                UUID uuid = ItemCache.getPlayerWhoDropped(item);
                if (uuid == null) return;
                Player dropper = Bukkit.getPlayer(uuid);
                if (dropper == null) return;
                if (receiver.canSee(dropper)) return;
                event.setCancelled(true);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.EXPLOSION) {
            WrapperPlayServerExplosion wrapper = new WrapperPlayServerExplosion(event);
            UUID owner = EntityCache.getWindChargeOwner(wrapper.getPosition());
            if (owner == null) return;
            Player shooter = Bukkit.getPlayer(owner);
            if (shooter == null) return;
            if (receiver.canSee(shooter)) return;
            event.setCancelled(true);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_SOUND_EFFECT) {
            WrapperPlayServerEntitySoundEffect wrapper = new WrapperPlayServerEntitySoundEffect(event);
            if (EntityCache.getEntityById(wrapper.getEntityId()) instanceof Player player) {
                if (receiver.canSee(player)) return;
                event.setCancelled(true);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
            WrapperPlayServerEntityEffect wrapper = new WrapperPlayServerEntityEffect(event);
            if (EntityCache.getEntityById(wrapper.getEntityId()) instanceof Player player) {
                if (receiver.canSee(player)) return;
                event.setCancelled(true);
            }
        }  else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
            WrapperPlayServerPlayerInfoRemove wrapper = new WrapperPlayServerPlayerInfoRemove(event);
            Player player = Bukkit.getPlayer(wrapper.getProfileIds().get(0));
            if (player != null) event.setCancelled(true);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove wrapper = new WrapperPlayServerEntityRelativeMove(event);
            if (EntityCache.getEntityById(wrapper.getEntityId()) instanceof Player player) {
                if (receiver.canSee(player)) return;
                event.setCancelled(true);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation wrapper = new WrapperPlayServerEntityRelativeMoveAndRotation(event);
            if (EntityCache.getEntityById(wrapper.getEntityId()) instanceof Player player) {
                if (receiver.canSee(player)) return;
                event.setCancelled(true);
            }
        }
    }
}
