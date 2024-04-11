package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.match.Match;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Profile {
    private Match match;
    private ProfileState state;
}
