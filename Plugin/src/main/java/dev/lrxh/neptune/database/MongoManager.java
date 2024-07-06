package dev.lrxh.neptune.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;


@Getter
public class MongoManager {
    private final Neptune plugin;
    public MongoCollection<Document> collection;

    public MongoManager() {
        this.plugin = Neptune.get();
        connect();
    }

    public void connect() {
        String mongoUri = SettingsLocale.MONGO_URI.getString();
        if (mongoUri != null && !mongoUri.isEmpty() && !mongoUri.equals("NONE")) {
            try {
                MongoClient mongoClient = MongoClients.create(mongoUri);
                MongoDatabase mongoDatabase = mongoClient.getDatabase(SettingsLocale.MONGO_DATABASE.getString());
                collection = mongoDatabase.getCollection("playerData");
            } catch (Exception e) {
                ServerUtils.error("Connecting to MongoDB:" + e.getMessage());
            }
        } else {
            ServerUtils.error("MongoDB URI is missing or empty in the config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }
}
