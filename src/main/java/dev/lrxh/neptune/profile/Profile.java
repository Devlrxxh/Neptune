package dev.lrxh.neptune.profile;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchSnapshot;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.data.PlayerData;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.duel.DuelRequest;
import dev.lrxh.neptune.utils.ItemUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Data
public class Profile {
    private final UUID playerUUID;
    private String username;
    private ProfileState state;
    private Match match;
    private MatchSnapshot matchSnapshot;
    private PlayerData data = new PlayerData();
    private Kit kitEditor;
    private MongoCollection<Document> collection = Neptune.get().getMongoManager().getCollection();

    public Profile(UUID playerUUID, ProfileState state) {
        this.playerUUID = playerUUID;
        this.state = state;
        this.username = Objects.requireNonNull(Bukkit.getPlayer(playerUUID)).getName();
        this.match = null;
        this.matchSnapshot = null;
        this.kitEditor = null;
        for (Kit kit : Neptune.get().getKitManager().kits) {
            this.data.getKitData().put(kit, new KitData());
        }

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
        username = document.getString("username");
        Document kitStatistics = (Document) document.get("kitData");

        for (String key : kitStatistics.keySet()) {
            Document kitDocument = (Document) kitStatistics.get(key);
            Kit kit = Neptune.get().getKitManager().getKitByName(key);

            if (kit != null) {
                KitData profileKitData = new KitData();
                profileKitData.setCurrentStreak(kitDocument.getInteger("WIN_STREAK_CURRENT", 0));
                profileKitData.setWins(kitDocument.getInteger("WINS", 0));
                profileKitData.setLosses(kitDocument.getInteger("LOSSES", 0));
                profileKitData.setBestStreak(kitDocument.getInteger("WIN_STREAK_BEST", 0));
                profileKitData.setElo(kitDocument.getInteger("elo", 1000));
                profileKitData.setKit(Objects.equals(kitDocument.getString("kit"), "") ? kit.getItems() : ItemUtils.deserialize(kitDocument.getString("kit")));

                data.getKitData().put(kit, profileKitData);
            }
        }
    }

    public void sendDuel(DuelRequest duelRequest) {
        Player sender = Bukkit.getPlayer(duelRequest.getSender());
        if (sender == null) return;

        data.setDuelRequest(duelRequest);

        TextComponent accept = Component.text(MessagesLocale.DUEL_ACCEPT.getString())
                .clickEvent(ClickEvent.runCommand("/duel accept"))
                .hoverEvent(HoverEvent.showText(Component.text(MessagesLocale.DUEL_ACCEPT_HOVER.getString())));
        TextComponent deny = Component.text(MessagesLocale.DUEL_DENY.getString())
                .clickEvent(ClickEvent.runCommand("/duel deny"))
                .hoverEvent(HoverEvent.showText(Component.text(MessagesLocale.DUEL_DENY_HOVER.getString())));

        MessagesLocale.DUEL_REQUEST_RECEIVER.send(playerUUID,
                new Replacement("<accept>", accept),
                new Replacement("<deny>", deny),
                new Replacement("<kit>", duelRequest.getKit().getDisplayName()),
                new Replacement("<arena>", duelRequest.getArena().getDisplayName()),
                new Replacement("<sender>", sender.getName()));
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", playerUUID.toString());
        document.put("username", username);

        Document kitStatsDoc = new Document();

        for (Map.Entry<Kit, KitData> entry : data.getKitData().entrySet()) {
            Document kitStatisticsDocument = new Document();
            kitStatisticsDocument.put("elo", entry.getValue().getElo());
            kitStatisticsDocument.put("WIN_STREAK_CURRENT", entry.getValue().getCurrentStreak());
            kitStatisticsDocument.put("WINS", entry.getValue().getWins());
            kitStatisticsDocument.put("LOSSES", entry.getValue().getLosses());
            kitStatisticsDocument.put("WIN_STREAK_BEST", entry.getValue().getBestStreak());
            kitStatisticsDocument.put("kit", entry.getValue().getKit().isEmpty() ? "" : ItemUtils.serialize(entry.getValue().getKit()));

            kitStatsDoc.put(entry.getKey().getName(), kitStatisticsDocument);

        }

        document.put("kitData", kitStatsDoc);


        collection.replaceOne(Filters.eq("uuid", playerUUID.toString()), document, new ReplaceOptions().upsert(true));
    }
}
