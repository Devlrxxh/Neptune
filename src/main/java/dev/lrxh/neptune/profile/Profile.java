package dev.lrxh.neptune.profile;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchSnapshot;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.data.PlayerData;
import dev.lrxh.neptune.utils.ItemUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;

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
            this.data.getKitData().put(kit, new KitData(kit));
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
                KitData profileKitData = new KitData(kit);
                profileKitData.setCurrentRankedStreak(kitDocument.getInteger("RANKED_WIN_STREAK_CURRENT", 0));
                profileKitData.setCurrentUnrankedStreak(kitDocument.getInteger("UNRANKED_WIN_STREAK_CURRENT", 0));
                profileKitData.setUnrankedWins(kitDocument.getInteger("UNRANKED_WINS", 0));
                profileKitData.setRankedWins(kitDocument.getInteger("RANKED_WINS", 0));
                profileKitData.setUnrankedLosses(kitDocument.getInteger("UNRANKED_LOSSES", 0));
                profileKitData.setRankedLosses(kitDocument.getInteger("RANKED_LOSSES", 0));
                profileKitData.setUnrankedBestStreak(kitDocument.getInteger("UNRANKED_WIN_STREAK_BEST", 0));
                profileKitData.setRankedBestStreak(kitDocument.getInteger("RANKED_WIN_STREAK_BEST", 0));
                profileKitData.setElo(kitDocument.getInteger("elo", 1000));
                profileKitData.setKit(kitDocument.getString("kit").isEmpty() ? kit.getItems() : ItemUtils.deserialize(kitDocument.getString("kit")));

                data.getKitData().put(kit, profileKitData);
            }
        }

    }

    public void save() {
        Document document = new Document();
        document.put("uuid", playerUUID.toString());
        document.put("username", username);

        Document kitStatsDoc = new Document();

        for (Map.Entry<Kit, KitData> entry : data.getKitData().entrySet()) {
            Document kitStatisticsDocument = new Document();
            kitStatisticsDocument.put("elo", entry.getValue().getElo());
            kitStatisticsDocument.put("UNRANKED_WIN_STREAK_CURRENT", entry.getValue().getCurrentUnrankedStreak());
            kitStatisticsDocument.put("RANKED_WIN_STREAK_CURRENT", entry.getValue().getCurrentRankedStreak());
            kitStatisticsDocument.put("UNRANKED_WINS", entry.getValue().getUnrankedWins());
            kitStatisticsDocument.put("RANKED_WINS", entry.getValue().getRankedWins());
            kitStatisticsDocument.put("UNRANKED_LOSSES", entry.getValue().getUnrankedLosses());
            kitStatisticsDocument.put("RANKED_LOSSES", entry.getValue().getRankedLosses());
            kitStatisticsDocument.put("UNRANKED_WIN_STREAK_BEST", entry.getValue().getUnrankedBestStreak());
            kitStatisticsDocument.put("RANKED_WIN_STREAK_BEST", entry.getValue().getRankedBestStreak());
            kitStatisticsDocument.put("kit", ItemUtils.serialize(entry.getValue().getKit()));

            kitStatsDoc.put(entry.getKey().getName(), kitStatisticsDocument);

        }

        document.put("kitData", kitStatsDoc);


        collection.replaceOne(Filters.eq("uuid", playerUUID.toString()), document, new ReplaceOptions().upsert(true));
    }
}
