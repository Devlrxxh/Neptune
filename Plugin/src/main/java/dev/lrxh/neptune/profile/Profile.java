package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.database.DataDocument;
import dev.lrxh.neptune.duel.DuelRequest;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.providers.clickable.ClickableComponent;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

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

    public Profile(Player player, Neptune plugin) {
        this.plugin = Neptune.get();
        this.playerUUID = player.getUniqueId();
        this.state = ProfileState.IN_LOBBY;
        this.gameData = new GameData(plugin);
        this.settingData = new SettingData();
        this.username = player.getName();

        load();
    }

    public void handleVisibility() {
        VisibilityLogic.handle(playerUUID);
    }

    public void setState(ProfileState profileState) {
        state = profileState;
        handleVisibility();
        plugin.getHotbarManager().giveItems(playerUUID);
    }

    public void load() {
        DataDocument dataDocument = plugin.getDatabaseManager().getDatabase().getUserData(playerUUID);

        if (dataDocument == null) {
            save();
        }

        if (dataDocument == null) return;

        gameData.setMatchHistories(gameData.deserializeHistory(dataDocument.getList("history", new ArrayList<>())));

        DataDocument kitStatistics = dataDocument.getDataDocument("kitData");
        DataDocument settingsStatistics = dataDocument.getDataDocument("settings");

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

        settingData.setPlayerVisibility(settingsStatistics.getBoolean("showPlayers", true));
        settingData.setAllowSpectators(settingsStatistics.getBoolean("allowSpectators", true));
        settingData.setAllowDuels(settingsStatistics.getBoolean("allowDuels", true));
        settingData.setAllowParty(settingsStatistics.getBoolean("allowParty", true));
        settingData.setMaxPing(settingsStatistics.getInteger("maxPing", 350));

    }

    public void save() {
        DataDocument dataDocument = new DataDocument();
        dataDocument.put("uuid", playerUUID.toString());

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            dataDocument.put("username", player.getName());
        } else {
            dataDocument.put("username", "???");
        }

        DataDocument kitStatsDoc = new DataDocument();

        dataDocument.put("history", gameData.serializeHistory());

        for (Kit kit : plugin.getKitManager().kits) {
            KitData entry = gameData.getKitData().get(kit);
            DataDocument kitStatisticsDocument = new DataDocument();
            kitStatisticsDocument.put("WIN_STREAK_CURRENT", entry.getCurrentStreak());
            kitStatisticsDocument.put("WINS", entry.getWins());
            kitStatisticsDocument.put("LOSSES", entry.getLosses());
            kitStatisticsDocument.put("WIN_STREAK_BEST", entry.getBestStreak());
            kitStatisticsDocument.put("kit", entry.getKitLoadout() == null || entry.getKitLoadout().isEmpty() ? "" : ItemUtils.serialize(entry.getKitLoadout()));
            entry.updateDivision();

            kitStatsDoc.put(kit.getName(), kitStatisticsDocument);
        }
        dataDocument.put("kitData", kitStatsDoc);

        DataDocument settingsDoc = new DataDocument();

        settingsDoc.put("showPlayers", settingData.isPlayerVisibility());
        settingsDoc.put("allowSpectators", settingData.isAllowSpectators());
        settingsDoc.put("allowDuels", settingData.isAllowDuels());
        settingsDoc.put("allowParty", settingData.isAllowParty());
        settingsDoc.put("maxPing", settingData.getMaxPing());

        dataDocument.put("settings", settingsDoc);

        plugin.getDatabaseManager().getDatabase().replace(playerUUID, dataDocument);
    }

    public void sendDuel(DuelRequest duelRequest, UUID senderUUID) {
        Player sender = Bukkit.getPlayer(duelRequest.getSender());
        if (sender == null) return;

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

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

        plugin.getProfileManager().getByUUID(playerUUID).getGameData().setParty(new Party(playerUUID, plugin));
        MessagesLocale.PARTY_CREATE.send(playerUUID);
    }

    public void disband() {
        Party party = gameData.getParty();

        if (party == null) {
            MessagesLocale.PARTY_NOT_IN.send(playerUUID);
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
        plugin.getQueueManager().remove(playerUUID);
        DuelRequest duelRequest = (DuelRequest) gameData.getRequests().get(senderUUID);

        Participant participant1 =
                new Participant(duelRequest.getSender());

        Participant participant2 =
                new Participant(playerUUID);

        List<Participant> participants = Arrays.asList(participant1, participant2);

        plugin.getMatchManager().startMatch(participants, duelRequest.getKit(),
                duelRequest.getArena(), true, duelRequest.getRounds());

        gameData.removeRequest(senderUUID);
    }

    public Match getMatch() {
        return gameData.getMatch();
    }


    public void setMatch(Match match) {
        gameData.setMatch(match);
    }
}