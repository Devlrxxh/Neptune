package dev.lrxh.neptune.database.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.database.DataDocument;
import dev.lrxh.neptune.database.IDatabase;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class MongoDatabase implements IDatabase {
    private final Neptune plugin;
    public MongoCollection<Document> collection;

    public MongoDatabase() {
        this.plugin = Neptune.get();
    }

    @Override
    public IDatabase load() {
        if (uri != null && !uri.isEmpty() && !uri.equals("NONE")) {
            try {
                MongoClient mongoClient = MongoClients.create(uri);
                com.mongodb.client.MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
                collection = mongoDatabase.getCollection("playerData");
            } catch (Exception e) {
                ServerUtils.error("Connecting to MongoDB:" + e.getMessage());
            }
        } else {
            ServerUtils.error("MongoDB URI is missing or empty in the config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        return this;
    }

    @Override
    public DataDocument getUserData(UUID playerUUID) {
        Document document = collection.find(Filters.eq("uuid", playerUUID.toString())).first();
        if (document == null) return null;
        return new DataDocument(document);
    }

    @Override
    public void replace(UUID playerUUID, DataDocument newDocument) {
        Document document = newDocument.toDocument();
        collection.replaceOne(Filters.eq("uuid", playerUUID.toString()), document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void replace(String playerUUID, DataDocument newDocument) {
        Document document = newDocument.toDocument();
        collection.replaceOne(Filters.eq("uuid", playerUUID), document, new ReplaceOptions().upsert(true));
    }

    @Override
    public List<DataDocument> getAll() {
        List<DataDocument> allDocuments = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                allDocuments.add(new DataDocument(document));
            }
        } catch (Exception e) {
            ServerUtils.error("Error retrieving documents from MongoDB: " + e.getMessage());
        }
        return allDocuments;
    }
}
