package dev.lrxh.neptune.providers.database;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.utils.Console;
import lombok.Getter;
import org.bson.Document;


@Getter
public class MongoManager {
    public MongoCollection<Document> collection;

    public void connect() {
        String mongoUri = SettingsLocale.MONGO_URI.getString();
        if (mongoUri != null && !mongoUri.isEmpty()) {
            try {
                MongoClient mongoClient = MongoClients.create(new ConnectionString(mongoUri));
                MongoDatabase mongoDatabase = mongoClient.getDatabase(SettingsLocale.MONGO_DATABASE.getString());
                collection = mongoDatabase.getCollection("playerData");
            } catch (Exception e) {
                Console.error("Connecting to MongoDB:" + e.getMessage());
            }
        } else {
            Console.error("MongoDB URI is missing or empty in the config.yml");
        }
    }
}
