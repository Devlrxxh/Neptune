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

import java.util.ArrayList;
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
    private PlayerData playerData = new PlayerData();
    private MongoCollection<Document> collection = Neptune.get().getMongoManager().getCollection();

    public Profile(UUID playerUUID, ProfileState state) {
        this.playerUUID = playerUUID;
        this.state = state;
        this.username = Objects.requireNonNull(Bukkit.getPlayer(playerUUID)).getName();
        this.match = null;
        this.matchSnapshot = null;

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

        Document kitStatistics = (Document) document.get("kitData");

        for (String key : kitStatistics.keySet()) {
            Document kitDocument = (Document) kitStatistics.get(key);
            Kit kit = Neptune.get().getKitManager().getKitByName(key);

            if (kit != null) {
                KitData profileKitData = new KitData();
                profileKitData.setUnrankedElo(kitDocument.getInteger("unrankedElo"));
                profileKitData.setRankedElo(kitDocument.getInteger("rankedElo"));
                profileKitData.setUnrankedWins(kitDocument.getInteger("unrankedWins"));
                profileKitData.setRankedWins(kitDocument.getInteger("rankedWins"));
                profileKitData.setUnrankedLosses(kitDocument.getInteger("unrankedLosses"));
                profileKitData.setRankedLosses(kitDocument.getInteger("rankedLosses"));
                profileKitData.setUnrankedStreak(kitDocument.getInteger("unrankedStreak"));
                profileKitData.setRankedStreak(kitDocument.getInteger("rankedStreak"));
                profileKitData.setElo(kitDocument.getInteger("elo"));
                profileKitData.setKit(kitDocument.getString("kit").isEmpty() ? new ArrayList<>() : ItemUtils.deserializeItemStacks(kitDocument.getString("kit")));

                playerData.getKitData().put(kit, profileKitData);
            }
        }

    }

    public void save() {
        Document document = new Document();
        document.put("uuid", playerUUID.toString());
        document.put("username", username);

        Document kitStatsDoc = new Document();

        for (Map.Entry<Kit, KitData> entry : playerData.getKitData().entrySet()) {
            Document kitStatisticsDocument = new Document();
            kitStatisticsDocument.put("elo", entry.getValue().getElo());
            kitStatisticsDocument.put("unrankedElo", entry.getValue().getUnrankedElo());
            kitStatisticsDocument.put("rankedElo", entry.getValue().getRankedElo());
            kitStatisticsDocument.put("unrankedWins", entry.getValue().getUnrankedWins());
            kitStatisticsDocument.put("rankedWins", entry.getValue().getRankedWins());
            kitStatisticsDocument.put("unrankedLosses", entry.getValue().getUnrankedLosses());
            kitStatisticsDocument.put("rankedLosses", entry.getValue().getRankedLosses());
            kitStatisticsDocument.put("unrankedStreak", entry.getValue().getUnrankedStreak());
            kitStatisticsDocument.put("rankedStreak", entry.getValue().getRankedStreak());
            kitStatisticsDocument.put("kit", entry.getValue().getKit() == null ? "" : ItemUtils.serializeItemStacks(entry.getValue().getKit()));

            kitStatsDoc.put(entry.getKey().getName(), kitStatisticsDocument);

        }

        document.put("kitData", kitStatsDoc);


        collection.replaceOne(Filters.eq("uuid", playerUUID.toString()), document, new ReplaceOptions().upsert(true));
    }
}
