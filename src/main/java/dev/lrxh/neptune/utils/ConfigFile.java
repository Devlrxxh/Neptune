package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class ConfigFile {
    private final File file;
    private final YamlConfiguration configuration;
    private final Neptune plugin = Neptune.get();

    public ConfigFile(String name) {
        this.file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            plugin.saveResource(name + ".yml", false);
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException ignored) {
        }
    }
}
