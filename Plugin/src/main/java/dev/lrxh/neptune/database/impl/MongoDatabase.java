package dev.lrxh.neptune.database.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoDatabase implements IDatabase {
    public MongoCollection<Document> collection;

    @Override
    public IDatabase load() {
        try {
            MongoClient mongoClient = MongoClients.create(uri);
            com.mongodb.client.MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
            collection = mongoDatabase.getCollection("playerData");
        } catch (Exception e) {
            ServerUtils.error("Connecting to MongoDB:" + e.getMessage());
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
