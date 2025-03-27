package dev.lrxh.neptune.game.match;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.ScoreboardLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.impl.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import dev.lrxh.neptune.utils.BlockChanger;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.*;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
@Setter
public abstract class Match {
    public final List<UUID> spectators = new ArrayList<>();
    public final Neptune plugin = Neptune.get();
    private final UUID uuid = UUID.randomUUID();
    private final HashSet<Location> placedBlocks = new HashSet<>();
    
    // Modified block tracking system with chunking
    private final Map<ChunkKey, Map<BlockPosition, BlockData>> chunkedChanges = new HashMap<>();
    private final Set<Location> liquids = new HashSet<>();
    private final HashSet<Entity> entities = new HashSet<>();
    private final Time time = new Time();
    public MatchState state;
    public Arena arena;
    public Kit kit;
    public List<Participant> participants;
    public int rounds;
    private boolean duel;
    private boolean ended;
    
    // New inner classes for more efficient block storage
    @Getter
    @AllArgsConstructor
    public static class ChunkKey {
        private final int x;
        private final int z;
        private final World world;
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChunkKey chunkKey = (ChunkKey) o;
            return x == chunkKey.x && z == chunkKey.z && world.equals(chunkKey.world);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, z, world.getName());
        }
        
        public static ChunkKey fromLocation(Location location) {
            return new ChunkKey(location.getBlockX() >> 4, location.getBlockZ() >> 4, location.getWorld());
        }
    }
    
    @Getter
    @AllArgsConstructor
    public static class BlockPosition {
        private final int x;
        private final int y;
        private final int z;
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlockPosition that = (BlockPosition) o;
            return x == that.x && y == that.y && z == that.z;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
        
        public static BlockPosition fromLocation(Location location) {
            return new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }
        
        public Location toLocation(World world) {
            return new Location(world, x, y, z);
        }
    }
    
    /**
     * Add a block change to the tracking system.
     * This method uses the new chunked storage system for better performance.
     *
     * @param location The location of the block
     * @param blockData The original block data to restore later
     */
    public void addBlockChange(Location location, BlockData blockData) {
        ChunkKey chunkKey = ChunkKey.fromLocation(location);
        BlockPosition blockPos = BlockPosition.fromLocation(location);
        
        chunkedChanges.computeIfAbsent(chunkKey, k -> new HashMap<>())
                .putIfAbsent(blockPos, blockData);
    }
    
    /**
     * Check if a location has been changed
     *
     * @param location The location to check
     * @return true if the location has been changed
     */
    public boolean hasBlockChange(Location location) {
        ChunkKey chunkKey = ChunkKey.fromLocation(location);
        BlockPosition blockPos = BlockPosition.fromLocation(location);
        
        Map<BlockPosition, BlockData> chunkChanges = chunkedChanges.get(chunkKey);
        return chunkChanges != null && chunkChanges.containsKey(blockPos);
    }
    
    /**
     * Get the original block data for a changed location
     *
     * @param location The location to get data for
     * @return The original BlockData or null if not found
     */
    public BlockData getOriginalBlockData(Location location) {
        ChunkKey chunkKey = ChunkKey.fromLocation(location);
        BlockPosition blockPos = BlockPosition.fromLocation(location);
        
        Map<BlockPosition, BlockData> chunkChanges = chunkedChanges.get(chunkKey);
        return chunkChanges != null ? chunkChanges.get(blockPos) : null;
    }
    
    // Compatibility method to get all changes (for legacy code)
    public Map<Location, BlockData> getChanges() {
        Map<Location, BlockData> allChanges = new HashMap<>();
        for (Map.Entry<ChunkKey, Map<BlockPosition, BlockData>> chunkEntry : chunkedChanges.entrySet()) {
            World world = chunkEntry.getKey().getWorld();
            for (Map.Entry<BlockPosition, BlockData> blockEntry : chunkEntry.getValue().entrySet()) {
                BlockPosition pos = blockEntry.getKey();
                allChanges.put(pos.toLocation(world), blockEntry.getValue());
            }
        }
        return allChanges;
    }

    public void playSound(Sound sound) {
        forEachPlayer(player -> player.playSound(player.getLocation(), sound, 1.0f, 1.0f));
    }

    public Location getSpawn(Participant participant) {
        if (participant.getColor().equals(ParticipantColor.RED)) {
            return arena.getRedSpawn();
        } else {
            return arena.getBlueSpawn();
        }
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for (Participant participant : participants) {
            players.add(participant.getPlayer());
        }

        return players;
    }

    public Participant getParticipant(UUID playerUUID) {
        for (Participant participant : participants) {
            if (participant.getPlayerUUID().equals(playerUUID)) {
                return participant;
            }
        }
        return null;
    }

    public Participant getParticipant(Player player) {
        for (Participant participant : participants) {
            if (participant.getPlayerUUID().equals(player.getUniqueId())) {
                return participant;
            }
        }
        return null;
    }

    public void sendTitle(String header, String footer, int duration) {
        forEachParticipant(participant -> PlayerUtil.sendTitle(participant.getPlayer(), header, footer, duration));
    }

    public void sendMessage(MessagesLocale message, Replacement... replacements) {
        forEachParticipant(participant -> message.send(participant.getPlayerUUID(), replacements));
    }

    public void addSpectator(Player player, Player target, boolean sendMessage, boolean add) {
        Profile profile = API.getProfile(player);

        profile.setMatch(this);
        profile.setState(ProfileState.IN_SPECTATOR);
        if (add) spectators.add(player.getUniqueId());

        forEachPlayer(participiantPlayer -> player.showPlayer(Neptune.get(), participiantPlayer));

        if (sendMessage) {
            broadcast(MessagesLocale.SPECTATE_START, new Replacement("<player>", player.getName()));
        }

        player.teleport(target.getLocation());
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void showPlayerForSpectators() {
        forEachSpectator(player -> forEachPlayer(participiantPlayer -> player.showPlayer(Neptune.get(), participiantPlayer)));
    }

    public void forEachPlayer(Consumer<Player> action) {
        for (Participant participant : participants) {
            Player player = participant.getPlayer();
            if (player != null) {
                action.accept(player);
            }
        }
    }

    public void forEachSpectator(Consumer<Player> action) {
        for (UUID spectatorUUID : spectators) {
            Player player = Bukkit.getPlayer(spectatorUUID);
            if (player != null) {
                action.accept(player);
            }
        }
    }

    public void forEachParticipant(Consumer<Participant> action) {
        for (Participant participant : participants) {
            if (participant.isDisconnected() || participant.isLeft()) continue;
            Player player = participant.getPlayer();
            if (player != null) {
                action.accept(participant);
            }
        }
    }

    public void forEachParticipantForce(Consumer<Participant> action) {
        for (Participant participant : participants) {
            Player player = participant.getPlayer();
            if (player != null) {
                action.accept(participant);
            }
        }
    }

    public void resetArena() {
        if (SettingsLocale.ARENA_RESET_EXPERIMENTAL.getBoolean()) {
            List<BlockChanger.BlockSnapshot> blocks = new ArrayList<>();

            // Add liquids to reset
            for (Location location : liquids) {
                blocks.add(new BlockChanger.BlockSnapshot(location, Material.AIR));
            }

            // Add changed blocks to reset using the chunked system
            for (Map.Entry<ChunkKey, Map<BlockPosition, BlockData>> chunkEntry : chunkedChanges.entrySet()) {
                World world = chunkEntry.getKey().getWorld();
                for (Map.Entry<BlockPosition, BlockData> blockEntry : chunkEntry.getValue().entrySet()) {
                    BlockPosition pos = blockEntry.getKey();
                    Location location = pos.toLocation(world);
                    blocks.add(new BlockChanger.BlockSnapshot(location, blockEntry.getValue()));
                }
            }
            
            BlockChanger.setBlocksAsync(arena.getWorld(), blocks);
        } else {
            // Standard reset process
            for (Location location : liquids) {
                arena.getWorld().getBlockAt(location).setBlockData(Material.AIR.createBlockData(), false);
            }
            
            // Reset blocks by chunk for better efficiency
            for (Map.Entry<ChunkKey, Map<BlockPosition, BlockData>> chunkEntry : chunkedChanges.entrySet()) {
                World world = chunkEntry.getKey().getWorld();
                for (Map.Entry<BlockPosition, BlockData> blockEntry : chunkEntry.getValue().entrySet()) {
                    BlockPosition pos = blockEntry.getKey();
                    Location location = pos.toLocation(world);
                    world.getBlockAt(location).setBlockData(blockEntry.getValue(), false);
                }
            }
        }

        removeEntities();
    }

    /**
     * Checks if a location is protected from block placement/breaking due to being near an end portal
     * Used for portal goal kits to prevent griefing near portals
     * 
     * @param location The location to check
     * @return True if protected, false if not
     */
    public boolean isLocationPortalProtected(Location location) {
        // Only check if the kit has bridges enabled
        if (kit.is(KitRule.BRIDGES)) {
            // Get protection radius from kit (if PORTAL_PROTECTION_RADIUS is enabled) or use default
            int protectionRadius = kit.is(KitRule.PORTAL_PROTECTION_RADIUS) ? 
                    kit.getPortalProtectionRadius() : 3;
            
            // If radius is 0, portal protection is disabled
            if (protectionRadius <= 0) {
                return false;
            }
                    
            // Get the blocks around the location
            for (int x = -protectionRadius; x <= protectionRadius; x++) {
                for (int y = -protectionRadius; y <= protectionRadius; y++) {
                    for (int z = -protectionRadius; z <= protectionRadius; z++) {
                        Location checkLoc = location.clone().add(x, y, z);
                        if (checkLoc.getBlock().getType() == Material.END_PORTAL) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public List<String> getScoreboard(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return new ArrayList<>();
        
        // Check global in-game scoreboard setting
        if (!SettingsLocale.ENABLED_SCOREBOARD_INGAME.getBoolean()) return new ArrayList<>();

        if (this instanceof SoloFightMatch) {
            MatchState matchState = this.getState();

            if (kit.is(KitRule.BEST_OF_ROUNDS) && matchState.equals(MatchState.STARTING)) {
                if (!SettingsLocale.ENABLED_SCOREBOARD_INGAME_BESTOF.getBoolean()) return new ArrayList<>();
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_BEST_OF.getStringList()), player);
            }

            switch (matchState) {
                case STARTING:
                    if (!SettingsLocale.ENABLED_SCOREBOARD_INGAME_STARTING.getBoolean()) return new ArrayList<>();
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_STARTING.getStringList()), player);
                case IN_ROUND:
                    if (this.getRounds() > 1) {
                        if (!SettingsLocale.ENABLED_SCOREBOARD_INGAME_BESTOF.getBoolean()) return new ArrayList<>();
                        return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_BEST_OF.getStringList()), player);
                    }
                    if (this.getKit().is(KitRule.BOXING)) {
                        if (!SettingsLocale.ENABLED_SCOREBOARD_INGAME_BOXING.getBoolean()) return new ArrayList<>();
                        return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_BOXING.getStringList()), player);
                    }
                    if (!SettingsLocale.ENABLED_SCOREBOARD_INGAME_REGULAR.getBoolean()) return new ArrayList<>();
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME.getStringList()), player);
                case ENDING:
                    if (!SettingsLocale.ENABLED_SCOREBOARD_INGAME_ENDED.getBoolean()) return new ArrayList<>();
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_ENDED.getStringList()), player);
                default:
                    break;
            }
        } else if (this instanceof TeamFightMatch) {
            if (!SettingsLocale.ENABLED_SCOREBOARD_INGAME_TEAM.getBoolean()) return new ArrayList<>();
            return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_TEAM.getStringList()), player);
        } else if (this instanceof FfaFightMatch) {
            if (!SettingsLocale.ENABLED_SCOREBOARD_INGAME_FFA.getBoolean()) return new ArrayList<>();
            return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_FFA.getStringList()), player);
        }

        return null;
    }

    public void removeSpectator(UUID playerUUID, boolean sendMessage) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        Profile profile = API.getProfile(playerUUID);

        if (profile.getMatch() == null) return;
        profile.setState(ProfileState.IN_LOBBY);
        PlayerUtil.reset(player);
        PlayerUtil.teleportToSpawn(playerUUID);
        profile.setMatch(null);

        spectators.remove(playerUUID);

        if (sendMessage) {
            broadcast(MessagesLocale.SPECTATE_STOP, new Replacement("<player>", player.getName()));
        }
    }

    public void setupPlayer(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        Profile profile = API.getProfile(playerUUID);
        profile.setMatch(this);
        profile.setState(ProfileState.IN_GAME);
        PlayerUtil.reset(player);
        Participant participant = getParticipant(playerUUID);
        participant.setLastAttacker(null);
        kit.giveLoadout(participant);
    }

    public void broadcast(MessagesLocale messagesLocale, Replacement... replacements) {
        forEachParticipant(participant -> messagesLocale.send(participant.getPlayerUUID(), replacements));

        forEachSpectator(player -> messagesLocale.send(player.getUniqueId(), replacements));
    }

    public void broadcast(String message) {
        forEachParticipant(participant -> participant.sendMessage(message));

        forEachSpectator(player -> player.sendMessage(CC.color(message)));
    }

    public void checkRules() {
        forEachParticipant(participant -> {
            if (!(this instanceof FfaFightMatch)) {
                if (kit.is(KitRule.DENY_MOVEMENT)) {
                    participant.toggleFreeze();
                }
            }
            if (kit.is(KitRule.SHOW_HP)) {
                if (state.equals(MatchState.STARTING)) {
                    showHealth();
                }
            }

            if (!kit.is(KitRule.SATURATION)) {
                Player player = participant.getPlayer();
                if (player == null) return;
                player.setSaturation(0.0F);
            } else {
                Player player = participant.getPlayer();
                if (player == null) return;
                player.setSaturation(20.0f);
            }
        });

        forEachPlayer(player -> {
            Profile profile = API.getProfile(player);
            profile.handleVisibility();
        });
    }

    public void hideHealth() {
        forEachPlayer(player -> {
            Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
            if (objective != null) {
                objective.unregister();
            }
        });
    }

    public void hideParticipant(Participant participant) {
        forEachParticipant(p -> {
            if (!p.equals(participant)) {
                p.getPlayer().hidePlayer(Neptune.get(), participant.getPlayer());
            }
        });
    }

    public void showParticipant(Participant participant) {
        forEachParticipant(p -> {
            if (!p.equals(participant)) {
                p.getPlayer().showPlayer(Neptune.get(), participant.getPlayer());
            }
        });
    }

    private void showHealth() {
        forEachPlayer(player -> {
            Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

            if (objective == null) {
                objective = player.getScoreboard().registerNewObjective("neptune_health", Criteria.HEALTH, Component.text(CC.color("&c❤")));
            }
            try {
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            } catch (IllegalStateException ignored) {
            }

            player.sendHealthUpdate();
        });
    }

    public void removeEntities() {
        for (Entity entity : new HashSet<>(entities)) {
            if (entity == null) continue;
            entity.remove();
            entities.remove(entity);
        }
    }

    public void setupParticipants() {
        forEachPlayer(player -> setupPlayer(player.getUniqueId()));
    }

    public void sendDeathMessage(Participant deadParticipant) {
        String deathMessage = deadParticipant.getDeathMessage();
        DeathCause deathCause = deadParticipant.getDeathCause();

        if (deathMessage.isEmpty() && deathCause != null) {
            broadcast(
                    deadParticipant.getDeathCause().getMessagesLocale(),
                    new Replacement("<player>", deadParticipant.getNameColored()),
                    new Replacement("<killer>", deadParticipant.getLastAttackerName())
            );
        } else {
            broadcast(deathMessage);
        }
        
        // Play kill sound to the attacker if this was a kill
        if (deadParticipant.getDeathCause() == DeathCause.KILL && deadParticipant.getLastAttacker() != null) {
            Player killer = deadParticipant.getLastAttacker().getPlayer();
            if (killer != null) {
                killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }
    }

    public void teleportToPositions() {
        // This method is called when a score happens in Bridges mode
        // It teleports players back to their spawn positions and resets their inventories
        for (Participant participant : participants) {
            teleportPlayerToPosition(participant);
        }
    }

    public void teleportPlayerToPosition(Participant participant) {
        Player player = participant.getPlayer();
        if (player == null) return;
        
        // Always reset player inventory for Bridges mode when a point is scored
        boolean isBridges = kit.is(KitRule.BRIDGES);
        if (isBridges) {
            // Reset player's inventory
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            
            // Reset health and saturation
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setSaturation(20.0f);
            
            // Clear any potion effects
            player.getActivePotionEffects().forEach(effect -> 
                player.removePotionEffect(effect.getType()));
            
            // Give kit loadout again
            kit.giveLoadout(participant);
        }
        
        // Teleport to appropriate spawn
        if (participant.getColor().equals(ParticipantColor.RED)) {
            player.teleport(arena.getRedSpawn());
        } else {
            player.teleport(arena.getBlueSpawn());
        }
        
        // Update inventory to ensure changes are visible to the player
        if (isBridges) {
            player.updateInventory();
        }
    }

    public abstract void end(Participant loser);

    public abstract void onDeath(Participant participant);

    public abstract void onLeave(Participant participant, boolean quit);

    public abstract void startMatch();

    public abstract void sendEndMessage();

    public abstract void breakBed(Participant participant);

    public abstract void sendTitle(Participant participant, String header, String footer, int duration);
}
