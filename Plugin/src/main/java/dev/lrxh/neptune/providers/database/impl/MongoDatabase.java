package dev.lrxh.neptune.providers.database.impl;

import com.mongodb.MongoException;
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
    private MongoCollection<Document> collection;
    private MongoClient mongoClient;

    @Override
    public IDatabase load() {
        try {
            mongoClient = MongoClients.create(uri);
            collection = mongoClient.getDatabase(database).getCollection("playerData");
        } catch (Exception e) {
            ServerUtils.error("Failed to connect to MongoDB: " + e.getMessage());
        }
        return this;
    }

    @Override
    public CompletableFuture<DataDocument> getUserData(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            if (collection == null) {
                ServerUtils.error("MongoDB collection is not initialized!");
                return null;
            }
            try {
                Document document = collection.find(Filters.eq("uuid", playerUUID.toString())).first();
                return (document != null) ? new DataDocument(document) : null;
            } catch (MongoException e) {
                ServerUtils.error("Error fetching user data from MongoDB: " + e.getMessage());
                return null;
            }
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<Void> replace(UUID playerUUID, DataDocument newDocument) {
        return CompletableFuture.runAsync(() -> {
            if (collection == null) {
                ServerUtils.error("MongoDB collection is not initialized!");
                return;
            }
            try {
                Document document = newDocument.toDocument();
                collection.replaceOne(Filters.eq("uuid", playerUUID.toString()), document,
                        new ReplaceOptions().upsert(true));
            } catch (MongoException e) {
                ServerUtils.error("Error replacing user data in MongoDB: " + e.getMessage());
            }
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<Void> replace(String playerUUID, DataDocument newDocument) {
        return CompletableFuture.runAsync(() -> {
            if (collection == null) {
                ServerUtils.error("MongoDB collection is not initialized!");
                return;
            }
            try {
                Document document = newDocument.toDocument();
                collection.replaceOne(Filters.eq("uuid", playerUUID), document, new ReplaceOptions().upsert(true));
            } catch (MongoException e) {
                ServerUtils.error("Error replacing user data in MongoDB: " + e.getMessage());
            }
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<List<DataDocument>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<DataDocument> allDocuments = new ArrayList<>();
            if (collection == null) {
                ServerUtils.error("MongoDB collection is not initialized!");
                return allDocuments;
            }
            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                while (cursor.hasNext()) {
                    allDocuments.add(new DataDocument(cursor.next()));
                }
            } catch (MongoException e) {
                ServerUtils.error("Error retrieving documents from MongoDB: " + e.getMessage());
            }
            return allDocuments;
        }, DatabaseService.get().getExecutor());
    }
}
