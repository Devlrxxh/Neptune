package dev.lrxh.neptune.database;

import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.database.impl.MongoDatabase;
import dev.lrxh.neptune.database.impl.MySQLDatabase;
import lombok.Getter;

@Getter
public class DatabaseManager {
    private IDatabase iDatabase;

    public DatabaseManager() {
        load();
    }

    public void load() {
        switch (DatabaseType.valueOf(SettingsLocale.DATABASE_TYPE.getString())) {
            case MONGO:
                iDatabase = new MongoDatabase(SettingsLocale.URI.getString(), SettingsLocale.DATABASE.getString());
                break;
            case MYSQL:
                iDatabase = new MySQLDatabase(SettingsLocale.URI.getString(), SettingsLocale.DATABASE.getString());
        }

        iDatabase.load();
    }
}
