package dev.lrxh.neptune.providers.database;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.providers.database.impl.DatabaseType;
import dev.lrxh.neptune.providers.database.impl.IDatabase;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;

@Getter
public class DatabaseService {
    private static DatabaseService instance;
    private IDatabase database = null;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public DatabaseService() {
        String uri = SettingsLocale.URI.getString();
        if (uri != null && (uri.isEmpty() || uri.equals("NONE")) &&
                !SettingsLocale.DATABASE_TYPE.getString().equalsIgnoreCase("SQLITE")) {
            ServerUtils.error("URI is missing or empty");
            Bukkit.getPluginManager().disablePlugin(Neptune.get());
        }

        try {
            this.database = DatabaseType.valueOf(SettingsLocale.DATABASE_TYPE.getString()).getIDatabase().load();
        } catch (RuntimeException e) {
            ServerUtils.error("Unknown database type in settings.yml");
        }
    }

    public static DatabaseService get() {
        if (instance == null)
            instance = new DatabaseService();

        return instance;
    }
}
