package dev.lrxh.neptune.match;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;

import java.util.HashSet;

public class MatchManager {
    private final HashSet<Match> matches = new HashSet<>();

    public void startMatch(HashSet<Participant> participants, Kit kit, Arena arena, boolean ranked, boolean duel) {
        Match match = new Match(arena, kit, ranked, duel, participants);
        matches.add(match);
    }
}
