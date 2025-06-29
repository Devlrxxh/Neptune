package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

@Getter
public class ConfigFile {
    private final File file;
    private final YamlConfiguration configuration;

    public ConfigFile(String name) {
        File dataFolder = new File(Neptune.get().getDataFolder().getParentFile(), "Neptune");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.file = new File(dataFolder, name + ".yml");

        if (!file.exists()) {
            try (InputStream in = Neptune.get().getResource(name + ".yml")) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                } else {
                    if (!file.createNewFile()) {
                        throw new IOException("File was not created.");
                    }
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
