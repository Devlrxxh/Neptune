package dev.lrxh.neptune.game.ffa.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.OptArg;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.ffa.FFAService;
import dev.lrxh.neptune.game.ffa.menu.LocationSelectMenu;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;

public class FFACommand {

    @Command(name = "", desc = "")
    public void ffa(@Sender Player player, Kit kit, @OptArg String location) {
        if (API.getProfile(player).getMatch() != null) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }
        if (location == null || location.isEmpty()) {
            new LocationSelectMenu(kit).open(player);
            return;
        }
        FFAService.get().join(API.getProfile(player), kit, location);
    }

    @Command(name = "leave", desc = "")
    public void leave(@Sender Player player) {
        if (!API.getProfile(player).hasState(ProfileState.IN_FFA)) {
            player.sendMessage(CC.error("You aren't in FFA!"));
            return;
        }
        FFAService.get().leave(API.getProfile(player));
    }
}
