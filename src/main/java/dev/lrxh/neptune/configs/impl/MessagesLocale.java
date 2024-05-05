package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.providers.clickable.ClickableUtils;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public enum MessagesLocale implements IDataAccessor {
    MATCH_DEATH_DISCONNECT("MATCH.DEATH.DISCONNECT", null, DataType.STRING_LIST, "<player> &7disconnected"),
    MATCH_DEATH_KILLED("MATCH.DEATH.KILLED", null, DataType.STRING_LIST, "&c☠ <player> &7was killed by \uD83D\uDDE1 <killer>"),
    MATCH_DEATH_DIED("MATCH.DEATH.DIED", null, DataType.STRING_LIST, "&c☠ <player> &7died"),
    MATCH_DEATH_VOID("MATCH.DEATH.VOID", null, DataType.STRING_LIST, "&c☠ <player> &7fell into the void while fighting \uD83D\uDDE1 <killer>"),
    QUEUE_JOIN("QUEUE.JOIN", null, DataType.STRING_LIST, "&7(&bDuels&7) Joined Queue"),
    QUEUE_LEAVE("QUEUE.LEAVE", null, DataType.STRING_LIST, "&7(&bDuels&7) Left queue"),
    MATCH_STARTED("MATCH.STARTED", null, DataType.STRING_LIST, "&aMatch Started!"),
    MATCH_FOUND("MATCH.FOUND", null, DataType.STRING_LIST, " ", "&a&lMatch Found!", " ", "&fKit: &a<kit>", "&fOpponent: &a<opponent>", "&fPing: &b<opponent-ping>", " "),
    MATCH_STARTING("MATCH.START.TIMER", null, DataType.STRING_LIST, "&fMatch starting in &b<timer>&f..."),
    MATCH_WINNER_TITLE("MATCH.TITLE.WINNER", null, DataType.STRING, "&aVICTORY!"),
    MATCH_LOSER_TITLE("MATCH.TITLE.LOSER", null, DataType.STRING, "&cDEFEAT!"),
    MATCH_TITLE_SUBTITLE("MATCH.TITLE.SUBTITLE", null, DataType.STRING, "&a<player> &fwon the match!"),
    MATCH_VIEW_INV_TEXT_WINNER("MATCH.END_DETAILS.INV-TEXT-WINNER-HOVER", null, DataType.STRING, "&aClick to view <winner> inventory"),
    MATCH_VIEW_INV_TEXT_LOSER("MATCH.END_DETAILS.INV-TEXT-LOSER-HOVER", null, DataType.STRING, "&cClick to view <loser> inventory"),
    MATCH_COMMA("MATCH.COMMA", null, DataType.STRING, "&7, "),
    MATCH_END_DETAILS("MATCH.END_DETAILS.MESSAGE", null, DataType.STRING_LIST, " ", "&bMatch Inventories &o&7(Click name to view)", "&aWinner: &e<winner> &7| &cLoser: &e<loser>", " "),
    MATCH_RESPAWN_TIMER("MATCH.RESPAWN_TIMER", null, DataType.STRING_LIST, "&fRespawning in &b<timer>&f..."),
    MATCH_RESPAWNED("MATCH.RESPAWNED", null, DataType.STRING_LIST, "&aRespawned!"),
    MATCH_PLAY_AGAIN_ENABLED("MATCH.PLAY_AGAIN.ENABLED", null, DataType.BOOLEAN, "true"),
    MATCH_PLAY_AGAIN("MATCH.PLAY_AGAIN.MESSAGE", null, DataType.STRING, "&bDo you want to play again? &a(Click here)"),
    MATCH_PLAY_AGAIN_HOVER("MATCH.PLAY_AGAIN.HOVER", null, DataType.STRING, "&aClick to play again!"),
    MATCH_COMBO_MESSAGE_ENABLE("MATCH.COMBO_MESSAGE.ENABLE", null, DataType.BOOLEAN, "true"),
    MATCH_COMBO_MESSAGE_5("MATCH.COMBO_MESSAGE.5COMBO", null, DataType.STRING_LIST, "&a5 COMBO!"),
    MATCH_COMBO_MESSAGE_10("MATCH.COMBO_MESSAGE.10COMBO", null, DataType.STRING_LIST, "&e10 COMBO!"),
    MATCH_COMBO_MESSAGE_20("MATCH.COMBO_MESSAGE.20COMBO", null, DataType.STRING_LIST, "&c!!!20 COMBO!!!"),
    KIT_EDITOR_START("KIT_EDITOR.START", "This is sent when the player starts editing a kit.", DataType.STRING_LIST, "&bOpen your Inventory to edit layout!"),
    KIT_EDITOR_STOP("KIT_EDITOR.STOP", "This is sent when the player finishes editing a kit.", DataType.STRING_LIST, "&aKit layout has been saved."),
    DUEL_SENT("DUEL.SENT", null, DataType.STRING_LIST, " ",
            "&bDuel Request",
            " ",
            "&fSender: &a<sender>",
            "&fKit: &a<kit>",
            "&fArena: &a<arena>",
            " ",
            "<accept>"),
    DUEL_ACCEPT("DUEL.ACCEPT", null, DataType.STRING, "&a&l(ACCEPT)"),
    DUEL_ACCEPT_HOVER("DUEL.ACCEPT_HOVER", null, DataType.STRING, "&aClick to accept duel request"),
    SPECTATE_START("MATCH.SPECTATE.START", null, DataType.STRING_LIST, "&b<player> &fstarted spectating match."),
    SPECTATE_STOP("MATCH.SPECTATE.STOP", null, DataType.STRING_LIST, "&b<player> &fstopped spectating match.");


    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    MessagesLocale(String path, @Nullable String comment, DataType dataType, String... defaultValue) {
        this.path = path;
        this.comment = comment;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getMessagesConfig();
    }

    public void send(UUID playerUUID, Replacement... replacements) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        for (String message : getStringList()) {
            PlayerUtil.sendMessage(player, ClickableUtils.returnMessage(message, replacements));
        }
    }
}
