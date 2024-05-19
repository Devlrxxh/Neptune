package dev.lrxh.neptune.profile;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.duel.DuelRequest;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.utils.chatComponent.ChatComponent;
import lombok.Data;
import net.kyori.adventure.text.TextComponent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Data
public class Profile {
    private final UUID playerUUID;
    private String username;
    private ProfileState state;
    private GameData gameData;

    private MongoCollection<Document> collection = Neptune.get().getMongoManager().getCollection();

    public Profile(UUID playerUUID, ProfileState state) {
        this.playerUUID = playerUUID;
        this.state = state;
        this.username = Bukkit.getPlayer(playerUUID).getName();
        this.gameData = new GameData();

        load();
    }

    public void setState(ProfileState profileState) {
        state = profileState;
        VisibilityLogic.handle(playerUUID);
        Neptune.get().getHotbarManager().giveItems(playerUUID);
    }

    public void load() {
        Document document = collection.find(Filters.eq("uuid", playerUUID.toString())).first();

        if (document == null) {
            save();
            return;
        }

        gameData.setMatchHistories(gameData.deserializeHistory(document.getList("history", String.class, new ArrayList<>())));

        Document kitStatistics = (Document) document.get("kitData");

        for (Kit kit : Neptune.get().getKitManager().kits) {
            Document kitDocument = (Document) kitStatistics.get(kit.getName());
            if (kitDocument == null) return;
            KitData profileKitData = new KitData();
            profileKitData.setCurrentStreak(kitDocument.getInteger("WIN_STREAK_CURRENT", 0));
            profileKitData.setWins(kitDocument.getInteger("WINS", 0));
            profileKitData.setLosses(kitDocument.getInteger("LOSSES", 0));
            profileKitData.setBestStreak(kitDocument.getInteger("WIN_STREAK_BEST", 0));
            profileKitData.setKit(Objects.equals(kitDocument.getString("kit"), "") ? kit.getItems() : ItemUtils.deserialize(kitDocument.getString("kit")));

            gameData.getKitData().put(kit, profileKitData);
        }
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", playerUUID.toString());
        document.put("username", username);

        Document kitStatsDoc = new Document();

        document.put("history", gameData.serializeHistory());

        for (Kit kit : Neptune.get().getKitManager().kits) {
            KitData entry = new KitData();
            Document kitStatisticsDocument = new Document();
            kitStatisticsDocument.put("WIN_STREAK_CURRENT", entry.getCurrentStreak());
            kitStatisticsDocument.put("WINS", entry.getWins());
            kitStatisticsDocument.put("LOSSES", entry.getLosses());
            kitStatisticsDocument.put("WIN_STREAK_BEST", entry.getBestStreak());
            kitStatisticsDocument.put("kit", entry.getKit() == null || entry.getKit().isEmpty() ? "" : ItemUtils.serialize(entry.getKit()));

            kitStatsDoc.put(kit.getName(), kitStatisticsDocument);
        }

        document.put("kitData", kitStatsDoc);

        collection.replaceOne(Filters.eq("uuid", playerUUID.toString()), document, new ReplaceOptions().upsert(true));
    }

    public void sendDuel(DuelRequest duelRequest) {
        Player sender = Bukkit.getPlayer(duelRequest.getSender());
        if (sender == null) return;

        gameData.setDuelRequest(duelRequest);

        TextComponent accept =
                Neptune.get().getVersionHandler().getChatComponent().create
                        (new ChatComponent(MessagesLocale.DUEL_ACCEPT.getString(),
                                MessagesLocale.DUEL_ACCEPT_HOVER.getString(), "/duel accept"));

        TextComponent deny =
                Neptune.get().getVersionHandler().getChatComponent().create(
                        new ChatComponent(MessagesLocale.DUEL_DENY.getString(),
                                MessagesLocale.DUEL_DENY_HOVER.getString(), "/duel deny"));

        MessagesLocale.DUEL_REQUEST_RECEIVER.send(playerUUID,
                new Replacement("<accept>", accept),
                new Replacement("<deny>", deny),
                new Replacement("<kit>", duelRequest.getKit().getDisplayName()),
                new Replacement("<arena>", duelRequest.getArena().getDisplayName()),
                new Replacement("<sender>", sender.getName()));
    }

    public void acceptDuel() {
        Neptune.get().getQueueManager().remove(playerUUID);
        DuelRequest duelRequest = gameData.getDuelRequest();

        Participant participant1 =
                new Participant(duelRequest.getSender());

        Participant participant2 =
                new Participant(playerUUID);

        List<Participant> participants = Arrays.asList(participant1, participant2);

        Neptune.get().getMatchManager().startMatch(participants, duelRequest.getKit(),
                duelRequest.getArena(), true, duelRequest.getRounds());

        gameData.setDuelRequest(null);
    }

    public Match getMatch() {
        return gameData.getMatch();
    }

    public void setMatch(Match match) {
        gameData.setMatch(match);
    }
}
