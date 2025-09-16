package dev.lrxh.neptune.providers.database.impl;

import com.google.gson.JsonParseException;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.providers.database.DatabaseService;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLiteDatabase implements IDatabase {
    private final String dbPath;
    private Connection connection;

    public SQLiteDatabase() {
        this.dbPath = "jdbc:sqlite:" + Neptune.get().getDataFolder() + "/neptune.db";
    }

    @Override
    public IDatabase load() {
        try {
            connection = DriverManager.getConnection(dbPath);
            createTableIfNotExists();
        } catch (SQLException e) {
            ServerUtils.error("Failed to connect to SQLite database: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(Neptune.get());
        }
        return this;
    }

    @Override
    public CompletableFuture<DataDocument> getUserData(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT * FROM playerData WHERE uuid=?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerUUID.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String dataString = resultSet.getString("data");
                    if (isValidJSON(dataString)) {
                        return new DataDocument(dataString);
                    } else {
                        ServerUtils.error("Invalid JSON data for UUID: " + playerUUID);
                    }
                }
            } catch (SQLException e) {
                ServerUtils.error("Error fetching user data from SQLite: " + e.getMessage());
            }
            return null;
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<Void> replace(UUID playerUUID, DataDocument newDocument) {
        return CompletableFuture.runAsync(() -> {
            String updateQuery = "REPLACE INTO playerData (uuid, data) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, playerUUID.toString());
                statement.setString(2, newDocument.toDocument().toJson());
                statement.executeUpdate();
            } catch (SQLException e) {
                ServerUtils.error("Error replacing user data in SQLite: " + e.getMessage());
            }
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<Void> replace(String playerUUID, DataDocument newDocument) {
        return CompletableFuture.runAsync(() -> {
            String updateQuery = "REPLACE INTO playerData (uuid, data) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, playerUUID);
                statement.setString(2, newDocument.toDocument().toJson());
                statement.executeUpdate();
            } catch (SQLException e) {
                ServerUtils.error("Error replacing user data in SQLite: " + e.getMessage());
            }
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<List<DataDocument>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<DataDocument> allDocuments = new ArrayList<>();
            String query = "SELECT * FROM playerData";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String jsonString = resultSet.getString("data");
                    if (isValidJSON(jsonString)) {
                        allDocuments.add(new DataDocument(jsonString));
                    } else {
                        ServerUtils.error("Invalid JSON found in database: " + jsonString);
                    }
                }
            } catch (SQLException e) {
                ServerUtils.error("Error retrieving all documents from SQLite: " + e.getMessage());
            }
            return allDocuments;
        }, DatabaseService.get().getExecutor());
    }

    public boolean isValidJSON(String jsonString) {
        try {
            Document.parse(jsonString);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }

    private void createTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS playerData (" +
                "uuid VARCHAR(36) NOT NULL, " +
                "data TEXT NOT NULL, " +
                "PRIMARY KEY (uuid)" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery);
        } catch (SQLException e) {
            ServerUtils.error("Error creating playerData table: " + e.getMessage());
        }
    }
}
