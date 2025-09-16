package dev.lrxh.neptune.providers.database.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.providers.database.DatabaseService;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLDatabase implements IDatabase {
    private Connection connection;

    @Override
    public IDatabase load() {
        try {
            connection = DriverManager.getConnection(uri);
            createTableIfNotExists();
        } catch (SQLException e) {
            ServerUtils.error("Failed to connect to MySQL database: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(Neptune.get());
        }
        return this;
    }

    @Override
    public CompletableFuture<DataDocument> getUserData(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT data FROM playerData WHERE uuid=?";
            try (Connection conn = DriverManager.getConnection(uri);
                 PreparedStatement statement = conn.prepareStatement(query)) {

                statement.setString(1, playerUUID.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String dataString = resultSet.getString("data");
                        return new DataDocument(dataString);
                    }
                }
            } catch (SQLException e) {
                ServerUtils.error("Error fetching user data from MySQL: " + e.getMessage());
            }
            return null;
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<Void> replace(UUID playerUUID, DataDocument newDocument) {
        return CompletableFuture.runAsync(() -> {
            String updateQuery = "REPLACE INTO playerData (uuid, data) VALUES (?, ?)";
            try (Connection conn = DriverManager.getConnection(uri);
                 PreparedStatement statement = conn.prepareStatement(updateQuery)) {

                statement.setString(1, playerUUID.toString());
                statement.setString(2, newDocument.toDocument().toJson());
                statement.executeUpdate();
            } catch (SQLException e) {
                ServerUtils.error("Error replacing user data in MySQL: " + e.getMessage());
            }
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<Void> replace(String playerUUID, DataDocument newDocument) {
        return CompletableFuture.runAsync(() -> {
            String updateQuery = "REPLACE INTO playerData (uuid, data) VALUES (?, ?)";
            try (Connection conn = DriverManager.getConnection(uri);
                 PreparedStatement statement = conn.prepareStatement(updateQuery)) {

                statement.setString(1, playerUUID);
                statement.setString(2, newDocument.toDocument().toJson());
                statement.executeUpdate();
            } catch (SQLException e) {
                ServerUtils.error("Error replacing user data in MySQL: " + e.getMessage());
            }
        }, DatabaseService.get().getExecutor());
    }

    @Override
    public CompletableFuture<List<DataDocument>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<DataDocument> allDocuments = new ArrayList<>();
            String query = "SELECT data FROM playerData";
            try (Connection conn = DriverManager.getConnection(uri);
                 PreparedStatement statement = conn.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String jsonString = resultSet.getString("data");
                    allDocuments.add(new DataDocument(jsonString));
                }
            } catch (SQLException e) {
                ServerUtils.error("Error retrieving all documents from MySQL: " + e.getMessage());
            }
            return allDocuments;
        }, DatabaseService.get().getExecutor());
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
            ServerUtils.error("Error creating playerData table in MySQL: " + e.getMessage());
        }
    }
}
