package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("stats")
@Description("Display player stats.")
public class DuelCommand extends BaseCommand {

    @Default
    @Syntax("<name>")
    @CommandCompletion("@names")
    public void statsOthers(Player player, String otherPlayer) {
        Player target = Bukkit.getPlayer(otherPlayer);
        if (target == null) {
            player.sendMessage(CC.error("Player isn't online!"));
            return;
        }
        Profile targetProfile = Neptune.get().getProfileManager().getByUUID(target.getUniqueId());

        if (targetProfile.getMatch() != null || targetProfile.getState().equals(ProfileState.IN_KIT_EDITOR)) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
        }

        if (targetProfile.getData().getDuelRequest() != null) {
            player.sendMessage(CC.error("Player already has pending duel request!"));
        }

//        TextComponent winnerMessage = Component.text(MessagesLocale.DUEL_SENT.getStringList())
//                .clickEvent(ClickEvent.runCommand("/viewinv " + winnerTeam.getTeamNames()))
//                .hoverEvent(HoverEvent.showText(Component.text(MessagesLocale.MATCH_VIEW_INV_TEXT_WINNER.getString().replace("<winner>", winnerTeam.getTeamNames()))));

    }
}