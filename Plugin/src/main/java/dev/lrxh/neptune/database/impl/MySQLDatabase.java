package dev.lrxh.neptune.database.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.database.DataDocument;
import dev.lrxh.neptune.database.IDatabase;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class MySQLDatabase implements IDatabase {
    private final Neptune plugin;
    private Connection connection;

    public MySQLDatabase() {
        this.plugin = Neptune.get();
    }

    @Override
    public IDatabase load() {
        try {
            connection = DriverManager.getConnection(getUri());
        } catch (SQLException e) {
            ServerUtils.error("Failed to connect to MySQL database: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        return this;
    }

    @Override
    public DataDocument getUserData(UUID playerUUID) {
        String query = "SELECT * FROM playerData WHERE uuid=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String dataString = resultSet.getString("data");
                if (dataString != null) {
                    return new DataDocument(dataString);
                }
            }
        } catch (SQLException e) {
            ServerUtils.error("Error fetching user data from MySQL: " + e.getMessage());
        }
        return null;
    }


    @Override
    public void replace(UUID playerUUID, DataDocument newDocument) {
        String updateQuery = "REPLACE INTO playerData (uuid, data) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, newDocument.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            ServerUtils.error("Error replacing user data in MySQL: " + e.getMessage());
        }
    }

    @Override
    public void replace(String playerUUID, DataDocument newDocument) {
        String updateQuery = "REPLACE INTO playerData (uuid, data) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, playerUUID);
            statement.setString(2, newDocument.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            ServerUtils.error("Error replacing user data in MySQL: " + e.getMessage());
        }
    }

    @Override
    public List<DataDocument> getAll() {
        List<DataDocument> allDocuments = new ArrayList<>();
        String query = "SELECT * FROM playerData";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allDocuments.add(new DataDocument(resultSet.getString("data")));
            }
        } catch (SQLException e) {
            ServerUtils.error("Error retrieving all documents from MySQL: " + e.getMessage());
        }
        return allDocuments;
    }
}
