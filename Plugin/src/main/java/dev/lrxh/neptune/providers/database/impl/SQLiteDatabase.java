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

public class SQLiteDatabase implements IDatabase {
    private final String dbPath;

    public SQLiteDatabase() {
        this.dbPath = "jdbc:sqlite:" + Neptune.get().getDataFolder() + "/neptune.db";
    }

    @Override
    public IDatabase load() {
        try (Connection connection = DriverManager.getConnection(dbPath)) {
            createTableIfNotExists(connection);
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
            try (Connection connection = DriverManager.getConnection(dbPath);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, playerUUID.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String dataString = resultSet.getString("data");
                    return new DataDocument(dataString);
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
            try (Connection connection = DriverManager.getConnection(dbPath);
                 PreparedStatement statement = connection.prepareStatement(updateQuery)) {

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
            try (Connection connection = DriverManager.getConnection(dbPath);
                 PreparedStatement statement = connection.prepareStatement(updateQuery)) {

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
            try (Connection connection = DriverManager.getConnection(dbPath);
                 PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String jsonString = resultSet.getString("data");
                    allDocuments.add(new DataDocument(jsonString));
                }
            } catch (SQLException e) {
                ServerUtils.error("Error retrieving all documents from SQLite: " + e.getMessage());
            }
            return allDocuments;
        }, DatabaseService.get().getExecutor());
    }

    private void createTableIfNotExists(Connection connection) {
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
