package dev.lrxh.neptune.profile.hider;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Entity hider which aims to fix spigot visibility
 * of projectiles, particle effects and sounds
 * </p>
 * Originally coded by Lipchya and cleaned/improved
 * Modified DevDrizzy (Cleaned up code and Removed Excessive Reflection usage)
 * Rewritten to packetevents + latest versions by Athishh
 *
 * @version Lotus
 * @since 20/1/2024
 */
@SuppressWarnings({"unused"})
public class EntityHider {

    public static Map<Integer, Player> droppedItemsMap = new HashMap<>();
    protected static Table<Integer, Integer, Boolean> observerEntityMap = HashBasedTable.create();
    /**
     * -- GETTER --
     * Retrieve the current visibility policy.
     */
    @Getter
    protected static Policy policy;

    public EntityHider(JavaPlugin plugin, Policy polic) {
        Preconditions.checkNotNull(plugin, "plugin cannot be NULL.");
        policy = polic;
    }

    public static boolean setVisibility(Player observer, int entityID, boolean visible) {
        switch (policy) {
            case BLACKLIST:
                // Non-membership means they are visible
                return !setMembership(observer, entityID, !visible);
            case WHITELIST:
                return setMembership(observer, entityID, visible);
            default:
                throw new IllegalArgumentException("Unknown policy: " + policy);
        }
    }

    /**
     * Add or remove the given entity and observer entry from the table.
     *
     * @param observer - the player observer.
     * @param entityID - ID of the entity.
     * @param member   - TRUE if they should be present in the table, FALSE otherwise.
     * @return TRUE if they already were present, FALSE otherwise.
     */
    // Helper method
    protected static boolean setMembership(Player observer, int entityID, boolean member) {
        if (member) {
            return observerEntityMap.put(observer.getEntityId(), entityID, true) != null;
        } else {
            return observerEntityMap.remove(observer.getEntityId(), entityID) != null;
        }
    }

    /**
     * Determine if the given entity and observer is present in the table.
     *
     * @param observer - the player observer.
     * @param entityID - ID of the entity.
     * @return TRUE if they are present, FALSE otherwise.
     */
    protected static boolean getMembership(Player observer, int entityID) {
        return observerEntityMap.contains(observer.getEntityId(), entityID);
    }

    /**
     * Determine if a given entity is visible for a particular observer.
     *
     * @param observer - the observer player.
     * @param entityID -  ID of the entity that we are testing for visibility.
     * @return TRUE if the entity is visible, FALSE otherwise.
     */
    public static boolean isVisible(Player observer, int entityID) {
        // If we are using a whitelist, presence means visibility - if not, the opposite is the case

        boolean presence = getMembership(observer, entityID);

        return (policy == Policy.WHITELIST) == presence;
    }

    /**
     * Remove the given entity from the underlying map.
     *
     * @param entity - the entity to remove.
     */
    public static void removeEntity(Entity entity) {
        int entityID = entity.getEntityId();

        for (Map<Integer, Boolean> maps : observerEntityMap.rowMap().values()) {
            maps.remove(entityID);
        }
    }

    /**
     * Invoked when a player logs out.
     *
     * @param player - the player that used logged out.
     */
    public static void removePlayer(Player player) {
        // Cleanup
        observerEntityMap.rowMap().remove(player.getEntityId());
    }

    /**
     * Toggle the visibility status of an entity for a player.
     * <p>
     * If the entity is visible, it will be hidden. If it is hidden, it will become visible.
     *
     * @param observer - the player observer.
     * @param entity   - the entity to toggle.
     */
    public static void toggleEntity(Player observer, Entity entity) {
        if (isVisible(observer, entity.getEntityId())) {
            hideEntity(observer, entity);
        } else {
            showEntity(observer, entity);
        }
    }

    /**
     * Allow the observer to see an entity that was previously hidden.
     *
     * @param observer - the observer.
     * @param entity   - the entity to show.
     */
    public static void showEntity(Player observer, Entity entity) {
        validate(observer, entity);

        setVisibility(observer, entity.getEntityId(), true);
    }

    /**
     * Prevent the observer from seeing a given entity.
     *
     * @param observer - the player observer.
     * @param entity   - the entity to hide.
     */
    public static void hideEntity(Player observer, Entity entity) {
        validate(observer, entity);
        boolean visibleBefore = setVisibility(observer, entity.getEntityId(), false);

        if (visibleBefore) {
            // Make the entity disappear
            try {
                destroyEntity(observer.getUniqueId(), entity.getEntityId());
            } catch (Exception e) {
                throw new RuntimeException("Cannot send server packet.", e);
            }

        }
    }

    // For validating the input parameters
    private static void validate(Player observer, Entity entity) {
        Preconditions.checkNotNull(observer, "observer cannot be NULL.");
        Preconditions.checkNotNull(entity, "entity cannot be NULL.");
    }

    public static Player getPlayerWhoDropped(Item droppedItem) {
        return droppedItemsMap.get(droppedItem.getEntityId());
    }

    public static void destroyEntity(UUID user, int id) {
        WrapperPlayServerDestroyEntities wrapperPlayServerDestroyEntities = new WrapperPlayServerDestroyEntities(id);
        PacketEvents.getAPI().getProtocolManager().sendPacket(PacketEvents.getAPI().getProtocolManager().getChannel(user), wrapperPlayServerDestroyEntities);
    }

    /**
     * Determine if the given entity has been hidden from an observer.
     * Note that the entity may very well be occluded or out of range from the perspective
     * of the observer. This method simply checks if an entity has been completely hidden
     * for that observer.
     *
     * @param observer - the observer.
     * @param entity   - the entity that may be hidden.
     * @return TRUE if the player may see the entity, FALSE if the entity has been hidden.
     */
    public final boolean canSee(Player observer, Entity entity) {
        validate(observer, entity);

        return isVisible(observer, entity.getEntityId());
    }

    public enum Policy {
        /**
         * All entities are invisible by default. Only entities specifically made visible may be seen.
         */
        WHITELIST,

        /**
         * All entities are visible by default. An entity can only be hidden explicitly.
         */
        BLACKLIST,
    }

}