package dev.lrxh.neptune.database;

import dev.lrxh.neptune.configs.impl.SettingsLocale;
import lombok.Getter;


@Getter
public class DatabaseManager {
    private final IDatabase database;

    public DatabaseManager() {
        database = DatabaseType.valueOf(SettingsLocale.DATABASE_TYPE.getString()).getIDatabase().load();
    }
}
