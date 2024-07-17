package dev.lrxh.neptune.providers.hider.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntitySoundEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import dev.lrxh.neptune.cache.EntityCache;
import dev.lrxh.neptune.cache.ItemCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.UUID;

/**
 * Author: Athishh
 * Package: me.athishh.lotus.core.user.hider.listeners
 * Created on: 1/20/2024
 */
public class PacketInterceptor extends PacketListenerAbstract {

    public PacketInterceptor() {
        super(PacketListenerPriority.MONITOR);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
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
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_SOUND_EFFECT) {
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
            WrapperPlayServerEntitySoundEffect wrapper = new WrapperPlayServerEntitySoundEffect(event);
            if (EntityCache.getEntityById(wrapper.getEntityId()) instanceof Player player) {
                if (receiver.canSee(player)) return;
                event.setCancelled(true);
            }
        }
    }
}
