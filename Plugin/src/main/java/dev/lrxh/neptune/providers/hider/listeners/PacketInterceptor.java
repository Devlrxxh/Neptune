package dev.lrxh.neptune.providers.hider.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntitySoundEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import dev.lrxh.neptune.cache.EntityCache;
import dev.lrxh.neptune.cache.ItemCache;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ServerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Author: Athishh
 * Package: me.athishh.lotus.core.user.hider.listeners
 * Created on: 1/20/2024
 */
public class PacketInterceptor extends PacketListenerAbstract {

    public PacketInterceptor() {
        super(PacketListenerPriority.NORMAL);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {

//        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
//            WrapperPlayServerPlayerInfoUpdate wrapper = new WrapperPlayServerPlayerInfoUpdate(event);
//            List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> list = wrapper.getEntries();
//
//            for (WrapperPlayServerPlayerInfoUpdate.PlayerInfo data : list) {
//                if (data == null) return;
//
//                data.getGameProfile().setName("dsaadsaadsaadwqsw");
//            }
//
//            event.markForReEncode(true);
//            return;
//        }

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
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_SOUND_EFFECT) {

            WrapperPlayServerEntitySoundEffect wrapper = new WrapperPlayServerEntitySoundEffect(event);
            if (EntityCache.getEntityById(wrapper.getEntityId()) instanceof Player player) {
                if (receiver.canSee(player)) return;
                event.setCancelled(true);
            }

            /*
             * PLAYER_INFO_REMOVE removes player from tablist, but its fired by both hidePlayer() & on Player quit.
             * We need to cancel the packet only if its fired by hidePlayer() ie only if the player is online.
             * */
//        } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
//            WrapperPlayServerPlayerInfoRemove wrapper = new WrapperPlayServerPlayerInfoRemove(event);
//            Player player = Bukkit.getPlayer(wrapper.getProfileIds().get(0));
//            if (player != null) event.setCancelled(true);
        }
    }
}
