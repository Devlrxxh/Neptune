package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public enum MessagesLocale implements IDataAccessor {
    MATCH_DEATH_DISCONNECT("MATCH.DEATH.DISCONNECT", DataType.STRING_LIST, "&7disconnected"),
    MATCH_DEATH_KILLED("MATCH.DEATH.KILLED", DataType.STRING_LIST, "<player> &7was killed by <killer>"),
    MATCH_DEATH_DIED("MATCH.DEATH.DIED", DataType.STRING_LIST, "<player> &7died"),
    MATCH_DEATH_VOID("MATCH.DEATH.VOID", DataType.STRING_LIST, "<player> &7fell into the void while fighting <killer>"),
    QUEUE_JOIN("QUEUE.JOIN", DataType.STRING_LIST, "&aYou are now queued for <type> <kit>"),
    QUEUE_LEAVE("QUEUE.LEAVE", DataType.STRING_LIST, "&cYou have been removed from queue."),
    MATCH_STARTED("MATCH.STARTED", DataType.STRING_LIST, "&aMatch Started!"),
    MATCH_FOUND("MATCH.FOUND", DataType.STRING_LIST, " ", "&a&lMatch Found!", " ", "&fKit: &a<kit>", "&fOpponent: &a<opponent>", "&fPing: &b<opponent-ping>", " "),
    MATCH_STARTING("MATCH.START.TIMER", DataType.STRING_LIST, "&fMatch starting in &9<timer>&f..."),
    MATCH_WINNER_TITLE("MATCH.TITLE.WINNER", DataType.STRING, "&aVICTORY!"),
    MATCH_LOSER_TITLE("MATCH.TITLE.LOSER", DataType.STRING, "&cDEFEAT!"),
    MATCH_TITLE_SUBTITLE("MATCH.TITLE.SUBTITLE", DataType.STRING, "&a<player> &fwon the match!");

    private final String path;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    MessagesLocale(String path, DataType dataType, String... defaultValue) {
        this.path = path;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getMessagesConfig();
    }


    public void send(UUID playerUUID, String... replacements) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        for (String string : getStringList()) {
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