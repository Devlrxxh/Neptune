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
        File dataFolder = new File(Neptune.get().getDataFolder().getParentFile(), "Neptune");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.file = new File(dataFolder, name + ".yml");

        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (!created) {
                    throw new IOException("File was not created.");
                }
            } catch (IOException e) {
                ServerUtils.error("Error occurred creating config file: " + e);
            }
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            ServerUtils.error("Error occurred saving config: " + e);
        }
    }
}
