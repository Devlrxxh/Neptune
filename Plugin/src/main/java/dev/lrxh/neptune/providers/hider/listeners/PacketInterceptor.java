package dev.lrxh.neptune.providers.hider.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3i;
import dev.lrxh.neptune.cache.EntityCache;
import dev.lrxh.neptune.cache.ItemCache;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.*;

import java.util.Queue;
import java.util.UUID;

public class PacketInterceptor extends PacketListenerAbstract {

    public PacketInterceptor() {
        super(PacketListenerPriority.HIGHEST);
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
            Entity entity = EntityCache.getEntityById(wrapper.getEntityId());
            if (entity == null) return;

            if (entity instanceof Player player && !receiver.canSee(player)) {
                event.setCancelled(true);
            } else if (entity instanceof Projectile projectile &&
                    projectile.getShooter() instanceof Player shooter &&
                    !receiver.canSee(shooter)) {
                event.setCancelled(true);
            } else if (entity instanceof Item item) {
                UUID uuid = ItemCache.getPlayerWhoDropped(item);
                Player dropper = uuid != null ? Bukkit.getPlayer(uuid) : null;
                if (dropper != null && !receiver.canSee(dropper)) {
                    event.setCancelled(true);
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Server.EXPLOSION) {
            WrapperPlayServerExplosion wrapper = new WrapperPlayServerExplosion(event);
            Vector3d position = wrapper.getPosition();
            UUID owner = EntityCache.getWindChargeOwner(position);
            Player shooter = (owner != null) ? Bukkit.getPlayer(owner) : null;
            if (shooter != null && !receiver.canSee(shooter)) {
                event.setCancelled(true);
            }

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_SOUND_EFFECT) {

            WrapperPlayServerEntitySoundEffect wrapper = new WrapperPlayServerEntitySoundEffect(event);
            Entity entity = EntityCache.getEntityById(wrapper.getEntityId());
            if (entity instanceof Player player && !receiver.canSee(player)) {
                event.setCancelled(true);
            }

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
            WrapperPlayServerEntityEffect wrapper = new WrapperPlayServerEntityEffect(event);
            Entity entity = EntityCache.getEntityById(wrapper.getEntityId());
            if (entity instanceof Player player && !receiver.canSee(player)) {
                event.setCancelled(true);
            }

        } else if (event.getPacketType() == PacketType.Play.Server.EFFECT) {
            WrapperPlayServerEffect wrapper = new WrapperPlayServerEffect(event);
            Vector3i pos = wrapper.getPosition();
            Queue<EntityCache.ShooterData> queue = EntityCache.potionEffects.get(pos);

            if (queue == null || queue.isEmpty()) return;

            EntityCache.ShooterData data = queue.peek();
            if (data == null || System.currentTimeMillis() - data.timestamp() > 5000) return;

            Player shooter = Bukkit.getPlayer(data.shooterId());
            if (shooter == null || !receiver.canSee(shooter)) {
                event.setCancelled(true);
            }


        } else if (event.getPacketType() == PacketType.Play.Server.PARTICLE) {
            WrapperPlayServerParticle wrapper = new WrapperPlayServerParticle(event);
            String particleName = wrapper.getParticle().getType().getName().getKey();
            Vector3d pos = wrapper.getPosition();
            UUID shooterId = EntityCache.getShooterAt(pos);

            if (shooterId == null) {
                return;
            }
            Player shooter = Bukkit.getPlayer(shooterId);
            if (shooter != null && !receiver.canSee(shooter)) {
                event.setCancelled(true);
            }
        }

        else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
            WrapperPlayServerPlayerInfoRemove wrapper = new WrapperPlayServerPlayerInfoRemove(event);
            Player player = Bukkit.getPlayer(wrapper.getProfileIds().get(0));
            if (player != null && !receiver.canSee(player)) {
                event.setCancelled(true);
            }

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove wrapper = new WrapperPlayServerEntityRelativeMove(event);
            Entity entity = EntityCache.getEntityById(wrapper.getEntityId());
            if (entity instanceof Player player && !receiver.canSee(player)) {
                event.setCancelled(true);
            }

        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation wrapper = new WrapperPlayServerEntityRelativeMoveAndRotation(event);
            Entity entity = EntityCache.getEntityById(wrapper.getEntityId());
            if (entity instanceof Player player && !receiver.canSee(player)) {
                event.setCancelled(true);
            }
        }
    }
}
