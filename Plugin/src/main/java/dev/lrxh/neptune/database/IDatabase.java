package dev.lrxh.neptune.database;

import dev.lrxh.neptune.configs.impl.SettingsLocale;

import java.util.List;
import java.util.UUID;

public interface IDatabase {
    IDatabase load();
    DataDocument getUserData(UUID playerUUID);
    void replace(UUID playerUUID, DataDocument newDocument);
    void replace(String playerUUID, DataDocument newDocument);
    List<DataDocument> getAll();
    default String getUri(){
        return SettingsLocale.URI.getString();
    }

    default String getDatabase() {
        return SettingsLocale.DATABASE.getString();
    }
}
