package dev.lrxh.neptune.providers.hider;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@SuppressWarnings("unused")
public class EntityHider {

    private static final Int2ObjectMap<IntSet> hiddenEntitiesPerObserver = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<UUID> droppedItemsMap = new Int2ObjectOpenHashMap<>();

    @Getter
    protected static Policy policy;

    public EntityHider(JavaPlugin plugin, Policy polic) {
        policy = polic;
    }

    public static boolean setVisibility(Player observer, int entityID, boolean visible) {
        return switch (policy) {
            case BLACKLIST -> !setMembership(observer.getEntityId(), entityID, !visible);
            case WHITELIST -> setMembership(observer.getEntityId(), entityID, visible);
        };
    }

    protected static boolean setMembership(int observerID, int entityID, boolean member) {
        IntSet set = hiddenEntitiesPerObserver.computeIfAbsent(observerID, k -> new IntOpenHashSet());
        return member ? set.add(entityID) : set.remove(entityID);
    }

    protected static boolean getMembership(int observerID, int entityID) {
        IntSet set = hiddenEntitiesPerObserver.get(observerID);
        return set != null && set.contains(entityID);
    }

    public static boolean isVisible(Player observer, int entityID) {
        boolean presence = getMembership(observer.getEntityId(), entityID);
        return (policy == Policy.WHITELIST) == presence;
    }

    public static boolean isVisible(Player observer, Entity entity) {
        return entity != null && isVisible(observer, entity.getEntityId());
    }

    public static boolean isOwnedByHiddenPlayer(Entity entity, Player receiver) {
        if (entity instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
            return !isVisible(receiver, shooter.getEntityId());
        }
        if (entity instanceof Item item) {
            UUID dropper = droppedItemsMap.get(item.getEntityId());
            Player dropperPlayer = dropper != null ? Bukkit.getPlayer(dropper) : null;
            return dropperPlayer != null && !isVisible(receiver, dropperPlayer);
        }
        return false;
    }

    public static boolean isEntityHidden(Entity entity, Player observer) {
        if (entity == null) return false;

        if (entity instanceof Player player) return !isVisible(observer, player.getEntityId());
        if (entity instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter)
            return !isVisible(observer, shooter.getEntityId());
        if (entity instanceof Item item) {
            UUID dropper = droppedItemsMap.get(item.getEntityId());
            Player dropperPlayer = dropper != null ? Bukkit.getPlayer(dropper) : null;
            return dropperPlayer != null && !isVisible(observer, dropperPlayer);
        }

        return !isVisible(observer, entity.getEntityId());
    }

    public static void removeEntity(Entity entity) {
        int entityID = entity.getEntityId();
        hiddenEntitiesPerObserver.values().forEach(set -> set.remove(entityID));
        if (entity instanceof Item) {
            droppedItemsMap.remove(entityID);
        }
    }

    public static void removePlayer(Player player) {
        hiddenEntitiesPerObserver.remove(player.getEntityId());
    }

    public static void toggleEntity(Player observer, Entity entity) {
        if (isVisible(observer, entity)) {
            hideEntity(observer, entity);
        } else {
            showEntity(observer, entity);
        }
    }

    public static void showEntity(Player observer, Entity entity) {
        if (observer == null || entity == null) return;
        setVisibility(observer, entity.getEntityId(), true);
    }

    public static void hideEntity(Player observer, Entity entity) {
        if (observer == null || entity == null) return;
        boolean visibleBefore = setVisibility(observer, entity.getEntityId(), false);

        if (visibleBefore) {
            destroyEntity(observer.getUniqueId(), entity.getEntityId());
        }
    }

    public static UUID getPlayerWhoDropped(Item droppedItem) {
        return droppedItemsMap.get(droppedItem.getEntityId());
    }

    public static Int2ObjectMap<UUID> getDroppedItemsMap() {
        return droppedItemsMap;
    }

    public static void destroyEntity(UUID user, int id) {
        PacketEvents.getAPI().getProtocolManager().sendPacket(
                PacketEvents.getAPI().getProtocolManager().getChannel(user),
                new WrapperPlayServerDestroyEntities(id));
    }

    public final boolean canSee(Player observer, Entity entity) {
        return observer != null && entity != null && isVisible(observer, entity.getEntityId());
    }

    public enum Policy {
        WHITELIST,
        BLACKLIST
    }
}
