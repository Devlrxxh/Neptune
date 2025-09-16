package dev.lrxh.neptune.providers.database.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lrxh.neptune.providers.database.DatabaseService;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<DataDocument> getUserData(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = collection.find(Filters.eq("uuid", playerUUID.toString())).first();
            if (document == null)
                return null;
            return new DataDocument(document);
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<Void> replace(UUID playerUUID, DataDocument newDocument) {
        return CompletableFuture.runAsync(() -> {
            Document document = newDocument.toDocument();
            collection.replaceOne(Filters.eq("uuid", playerUUID.toString()), document,
                    new ReplaceOptions().upsert(true));
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<Void> replace(String playerUUID, DataDocument newDocument) {
        return CompletableFuture.runAsync(() -> {
            Document document = newDocument.toDocument();
            collection.replaceOne(Filters.eq("uuid", playerUUID), document, new ReplaceOptions().upsert(true));
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<List<DataDocument>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
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
        }, DatabaseService.get().getExecutor());
    }
}
