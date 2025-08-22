package dev.lrxh.neptune.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.entity.Player;

public class LeaveCommand {

    @Command(name = "", desc = "")
    public void leave(@Sender Player player) {
        Profile profile = API.getProfile(player.getUniqueId());
        ProfileState state = profile.getState();

        switch (state) {
            case IN_SPECTATOR:
                profile.getMatch().removeSpectator(player.getUniqueId(), true);
                return;
            case IN_GAME:
                profile.getMatch().onLeave(profile.getMatch().getParticipant(player.getUniqueId()), false);
                MessagesLocale.MATCH_FORFEIT.send(player);
                return;
            case IN_CUSTOM:
                profile.setState(ProfileState.IN_LOBBY);
                // TODO: Add consumers for custom API states
                PlayerUtil.teleportToSpawn(player.getUniqueId());
                return;
        }
        PlayerUtil.teleportToSpawn(player.getUniqueId());
    }
}
