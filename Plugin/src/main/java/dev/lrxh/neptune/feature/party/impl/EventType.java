package dev.lrxh.neptune.feature.party.impl;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.utils.CC;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public enum EventType {

    FFA(MenusLocale.PARTY_EVENTS_FFA_SLOT.getInt()) {
        @Override
        public void start(List<Participant> participants, Kit kit) {
            Arena arena = kit.getRandomArena();

            if (arena == null) {

                for (Participant participant : participants) {
                    participant.sendMessage(CC.error("No arenas were found!"));
                }
                return;
            }

            if (!arena.isSetup()) {

                for (Participant participant : participants) {
                    participant.sendMessage(CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
                }
                return;
            }

            MatchService.get().startMatch(participants, kit, arena);
        }
    },
    TEAM(MenusLocale.PARTY_EVENTS_SPLIT_SLOT.getInt()) {
        @Override
        public void start(List<Participant> participants, Kit kit) {
            if (participants.size() < 2) {
                participants.forEach(p -> p.sendMessage(CC.error("You need at least 2 players to start a duel.")));
                return;
            }

            List<Participant> shuffled = new ArrayList<>(participants);
            Collections.shuffle(shuffled);

            int half = shuffled.size() / 2;
            int rem = shuffled.size() % 2;
            List<Participant> teamAList = new ArrayList<>(shuffled.subList(0, half + rem));
            List<Participant> teamBList = new ArrayList<>(shuffled.subList(half + rem, shuffled.size()));

            MatchTeam teamA = new MatchTeam(teamAList);
            MatchTeam teamB = new MatchTeam(teamBList);

            Arena arena = kit.getRandomArena();
            if (arena == null) {
                participants.forEach(p ->
                        p.sendMessage(CC.error("No arenas were found! Please contact an admin.")));
                return;
            }
            if (!arena.isSetup()) {
                participants.forEach(p ->
                        p.sendMessage(CC.error("Arena wasn't set up properly! Please contact an admin.")));
                return;
            }

            MatchService.get().startMatch(teamA, teamB, kit, arena);
        }

    };

    final int slot;

    EventType(int slot) {
        this.slot = slot;
    }

    public abstract void start(List<Participant> participants, Kit kit);
}
