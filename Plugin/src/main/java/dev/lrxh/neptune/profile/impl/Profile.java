package dev.lrxh.neptune.profile.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.cosmetics.impl.KillEffect;
import dev.lrxh.neptune.database.impl.DataDocument;
import dev.lrxh.neptune.duel.DuelRequest;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.profile.data.*;
import dev.lrxh.neptune.providers.clickable.ClickableComponent;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class Profile {
    private final UUID playerUUID;
    public boolean cooldown;
    private String username;
    private ProfileState state;
    private Neptune plugin;
    private GameData gameData;
    private SettingData settingData;
    private Visibility visibility;

    public Profile(String name, UUID uuid, Neptune plugin) {
        this.plugin = plugin;
        this.username = name;
        this.playerUUID = uuid;
        this.state = ProfileState.IN_LOBBY;
        this.gameData = new GameData(plugin);
        this.settingData = new SettingData(plugin);
        this.visibility = new Visibility(plugin, playerUUID);

        load();
    }


    public void handleVisibility() {
        visibility.handle();
    }

    public boolean hasState(ProfileState profileState) {
        return state.equals(profileState);
    }

    public boolean hasState(ProfileState... profileStates) {
        for (ProfileState profileState : profileStates) {
            if (profileState.equals(state)) {
                return true;
            }
        }
        return false;
    }

    public void setState(ProfileState profileState) {
        state = profileState;
        handleVisibility();
        plugin.getHotbarManager().giveItems(getPlayer());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public void load() {
        DataDocument dataDocument = plugin.getDatabaseManager().getDatabase().getUserData(playerUUID);

        if (dataDocument == null) {
            save();
        }

        if (dataDocument == null) return;

        gameData.setMatchHistories(gameData.deserializeHistory(dataDocument.getList("history", new ArrayList<>())));

        DataDocument kitStatistics = dataDocument.getDataDocument("kitData");
        DataDocument settings = dataDocument.getDataDocument("settings");

        for (Kit kit : plugin.getKitManager().kits) {
            DataDocument kitDocument = kitStatistics.getDataDocument(kit.getName());
            if (kitDocument == null) return;
            KitData profileKitData = gameData.getKitData().get(kit);
            profileKitData.setCurrentStreak(kitDocument.getInteger("WIN_STREAK_CURRENT", 0));
            profileKitData.setWins(kitDocument.getInteger("WINS", 0));
            profileKitData.setLosses(kitDocument.getInteger("LOSSES", 0));
            profileKitData.setBestStreak(kitDocument.getInteger("WIN_STREAK_BEST", 0));
            profileKitData.setKitLoadout(Objects.equals(kitDocument.getString("kit"), "") ? kit.getItems() : ItemUtils.deserialize(kitDocument.getString("kit")));
            profileKitData.updateDivision();
        }

        gameData.getGlobalStats().setCurrentStreak(kitStatistics.getInteger("GLOBAL_WIN_STREAK_CURRENT", gameData.countGlobalWins()));
        gameData.getGlobalStats().setWins(kitStatistics.getInteger("GLOBAL_WINS", gameData.countGlobalLosses()));
        gameData.getGlobalStats().setLosses(kitStatistics.getInteger("GLOBAL_LOSSES", gameData.countGlobalCurrentStreak()));

        settingData.setPlayerVisibility(settings.getBoolean("showPlayers", true));
        settingData.setAllowSpectators(settings.getBoolean("allowSpectators", true));
        settingData.setAllowDuels(settings.getBoolean("allowDuels", true));
        settingData.setAllowParty(settings.getBoolean("allowParty", true));
        settingData.setMaxPing(settings.getInteger("maxPing", 350));
        settingData.setKillEffect(KillEffect.valueOf(settings.getString("killEffect", "NONE")));
        settingData.setMenuSound(settings.getBoolean("menuSound", false));
        settingData.setKillMessagePackage(plugin.getCosmeticManager().getDeathMessagePackage(settings.getString("deathMessagePackage")));
        gameData.setLastKit(settings.getString("lastKit", ""));
    }

    public void save() {
        DataDocument dataDocument = new DataDocument();
        dataDocument.put("uuid", playerUUID.toString());

        dataDocument.put("username", username);

        DataDocument kitStatsDoc = new DataDocument();

        dataDocument.put("history", gameData.serializeHistory());


        for (Kit kit : plugin.getKitManager().kits) {
            DataDocument kitStatisticsDocument = new DataDocument();
            KitData entry = gameData.getKitData().get(kit);
            kitStatisticsDocument.put("WIN_STREAK_CURRENT", entry.getCurrentStreak());
            kitStatisticsDocument.put("WINS", entry.getWins());
            kitStatisticsDocument.put("LOSSES", entry.getLosses());
            kitStatisticsDocument.put("WIN_STREAK_BEST", entry.getBestStreak());
            kitStatisticsDocument.put("kit", entry.getKitLoadout() == null || entry.getKitLoadout().isEmpty() ? "" : ItemUtils.serialize(entry.getKitLoadout()));
            entry.updateDivision();
            kitStatsDoc.put(kit.getName(), kitStatisticsDocument);
        }

        kitStatsDoc.put("GLOBAL_WINS", gameData.getGlobalStats().getWins());
        kitStatsDoc.put("GLOBAL_LOSSES", gameData.getGlobalStats().getLosses());
        kitStatsDoc.put("GLOBAL_WIN_STREAK_CURRENT", gameData.getGlobalStats().getCurrentStreak());

        dataDocument.put("kitData", kitStatsDoc);

        DataDocument settingsDoc = new DataDocument();

        settingsDoc.put("showPlayers", settingData.isPlayerVisibility());
        settingsDoc.put("allowSpectators", settingData.isAllowSpectators());
        settingsDoc.put("allowDuels", settingData.isAllowDuels());
        settingsDoc.put("allowParty", settingData.isAllowParty());
        settingsDoc.put("maxPing", settingData.getMaxPing());
        settingsDoc.put("killEffect", settingData.getKillEffect().toString());
        settingsDoc.put("menuSound", settingData.isMenuSound());
        settingsDoc.put("deathMessagePackage", settingData.getKillMessagePackage().getName());

        dataDocument.put("settings", settingsDoc);

        plugin.getDatabaseManager().getDatabase().replace(playerUUID, dataDocument);
    }

    public void sendDuel(DuelRequest duelRequest) {
        UUID senderUUID = duelRequest.getSender();

        Player sender = Bukkit.getPlayer(senderUUID);
        if (sender == null) return;

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        MessagesLocale.DUEL_REQUEST_SENDER.send(sender.getUniqueId(),
                new Replacement("<receiver>", username),
                new Replacement("<kit>", duelRequest.getKit().getDisplayName()),
                new Replacement("<rounds>", String.valueOf(1)),
                new Replacement("<arena>", duelRequest.getArena().getDisplayName()));

        gameData.addRequest(duelRequest, senderUUID, ignore -> MessagesLocale.DUEL_EXPIRED.send(senderUUID, new Replacement("<player>", player.getName())));

        TextComponent accept =
                new ClickableComponent(MessagesLocale.DUEL_ACCEPT.getString(), "/duel accept " + duelRequest.getSender().toString(), MessagesLocale.DUEL_ACCEPT_HOVER.getString()).build();

        TextComponent deny =
                new ClickableComponent(MessagesLocale.DUEL_DENY.getString(), "/duel deny " + duelRequest.getSender().toString(), MessagesLocale.DUEL_DENY_HOVER.getString()).build();

        MessagesLocale.DUEL_REQUEST_RECEIVER.send(playerUUID,
                new Replacement("<accept>", accept),
                new Replacement("<deny>", deny),
                new Replacement("<kit>", duelRequest.getKit().getDisplayName()),
                new Replacement("<arena>", duelRequest.getArena().getDisplayName()),
                new Replacement("<rounds>", String.valueOf(duelRequest.getRounds())),
                new Replacement("<sender>", sender.getName()));
    }

    public void createParty() {
        if (gameData.getParty() != null) {
            MessagesLocale.PARTY_ALREADY_IN.send(playerUUID);
            return;
        }

        plugin.getAPI().getProfile(playerUUID).getGameData().setParty(new Party(playerUUID, plugin));
        MessagesLocale.PARTY_CREATE.send(playerUUID);
    }

    public void disband() {
        Party party = gameData.getParty();

        if (party == null) {
            return;
        }

        if (party.getLeader().equals(playerUUID)) {
            party.disband();
            return;
        }
        party.broadcast(MessagesLocale.PARTY_LEFT,
                new Replacement("<player>", username));
        party.remove(playerUUID);
    }

    public void acceptDuel(UUID senderUUID) {
        ((DuelRequest) gameData.getRequests().get(senderUUID)).start(playerUUID);
        gameData.removeRequest(senderUUID);
    }

    public Match getMatch() {
        return gameData.getMatch();
    }


    public void setMatch(Match match) {
        gameData.setMatch(match);
    }
}