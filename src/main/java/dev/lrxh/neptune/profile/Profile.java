package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchSnapshot;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Data
public class Profile {
    private final UUID playerUUID;
    private ProfileState state;
    private Match match;
    private MatchSnapshot matchSnapshot;

    public Profile(UUID playerUUID, ProfileState state) {
        this.playerUUID = playerUUID;
        this.state = state;
        this.match = null;
        this.matchSnapshot = null;
    }

    public void setState(ProfileState profileState) {
        state = profileState;
        VisibilityLogic.handle(playerUUID);
        Neptune.get().getHotbarManager().giveItems(playerUUID);
    }
}
