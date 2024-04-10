package dev.lrxh.neptune.utils;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

@Getter
public class ConfigFile {
    private final File file;
    private final YamlConfiguration configuration;

    public ConfigFile(JavaPlugin plugin, String name) {
        this(plugin, name, false);
    }

    public ConfigFile(JavaPlugin plugin, String name, boolean overwrite) {
        this.file = new File(plugin.getDataFolder(), name + ".yml");
        plugin.saveResource(name + ".yml", overwrite);
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public String getString(String path) {
        return this.configuration.getString(path);
    }

    public int getInteger(String path) {
        return configuration.getInt(path);
    }

    public boolean getBoolean(String path) {
        return this.configuration.getBoolean(path);
    }

    public double getDouble(String path) {
        return this.configuration.getDouble(path);
    }

    public List<String> getStringList(String path) {
        return this.configuration.getStringList(path);
    }
}