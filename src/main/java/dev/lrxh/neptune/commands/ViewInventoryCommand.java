package dev.lrxh.neptune.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.menu.MatchSnapshotMenu;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("viewinv")
@Description("View post match inventories.")
public class ViewInventoryCommand extends BaseCommand {


    @Default
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void execute(Player player, String playerName) {
        Player target = Bukkit.getPlayer(playerName);

        if (target == null) {
            player.sendMessage(CC.error("Player not found!"));
            return;
        }
        Profile profile = Neptune.get().getProfileManager().getByUUID(target.getUniqueId());
        if (profile.getMatchSnapshot() == null) {
            player.sendMessage(CC.error("Match Snapshot not found!"));
            return;
        }
        new MatchSnapshotMenu(profile.getMatchSnapshot()).openMenu(player);
    }
}
