package dev.lrxh.neptune.database;

import java.util.List;
import java.util.UUID;

public interface IDatabase {
    void load();

    DataDocument getUserData(UUID playerUUID);

    void replace(UUID playerUUID, DataDocument newDocument);

    void replace(String playerUUID, DataDocument newDocument);

    Object toDocument(DataDocument dataDocument);

    List<DataDocument> getAll();
}
