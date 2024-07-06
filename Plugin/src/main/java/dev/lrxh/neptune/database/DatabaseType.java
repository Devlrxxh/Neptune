package dev.lrxh.neptune.database;

import dev.lrxh.neptune.database.impl.MongoDatabase;
import dev.lrxh.neptune.database.impl.MySQLDatabase;
import lombok.Getter;

@Getter
public enum DatabaseType {
    MONGO(new MongoDatabase()),
    MYSQL(new MySQLDatabase());

    private final IDatabase iDatabase;

    DatabaseType(IDatabase iDatabase) {
        this.iDatabase = iDatabase;
    }
}
