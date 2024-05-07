package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.providers.duel.DuelRequest;
import dev.lrxh.neptune.providers.duel.menu.KitSelectMenu;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@CommandAlias("duel|1v1")
@Description("Duel other players.")
public class DuelCommand extends BaseCommand {

    @Default
    @Syntax("<name>")
    @CommandCompletion("@names")
    public void duel(Player player, String otherPlayer) {
        Player target = Bukkit.getPlayer(otherPlayer);
        if (target == null) {
            player.sendMessage(CC.error("Player isn't online!"));
            return;
        }

        if (Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).getMatch() != null) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }

        if (player.getName().equalsIgnoreCase(otherPlayer)) {
            player.sendMessage(CC.error("You can't duel yourself!"));
            return;
        }
        Profile targetProfile = Neptune.get().getProfileManager().getByUUID(target.getUniqueId());

        if (targetProfile.getMatch() != null || targetProfile.getState().equals(ProfileState.IN_KIT_EDITOR)) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        if (targetProfile.getData().getDuelRequest() != null) {
            player.sendMessage(CC.error("Player already has pending duel request!"));
            return;
        }

        new KitSelectMenu(target.getUniqueId(), 1).openMenu(player);
    }

    @Default
    @Syntax("<name> <rounds>")
    @CommandCompletion("@names")
    public void statsOthers(Player player, String otherPlayer, int rounds) {

        System.out.println("This run");
        Player target = Bukkit.getPlayer(otherPlayer);
        if (target == null) {
            player.sendMessage(CC.error("Player isn't online!"));
            return;
        }

        if (Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).getMatch() != null) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }

        if (player.getName().equalsIgnoreCase(otherPlayer)) {
            player.sendMessage(CC.error("You can't duel yourself!"));
            return;
        }
        Profile targetProfile = Neptune.get().getProfileManager().getByUUID(target.getUniqueId());

        if (targetProfile.getMatch() != null || targetProfile.getState().equals(ProfileState.IN_KIT_EDITOR)) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        if (targetProfile.getData().getDuelRequest() != null) {
            player.sendMessage(CC.error("Player already has pending duel request!"));
            return;
        }

        new KitSelectMenu(target.getUniqueId(), rounds).openMenu(player);
    }

    @Subcommand("accept")
    public void accept(Player player) {
        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());

        if (profile.getMatch() != null || profile.getState().equals(ProfileState.IN_SPECTATOR) || profile.getState().equals(ProfileState.IN_KIT_EDITOR)) {
            player.sendMessage(CC.error("You can't accept duel requests right now!"));
            return;
        }

        DuelRequest duelRequest = profile.getData().getDuelRequest();

        if (duelRequest == null) {
            player.sendMessage(CC.error("You don't have any pending duel request!"));
            return;
        }

        if (Bukkit.getPlayer(duelRequest.getSender()) == null) {
            player.sendMessage(CC.error("You don't have any pending duel request!"));
            profile.getData().setDuelRequest(null);
            return;
        }

        //Create participants
        Participant participant1 =
                new Participant(duelRequest.getSender());

        Participant participant2 =
                new Participant(player.getUniqueId());

        List<Participant> participants = Arrays.asList(participant1, participant2);

        Neptune.get().getMatchManager().startMatch(participants, duelRequest.getKit(),
                duelRequest.getArena(), true, duelRequest.getRounds());

        System.out.println(duelRequest.getRounds());

        profile.getData().setDuelRequest(null);
    }
}