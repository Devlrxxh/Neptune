package dev.lrxh.neptune.database;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.database.impl.DatabaseType;
import dev.lrxh.neptune.database.impl.IDatabase;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class DatabaseManager {
    private IDatabase database = null;

    public DatabaseManager(Neptune plugin) {
        String uri = SettingsLocale.URI.getString();
        if (uri != null && (uri.isEmpty() || uri.equals("NONE"))) {
            ServerUtils.error("URI is missing or empty");
            Bukkit.getPluginManager().disablePlugin(plugin.get());
        }

        try {
            this.database = DatabaseType.valueOf(SettingsLocale.DATABASE_TYPE.getString()).getIDatabase().load();
        } catch (RuntimeException e) {
            ServerUtils.error("Unknown database type in settings.yml");
        }
    }
}
