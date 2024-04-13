package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.Match;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Profile {
    private UUID playerUUID;
    private Match match;
    private ProfileState state;

    public void setState(ProfileState profileState){
        state = profileState;
        VisibilityLogic.handle(playerUUID);
        Neptune.get().getHotbarManager().giveItems(playerUUID);
    }
}
