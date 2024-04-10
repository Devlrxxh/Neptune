package dev.lrxh.neptune.match.types;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.Participant;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

@Getter
@Setter
public class TeamFightMatch extends Match {

    private final HashSet<Participant> participantsA;
    private final HashSet<Participant> participantsB;

    public TeamFightMatch(Arena arena, Kit kit, boolean ranked, boolean duel, HashSet<Participant> participantsA, HashSet<Participant> participantsB) {
        super(arena, kit, ranked, duel);

        this.participantsA = participantsA;
        this.participantsB = participantsB;
    }
}
