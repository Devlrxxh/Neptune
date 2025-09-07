package dev.lrxh.neptune.feature.party;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.party.request.PartyRequest;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.ClickableComponent;
import dev.lrxh.neptune.providers.clickable.Replacement;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter @Setter
public class Party {

    private final Neptune plugin;

    private UUID leader;

    private final Set<UUID> users;

    private boolean open;
    private int maxUsers;

    private final boolean duelRequest;

    public Party(UUID leader, Neptune plugin) {
        this.plugin = plugin;

        this.leader = leader;

        this.users = ConcurrentHashMap.newKeySet();
        this.users.add(leader);

        this.open = false;
        this.maxUsers = 10;
        this.duelRequest = true;

        setupPlayer(leader);
    }

    /**
     * Gets the current leader's name, or the offline name if they are not online.
     *
     * @return the leader's name, or empty string if not found
     */
    public String getLeaderName() {
        Player player = Bukkit.getPlayer(leader);
        if (player != null) return player.getName();
        return Bukkit.getOfflinePlayer(leader).getName();
    }

    /**
     * Checks if the given UUID belongs to the current leader.
     *
     * @param playerUUID the player's UUID
     * @return true if leader, false otherwise
     */
    public boolean isLeader(UUID playerUUID) {
        return leader.equals(playerUUID);
    }

    /**
     * Sends a clickable invitation message to the target player.
     *
     * @param playerUUID the UUID of the invited player
     */
    public void invite(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        TextComponent accept = new ClickableComponent(
                MessagesLocale.DUEL_ACCEPT.getString().replace("<leader>", getLeaderName()),
                "/party accept " + leader,
                MessagesLocale.PARTY_ACCEPT_HOVER.getString()
        ).build();

        MessagesLocale.PARTY_INVITATION.send(playerUUID,
                new Replacement("<accept>", accept),
                new Replacement("<leader>", getLeaderName()));

        Profile profile = API.getProfile(playerUUID);
        profile.getGameData().addRequest(
                new PartyRequest(leader, this),
                leader,
                ignore -> MessagesLocale.PARTY_EXPIRED.send(leader,
                        new Replacement("<player>", player.getName()))
        );
    }

    /**
     * Accepts an invitation for a given player if the party is not full.
     *
     * @param playerUUID the UUID of the joining player
     */
    public void accept(UUID playerUUID) {
        if (users.size() >= maxUsers) return;
        setupPlayer(playerUUID);
    }

    /**
     * Registers a new player into the party, updating their profile state.
     *
     * @param playerUUID the UUID of the joining player
     */
    private void setupPlayer(UUID playerUUID) {
        Player invitedPlayer = Bukkit.getPlayer(playerUUID);
        if (invitedPlayer == null) return;

        users.add(playerUUID);

        Profile profile = API.getProfile(playerUUID);
        profile.getGameData().setParty(this);
        profile.setState(ProfileState.IN_PARTY);

        if (!playerUUID.equals(leader)) {
            broadcast(MessagesLocale.PARTY_JOINED,
                    new Replacement("<player>", invitedPlayer.getName()));
        }

        profile.getGameData().removeRequest(leader);
    }

    /**
     * Kicks a player from the party and broadcasts a message.
     *
     * @param playerUUID the UUID of the player to kick
     */
    public void kick(UUID playerUUID) {
        broadcast(MessagesLocale.PARTY_KICK,
                new Replacement("<player>", getLeaderName()));
        remove(playerUUID);
    }

    /**
     * Removes a player from the party without broadcasting.
     *
     * @param playerUUID the UUID of the player to remove
     */
    public void remove(UUID playerUUID) {
        Profile profile = API.getProfile(playerUUID);
        users.remove(playerUUID);
        profile.setState(ProfileState.IN_LOBBY);
        profile.getGameData().setParty(null);
    }


    /**
     * Disbands the entire party, resetting all members' states.
     */
    public void disband() {
        broadcast(MessagesLocale.PARTY_DISBANDED);

        forEachMemberAsUUID(uuid -> {
            Profile profile = API.getProfile(uuid);
            profile.getGameData().setParty(null);
            if (profile.getState() == ProfileState.IN_PARTY) {
                profile.setState(ProfileState.IN_LOBBY);
            }
        });

        PartyService.get().removeParty(this);
    }

    /**
     * Sends a message to all party members.
     *
     * @param locale the message template
     * @param replacements placeholder replacements
     */
    public void broadcast(MessagesLocale locale, Replacement... replacements) {
        forEachMemberAsUUID(uuid -> locale.send(uuid, replacements));
    }

    /**
     * Gets a comma-separated list of all party member names.
     *
     * @return member names in string format
     */
    public String getUserNames() {
        StringBuilder stringBuilder = new StringBuilder();
        forEachMemberAsPlayer(player -> {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(MessagesLocale.MATCH_COMMA.getString());
            }
            stringBuilder.append(player.getName());
        });
        return stringBuilder.toString();
    }

    /**
     * Executes an action for every online party member.
     *
     * @param action action to run on each player
     */
    public void forEachMemberAsPlayer(Consumer<Player> action) {
        for (UUID uuid : users) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) action.accept(player);
        }
    }

    /**
     * Executes an action for every party member UUID (regardless of online status).
     *
     * @param action action to run on each UUID
     */
    public void forEachMemberAsUUID(Consumer<UUID> action) {
        users.forEach(action);
    }

    /**
     * Transfers party leadership to another player.
     *
     * @param player the current leader
     * @param target the new leader
     */
    public void transfer(Player player, Player target) {
        this.leader = target.getUniqueId();
        broadcast(MessagesLocale.PARTY_TRANSFER,
                new Replacement("<leader>", player.getName()),
                new Replacement("<target>", target.getName()));
    }

    /**
     * Advertises the party in chat if the leader's cooldown has ended.
     *
     * @return true if the advertisement was sent, false otherwise
     */
    public boolean advertise() {
        Profile leaderProfile = API.getProfile(leader);

        if (!leaderProfile.hasCooldownEnded("party_advertise")) return false;

        leaderProfile.addCooldown("party_advertise", 300_000);
        setOpen(true);

        for (Profile profile : ProfileService.get().profiles.values()) {
            TextComponent join = new ClickableComponent(
                    MessagesLocale.PARTY_ADVERTISE_JOIN.getString(),
                    "/party join " + getLeaderName(),
                    MessagesLocale.PARTY_ADVERTISE_JOIN_HOVER.getString()
                            .replace("<leader>", getLeaderName())
            ).build();

            MessagesLocale.PARTY_ADVERTISE_MESSAGE.send(
                    profile.getPlayerUUID(),
                    new Replacement("<join>", join),
                    new Replacement("<leader>", getLeaderName())
            );
        }
        return true;
    }
}
