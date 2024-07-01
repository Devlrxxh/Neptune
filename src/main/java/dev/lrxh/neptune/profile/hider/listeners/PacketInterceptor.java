package dev.lrxh.neptune.profile.hider.listeners;


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

/**
 * Author: Athishh
 * Package: me.athishh.lotus.core.user.hider.listeners
 * Created on: 1/20/2024
 */
public class PacketInterceptor extends PacketListenerAbstract {

    public PacketInterceptor() {
        super(PacketListenerPriority.MONITOR);
    }

    // make sure to implement your threading system.
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            if (event.getUser() == null) {
                return;
            }
            Player receiver = Bukkit.getPlayer(event.getUser().getUUID());
            WrapperPlayServerSpawnEntity wrapper = new WrapperPlayServerSpawnEntity(event);
            int entityID = wrapper.getEntityId();
            assert receiver != null;
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
                Player dropper = Bukkit.getPlayer(ItemCache.getPlayerWhoDropped(item));
                if (dropper == null) return;
                if (receiver.canSee(dropper)) return;
                event.setCancelled(true);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_SOUND_EFFECT) {
            Player receiver = Bukkit.getPlayer(event.getUser().getUUID());
            if (receiver == null) return;
            WrapperPlayServerEntitySoundEffect wrapper = new WrapperPlayServerEntitySoundEffect(event);
            if(EntityCache.getEntityById(wrapper.getEntityId()) instanceof Player player) {
                if (receiver.canSee(player)) return;
                event.setCancelled(true);
            }
        }

    }
}