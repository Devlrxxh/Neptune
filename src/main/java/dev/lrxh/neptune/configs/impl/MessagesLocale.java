package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.providers.clickable.ClickableUtils;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.PlayerUtils;
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
    MATCH_DEATH_KILLED("MATCH.DEATH.KILLED", DataType.STRING_LIST, "&c☠ <player> &7was killed by \uD83D\uDDE1 <killer>"),
    MATCH_DEATH_DIED("MATCH.DEATH.DIED", DataType.STRING_LIST, "&c☠ <player> &7died"),
    MATCH_DEATH_VOID("MATCH.DEATH.VOID", DataType.STRING_LIST, "&c☠ <player> &7fell into the void while fighting \uD83D\uDDE1 <killer>"),
    QUEUE_JOIN("QUEUE.JOIN", DataType.STRING_LIST, "&aYou are now queued for <type> <kit>"),
    QUEUE_LEAVE("QUEUE.LEAVE", DataType.STRING_LIST, "&cYou have been removed from queue."),
    MATCH_STARTED("MATCH.STARTED", DataType.STRING_LIST, "&aMatch Started!"),
    MATCH_FOUND("MATCH.FOUND", DataType.STRING_LIST, " ", "&a&lMatch Found!", " ", "&fKit: &a<kit>", "&fOpponent: &a<opponent>", "&fPing: &b<opponent-ping>", " "),
    MATCH_STARTING("MATCH.START.TIMER", DataType.STRING_LIST, "&fMatch starting in &b<timer>&f..."),
    MATCH_WINNER_TITLE("MATCH.TITLE.WINNER", DataType.STRING, "&aVICTORY!"),
    MATCH_LOSER_TITLE("MATCH.TITLE.LOSER", DataType.STRING, "&cDEFEAT!"),
    MATCH_TITLE_SUBTITLE("MATCH.TITLE.SUBTITLE", DataType.STRING, "&a<player> &fwon the match!"),
    MATCH_VIEW_INV_TEXT_WINNER("MATCH.END_DETAILS.VIEW-INV-TEXT-WINNER", DataType.STRING, "&aClick to view <winner> inventory"),
    MATCH_VIEW_INV_TEXT_LOSER("MATCH.END_DETAILS.VIEW-INV-TEXT-LOSER", DataType.STRING, "&cClick to view <loser> inventory"),
    MATCH_COMMA("MATCH.COMMA", DataType.STRING, "&7, "),
    MATCH_END_DETAILS("MATCH.END_DETAILS.MESSAGE", DataType.STRING_LIST, " ", "&bMatch Inventories &o&7(Click name to view)", "&aWinner: &e<winner> &7| &cLoser: &e<loser>", " "),
    MATCH_RESPAWN_TIMER("MATCH.RESPAWN_TIMER", DataType.STRING_LIST, "&fRespawning in &b<timer>&f..."),
    MATCH_RESPAWNED("MATCH.RESPAWNED", DataType.STRING_LIST, "&aRespawned!");


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

    public void send(UUID playerUUID, Replacement... replacements) {
        Player player = Bukkit.getPlayer(playerUUID);

        for (String message : getStringList()) {

            PlayerUtils.sendMessage(player, ClickableUtils.returnMessage(message, replacements));
        }
    }
}
