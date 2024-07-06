package dev.lrxh.neptune.profile;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.duel.DuelRequest;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.providers.clickable.ClickableComponent;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bson.Document;
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
    private MongoCollection<Document> collection;

    public Profile(UUID playerUUID, Neptune plugin) {
        this.plugin = Neptune.get();
        this.playerUUID = playerUUID;
        this.state = ProfileState.IN_LOBBY;

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        this.username = player.getName();
        this.gameData = new GameData();
        this.collection = plugin.getMongoManager().getCollection();

        for (Kit kit : plugin.getKitManager().kits) {
            KitData kitData = new KitData();
            gameData.getKitData().put(kit, kitData);
            kitData.setPlugin(plugin);
        }

        load();
    }

    public void setState(ProfileState profileState) {
        state = profileState;
        VisibilityLogic.handle(playerUUID);
        plugin.getHotbarManager().giveItems(playerUUID);
    }

    public void load() {
        Document document = collection.find(Filters.eq("uuid", playerUUID.toString())).first();

        if (document == null) {
            save();
            return;
        }

        gameData.setMatchHistories(gameData.deserializeHistory(document.getList("history", String.class, new ArrayList<>())));

        Document kitStatistics = (Document) document.get("kitData");

        for (Kit kit : plugin.getKitManager().kits) {
            Document kitDocument = (Document) kitStatistics.get(kit.getName());
            if (kitDocument == null) return;
            KitData profileKitData = gameData.getKitData().get(kit);
            profileKitData.setCurrentStreak(kitDocument.getInteger("WIN_STREAK_CURRENT", 0));
            profileKitData.setWins(kitDocument.getInteger("WINS", 0));
            profileKitData.setLosses(kitDocument.getInteger("LOSSES", 0));
            profileKitData.setBestStreak(kitDocument.getInteger("WIN_STREAK_BEST", 0));
            profileKitData.setKitLoadout(Objects.equals(kitDocument.getString("kit"), "") ? kit.getItems() : ItemUtils.deserialize(kitDocument.getString("kit")));
            profileKitData.setDivision(plugin.getDivisionManager().getDivisionByWinCount(profileKitData.getWins()));
        }
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", playerUUID.toString());
        document.put("username", username);

        Document kitStatsDoc = new Document();

        document.put("history", gameData.serializeHistory());

        for (Kit kit : plugin.getKitManager().kits) {
            KitData entry = gameData.getKitData().get(kit);
            Document kitStatisticsDocument = new Document();
            kitStatisticsDocument.put("WIN_STREAK_CURRENT", entry.getCurrentStreak());
            kitStatisticsDocument.put("WINS", entry.getWins());
            kitStatisticsDocument.put("LOSSES", entry.getLosses());
            kitStatisticsDocument.put("WIN_STREAK_BEST", entry.getBestStreak());
            kitStatisticsDocument.put("kit", entry.getKitLoadout() == null || entry.getKitLoadout().isEmpty() ? "" : ItemUtils.serialize(entry.getKitLoadout()));
            entry.setDivision(plugin.getDivisionManager().getDivisionByWinCount(entry.getWins()));

            kitStatsDoc.put(kit.getName(), kitStatisticsDocument);
        }

        document.put("kitData", kitStatsDoc);

        collection.replaceOne(Filters.eq("uuid", playerUUID.toString()), document, new ReplaceOptions().upsert(true));
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

        if (!party.getLeader().equals(playerUUID)) {
            party.broadcast(MessagesLocale.PARTY_LEFT,
                    new Replacement("<player>", username));
            party.remove(playerUUID);
        } else {
            party.disband();
        }
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