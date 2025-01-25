package dev.lrxh.neptune.database.impl;

import lombok.Getter;

@Getter
public enum DatabaseType {
    MONGO(new MongoDatabase()),
    SQLITE(new SQLiteDatabase()),
    MYSQL(new MySQLDatabase());

    private final IDatabase iDatabase;

    DatabaseType(IDatabase iDatabase) {
        this.iDatabase = iDatabase;
    }
}
