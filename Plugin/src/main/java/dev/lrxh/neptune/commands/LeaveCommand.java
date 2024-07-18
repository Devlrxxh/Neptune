package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.profile.data.ProfileState;
import org.bukkit.entity.Player;

@CommandAlias("leave")
@Description("Leave command.")
public class LeaveCommand extends BaseCommand {

    @Default
    public void leave(Player player) {
        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());
        ProfileState state = profile.getState();

        switch (state) {
            case IN_SPECTATOR:
                profile.getMatch().removeSpectator(player.getUniqueId(), true);
                break;
            case IN_GAME:
                profile.getMatch().onLeave(profile.getMatch().getParticipant(player.getUniqueId()));
        }
    }
}
