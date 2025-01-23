package dev.lrxh.neptune.commands;

import com.jonahseguin.drink.annotation.Command;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.entity.Player;

public class LeaveCommand {

    @Command(name = "", desc = "")
    public void leave(Player player) {
        Profile profile = API.getProfile(player.getUniqueId());
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
