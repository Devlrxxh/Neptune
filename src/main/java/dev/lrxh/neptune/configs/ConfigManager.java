package dev.lrxh.neptune.configs;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Getter
public class ConfigManager {
    private final Neptune plugin = Neptune.get();
    private ConfigFile messagesConfig;
    private ConfigFile arenasConfig;
    private ConfigFile kitsConfig;

    public void load() {
        loadConfigs();
        loadLocales();
    }

    private void loadLocales() {
        YamlConfiguration messagesConfigConfiguration = messagesConfig.getConfiguration();
        Arrays.stream(MessagesLocale.values())
                .filter(messagesConfig -> messagesConfigConfiguration.getStringList(messagesConfig.getPath()).isEmpty())
                .forEach(messagesConfig -> messagesConfigConfiguration.set(messagesConfig.getPath(), Collections.singletonList(messagesConfig.getDefaultValue())));
        messagesConfig.save();

    }

    private void loadConfigs() {
        messagesConfig = new ConfigFile(plugin, "messages");
        arenasConfig = new ConfigFile(plugin, "arenas");
        kitsConfig = new ConfigFile(plugin, "kits");
    }
}
