package dev.lrxh.neptune.game.match.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.menu.button.MatchSpectateTeleportButton;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MatchSpectateTeleportMenu extends Menu {
    public MatchSpectateTeleportMenu() {
        super(MenusLocale.MATCH_SPECTATE_TITLE.getString(), 36, Filter.valueOf(MenusLocale.MATCH_SPECTATE_FILTER.getString()));
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        Profile profile = API.getProfile(player.getUniqueId());

        int i = 0;

        for (Participant participant : profile.getMatch().getParticipantsList()) {
            buttons.add(new MatchSpectateTeleportButton(i, participant));
            i++;
        }
        return buttons;
    }
}
