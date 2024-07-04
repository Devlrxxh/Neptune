package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import org.bukkit.entity.Player;

@CommandAlias("leave")
@Description("Leave command.")
public class LeaveCommand extends BaseCommand {

    @Default
    public void leave(Player player) {
        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());

        if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
            Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).getMatch().removeSpectator(player.getUniqueId(), true);
        }
    }
}
