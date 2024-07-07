package dev.lrxh.neptune.database;

import dev.lrxh.neptune.configs.impl.SettingsLocale;
import lombok.Getter;


@Getter
public class DatabaseManager {
    private IDatabase database;

    public DatabaseManager() {
        load();
    }

    public void load() {
        database = DatabaseType.valueOf(SettingsLocale.DATABASE_TYPE.getString()).getIDatabase().load();
    }
}
