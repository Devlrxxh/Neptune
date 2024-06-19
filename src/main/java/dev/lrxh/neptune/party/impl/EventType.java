package dev.lrxh.neptune.party.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.impl.MatchTeam;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.utils.CC;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public enum EventType {

    FFA(MenusLocale.PARTY_EVENTS_FFA_SLOT.getInt()) {
        @Override
        public void start(List<Participant> participants, Kit kit) {
            Neptune plugin = Neptune.get();

            Arena arena = plugin.getArenaManager().getRandomArena(kit);

            if (arena == null || arena.getBlueSpawn() == null ||
                    arena.getRedSpawn() == null ||
                    (arena instanceof StandAloneArena &&
                            (((StandAloneArena) arena).getMax() == null ||
                                    ((StandAloneArena) arena).getMin() == null))) {

                for (Participant participant : participants) {
                    participant.sendMessage(CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
                }
                return;
            }

            if (arena instanceof StandAloneArena) {
                ((StandAloneArena) arena).setUsed(true);
            }

            plugin.getMatchManager().startMatch(participants, kit, arena);
        }
    },
    TEAM(MenusLocale.PARTY_EVENTS_SPLIT_SLOT.getInt()) {
        @Override
        public void start(List<Participant> participants, Kit kit) {
            Neptune plugin = Neptune.get();

            Collections.shuffle(participants);

            int halfSize = participants.size() / 2;
            int remainder = participants.size() % 2;
            List<Participant> teamAList = participants.subList(0, halfSize + remainder);
            List<Participant> teamBList = participants.subList(halfSize + remainder, participants.size());

            MatchTeam teamA = new MatchTeam(teamAList);
            MatchTeam teamB = new MatchTeam(teamBList);

            Arena arena = plugin.getArenaManager().getRandomArena(kit);

            if (arena == null || arena.getBlueSpawn() == null ||
                    arena.getRedSpawn() == null ||
                    (arena instanceof StandAloneArena &&
                            (((StandAloneArena) arena).getMax() == null ||
                                    ((StandAloneArena) arena).getMin() == null))) {

                for (Participant participant : participants) {
                    participant.sendMessage(CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
                }
                return;
            }

            if (arena instanceof StandAloneArena) {
                ((StandAloneArena) arena).setUsed(true);
            }

            plugin.getMatchManager().startMatch(teamA, teamB, kit, arena);
        }
    };

    final int slot;

    EventType(int slot) {
        this.slot = slot;
    }

    public abstract void start(List<Participant> participants, Kit kit);
}
