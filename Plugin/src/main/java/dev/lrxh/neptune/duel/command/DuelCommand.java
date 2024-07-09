package dev.lrxh.neptune.duel.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.duel.DuelRequest;
import dev.lrxh.neptune.duel.menu.KitSelectMenu;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("duel|1v1")
@Description("Duel other players.")
public class DuelCommand extends BaseCommand {
    private final Neptune plugin = Neptune.get();

    @Default
    @Syntax("<name>")
    @CommandCompletion("@names")
    public void duel(Player player, String otherPlayer) {
        Player target = Bukkit.getPlayer(otherPlayer);
        if (target == null) {
            player.sendMessage(CC.error("Player isn't online!"));
            return;
        }

        if (plugin.getProfileManager().getByUUID(player.getUniqueId()).getMatch() != null) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }

        if (player.getName().equalsIgnoreCase(otherPlayer)) {
            player.sendMessage(CC.error("You can't duel yourself!"));
            return;
        }
        Profile targetProfile = plugin.getProfileManager().getByUUID(target.getUniqueId());

        if (targetProfile.getMatch() != null || targetProfile.getState().equals(ProfileState.IN_KIT_EDITOR) || !targetProfile.getSettingData().isAllowDuels()) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        if (targetProfile.getGameData().getRequests().contains(player.getUniqueId())) {
            MessagesLocale.DUEL_ALREADY_SENT.send(player.getUniqueId(), new Replacement("<player>", player.getName()));
            return;
        }

        new KitSelectMenu(target.getUniqueId(), false).openMenu(player.getUniqueId());
    }

    @Subcommand("accept")
    @Syntax("<uuid>")
    public void accept(Player player, String otherString) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        GameData playerGameData = plugin.getProfileManager().getByUUID(player.getUniqueId()).getGameData();

        if (profile.getMatch() != null || profile.getState().equals(ProfileState.IN_SPECTATOR) || profile.getState().equals(ProfileState.IN_KIT_EDITOR)) {
            player.sendMessage(CC.error("You can't accept duel requests right now!"));
            return;
        }
        UUID otherUUID = UUID.fromString(otherString);

        DuelRequest duelRequest = (DuelRequest) playerGameData.getRequests().get(otherUUID);

        if (duelRequest == null) {
            player.sendMessage(CC.error("You don't have any duel request from this player!"));
            return;
        }

        profile.acceptDuel(otherUUID);
    }

    @Subcommand("deny")
    @Syntax("<uuid>")
    public void deny(Player player, String otherString) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        GameData playerGameData = profile.getGameData();

        UUID otherUUID = UUID.fromString(otherString);

        DuelRequest duelRequest = (DuelRequest) playerGameData.getRequests().get(otherUUID);

        if (duelRequest == null) {
            player.sendMessage(CC.error("You don't have any duel request from this player!"));
            return;
        }

        MessagesLocale.DUEL_DENY_SENDER.send(player.getUniqueId());
        MessagesLocale.DUEL_DENY_RECEIVER.send(otherUUID,
                new Replacement("<player>", player.getName()));

        playerGameData.removeRequest(otherUUID);
    }
}