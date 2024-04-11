package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public enum MessagesLocale {

    MATCH_DEATH_DISCONNECT("MATCH.DEATH.DISCONNECT", "<player> &7disconnected"),
    MATCH_DEATH_KILLED("MATCH.DEATH.KILLED", "<player> &7was killed by <killer>"),
    MATCH_DEATH_VOID("MATCH.DEATH.VOID", "<player> &7fell into the void while fighting <killer>"),
    QUEUE_JOIN("MESSAGES.QUEUE.JOIN", "&aYou are now queued for <type> <kit>"),
    QUEUE_LEAVE("MESSAGES.QUEUE.LEAVE", "&cYou have been removed from queue.");

    private final String path;
    private final String defaultValue;

    MessagesLocale(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public void send(UUID playerUUID, String... replacements) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        for (String string : Neptune.get().getConfigManager().getMessagesConfig().getConfiguration().getStringList(path)) {
            String translatedMessage = string;
            if (replacements.length % 2 == 0) {
                for (int i = 0; i < replacements.length; i += 2) {
                    translatedMessage = translatedMessage.replace(replacements[i], replacements[i + 1]);
                }
                player.sendMessage(CC.translate(translatedMessage));
            }
        }
    }
}
