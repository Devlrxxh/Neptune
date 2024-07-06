package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.configs.impl.handler.DataType;
import dev.lrxh.neptune.configs.impl.handler.IDataAccessor;
import dev.lrxh.neptune.providers.clickable.ClickableUtils;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

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
    ROUND_STARTED("MATCH.ROUND.STARTED", null, DataType.STRING_LIST, "&aRound Started!"),
    MATCH_FOUND("MATCH.FOUND", null, DataType.STRING_LIST, " ", "&a&lMatch Found!", " ", "&fKit: &a<kit>", "&fOpponent: &a<opponent>", "&fPing: &b<opponent-ping>", " "),
    MATCH_STARTING("MATCH.START.TIMER", null, DataType.STRING_LIST, "&fMatch starting in &b<timer>&f..."),
    MATCH_STARTING_TITLE_HEADER("MATCH.START.TITLE-HEADER", null, DataType.STRING, "&e<countdown-time>"),
    MATCH_STARTING_TITLE_FOOTER("MATCH.START.TITLE-FOOTER", null, DataType.STRING, ""),
    ROUND_STARTING("MATCH.ROUND.START.TIMER", null, DataType.STRING_LIST, "&fRound starting in &b<timer>&f..."),
    MATCH_START_TITLE("MATCH.START.TITLE", null, DataType.STRING, "&aFight!"),
    MATCH_START_HEADER("MATCH.START.HEADER", null, DataType.STRING, ""),
    MATCH_WINNER_TITLE("MATCH.TITLE.WINNER", null, DataType.STRING, "&aVICTORY!"),
    MATCH_LOSER_TITLE("MATCH.TITLE.LOSER", null, DataType.STRING, "&cDEFEAT!"),
    MATCH_TITLE_SUBTITLE("MATCH.TITLE.SUBTITLE", null, DataType.STRING, "&a<player> &fwon the match!"),
    MATCH_COMMA("MATCH.COMMA", null, DataType.STRING, "&7, "),
    MATCH_END_DETAILS_SOLO("MATCH.END_DETAILS_MESSAGE.SOLO", null, DataType.STRING_LIST,
            " ",
            "&bMatch Results:",
            "&aWinner: &e<winner> &7| &cLoser: &e<loser>",
            " "),
    MATCH_END_DETAILS_TEAM("MATCH.END_DETAILS_MESSAGE.TEAM", null, DataType.STRING_LIST,
            " ",
            "&bMatch Results:",
            "&aWinners: &e<winners>",
            "&cLosers: &e<losers>",
            " "),
    MATCH_END_DETAILS_FFA("MATCH.END_DETAILS_MESSAGE.FFA", null, DataType.STRING_LIST,
            "",
            "&f&l<winner> &b&lwon the FFA match!",
            " "),
    MATCH_RESPAWN_TIMER("MATCH.RESPAWN_TIMER", null, DataType.STRING_LIST, "&fRespawning in &b<timer>&f..."),
    MATCH_RESPAWNED("MATCH.RESPAWNED", null, DataType.STRING_LIST, "&aRespawned!"),
    MATCH_PLAY_AGAIN_ENABLED("MATCH.PLAY_AGAIN.ENABLED", null, DataType.BOOLEAN, "true"),
    MATCH_PLAY_AGAIN("MATCH.PLAY_AGAIN.MESSAGE", null, DataType.STRING, "&bDo you want to play again? &a(Click here)"),
    MATCH_PLAY_AGAIN_HOVER("MATCH.PLAY_AGAIN.HOVER", null, DataType.STRING, "&aClick to play again!"),
    MATCH_COMBO_MESSAGE_ENABLE("MATCH.COMBO_MESSAGE.ENABLE", null, DataType.BOOLEAN, "true"),
    MATCH_COMBO_MESSAGE_5("MATCH.COMBO_MESSAGE.5COMBO", null, DataType.STRING_LIST, "&a5 COMBO!"),
    MATCH_COMBO_MESSAGE_10("MATCH.COMBO_MESSAGE.10COMBO", null, DataType.STRING_LIST, "&e10 COMBO!"),
    MATCH_COMBO_MESSAGE_20("MATCH.COMBO_MESSAGE.20COMBO", null, DataType.STRING_LIST, "&c!!!20 COMBO!!!"),
    KIT_EDITOR_START("KIT_EDITOR.START", "This is sent when the player starts editing a kit.", DataType.STRING_LIST,
            "&bOpen your Inventory to edit layout!",
            "&bYou can use &f/kiteditor reset <kit> &bto reset the kit!"),
    KIT_EDITOR_STOP("KIT_EDITOR.STOP", "This is sent when the player finishes editing a kit.", DataType.STRING_LIST, "&aKit layout has been saved."),
    KIT_EDITOR_RESET("KIT_EDITOR.RESET", "This is sent when the player resets a kit.", DataType.STRING_LIST, "&aKit has been reset."),
    DUEL_REQUEST_RECEIVER("DUEL.SENT", null, DataType.STRING_LIST, " ",
            "&bDuel Request",
            " ",
            "&fSender: &a<sender>",
            "&fKit: &a<kit>",
            "&fArena: &a<arena>",
            "&fRounds: &b<rounds>",
            " ",
            "<accept> <deny>"),
    DUEL_REQUEST_SENDER("DUEL.SENDER", null, DataType.STRING_LIST, " ",
            "&bDuel Request Sent",
            " ",
            "&fReceiver: &a<receiver>",
            "&fKit: &a<kit>",
            "&fArena: &a<arena>",
            "&fRounds: &b<rounds>",
            " "),
    DUEL_DENY_SENDER("DUEL.SENDER_DENY", null, DataType.STRING_LIST, "&cDuel Denied."),
    DUEL_DENY_RECEIVER("DUEL.RECEIVER_DENY", null, DataType.STRING_LIST, "&cYour duel to &c<player> &chas been denied."),
    DUEL_ACCEPT("DUEL.ACCEPT", null, DataType.STRING, "&a&l(ACCEPT)"),
    DUEL_ACCEPT_HOVER("DUEL.ACCEPT_HOVER", null, DataType.STRING, "&aClick to accept duel request"),
    DUEL_DENY("DUEL.DENY", null, DataType.STRING, "&c&l(DENY)"),
    DUEL_DENY_HOVER("DUEL.DENY_HOVER", null, DataType.STRING, "&cClick to deny duel request"),
    DUEL_ALREADY_SENT("DUEL.ALREADY_SENT", null, DataType.STRING, "&cYou have already sent <player> a duel request."),
    DUEL_EXPIRED("DUEL.EXPIRED", null, DataType.STRING_LIST, "&cYour duel request to <player> has expired."),
    SPECTATE_START("MATCH.SPECTATE.START", null, DataType.STRING_LIST, "&b<player> &fstarted spectating match."),
    SPECTATE_STOP("MATCH.SPECTATE.STOP", null, DataType.STRING_LIST, "&b<player> &fstopped spectating match."),
    ERROR_MESSAGE("ERROR_MESSAGE", null, DataType.STRING, "&c<error>"),
    JOIN_MESSAGE("JOIN_MESSAGE", null, DataType.STRING, "&8[&a+&8] &7<player> &7joined"),
    LEAVE_MESSAGE("LEAVE_MESSAGE", null, DataType.STRING, "&8[&c-&8] &7<player> &7left"),
    PARTY_CREATE("PARTY.CREATE", null, DataType.STRING_LIST, "&aCreated party!"),
    PARTY_DISBANDED("PARTY.DISABLED", null, DataType.STRING_LIST, "&cParty has been disbanded."),
    PARTY_INVITED("PARTY.INVITED", null, DataType.STRING_LIST, "&f<player> &bhas been invited you to the party!"),
    PARTY_NOT_IN("PARTY.NOT_IN", null, DataType.STRING_LIST, "&cYou are not in a party."),
    PARTY_NOT_IN_PARTY("PARTY.NOT_IN_PARTY", null, DataType.STRING_LIST, "&c<player> isn't in a party."),
    PARTY_JOINED("PARTY.JOINED", null, DataType.STRING_LIST, "&f<player> &bjoined the party!"),
    PARTY_INVITATION("PARTY.INVITATION", null, DataType.STRING_LIST, "&bYou have been invited to &f<leader> &bparty <accept>"),
    PARTY_ACCEPT("PARTY.ACCEPT", null, DataType.STRING, "&a&l(ACCEPT)"),
    PARTY_ACCEPT_HOVER("PARTY.ACCEPT_HOVER", null, DataType.STRING, "&aClick to accept party request"),
    PARTY_NO_PERMISSION("PARTY.NO_PERMISSION", null, DataType.STRING_LIST, "&cYou do not have permission to do this."),
    PARTY_ALREADY_IN("PARTY.ALREADY_IN", null, DataType.STRING_LIST, "&cYou are already in a party."),
    PARTY_ALREADY_SENT("PARTY.ALREADY_SENT", null, DataType.STRING_LIST, "&cYou have already sent <player> a party request."),
    PARTY_ALREADY_PARTY("PARTY.ALREADY_IN_PARTY", null, DataType.STRING_LIST, "&c<player> is already in a party."),
    PARTY_KICK("PARTY.KICK", null, DataType.STRING_LIST, "&f<player> &bhas been kicked from the party."),
    PARTY_LEFT("PARTY.LEFT", null, DataType.STRING_LIST, "&f<player> &bhas left the party."),
    PARTY_INFO("PARTY.INFO", null, DataType.STRING_LIST,
            " ",
            "&7&m------------------------------------------------",
            "&fPrivacy: &b<privacy>",
            "&fLeader: &b<leader>",
            "&fSize: &b<size>",
            "&7&m------------------------------------------------"),
    PARTY_MAX_SIZE("PARTY.MAX_SIZE_REACHED", null, DataType.STRING_LIST, "&cYou have reached max party size"),
    PARTY_NOT_ENOUGH_MEMBERS("PARTY.NOT_ENOUGH_MEMBERS", null, DataType.STRING_LIST, "&cYou need at least 2 players to start a party event."),
    PARTY_HELP("PARTY.HELP", "Message sent on /party help", DataType.STRING_LIST,
            "&bParty Help",
            " ",
            "&b/party create",
            "&b/party invite <player>",
            "&b/party disband",
            "&b/party leave",
            "&b/party kick <player>",
            "&b/party join <player>",
            " "),
    PARTY_EXPIRED("PARTY.EXPIRED", null, DataType.STRING_LIST, "&cYour party request to &c<player> &chas expired."),
    SPECTATE_MENU_NO_MATCH("SPECTATE.MENU.NO_MATCH_ONGOING", null, DataType.STRING_LIST, "&cThere are no ongoing matches!");

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

    @Override
    public String getHeader() {
        return "Replace with NONE to disable";
    }

    public void send(UUID playerUUID, Replacement... replacements) {
        if (dataType.equals(DataType.STRING_LIST)) {
            for (String message : getStringList()) {
                if (message.equals("NONE")) continue;
                PlayerUtil.sendMessage(playerUUID, ClickableUtils.returnMessage(message, replacements));
            }
        } else if (dataType.equals(DataType.STRING)) {
            if (getString().equals("NONE")) return;
            PlayerUtil.sendMessage(playerUUID, ClickableUtils.returnMessage(getString(), replacements));
        }
    }
}
