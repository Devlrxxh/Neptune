package dev.lrxh.neptune.match;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.impl.FfaFightMatch;
import dev.lrxh.neptune.match.impl.SoloFightMatch;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.match.impl.team.MatchTeam;
import dev.lrxh.neptune.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.match.tasks.MatchStartRunnable;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.queue.QueueService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class MatchService {
    private static MatchService instance;
    public final HashSet<Match> matches = new HashSet<>();
    private final Neptune plugin;

    public MatchService() {
        this.plugin = Neptune.get();
    }

    public static MatchService get() {
        if (instance == null) instance = new MatchService();

        return instance;
    }

    public void startMatch(List<Participant> participants, Kit kit, Arena arena, boolean duel, int rounds) {
        if (arena instanceof StandAloneArena standAloneArena) {
            for (Participant participant : participants) {
                participant.sendMessage(MessagesLocale.CREATING_ARENA);
            }
            standAloneArena.loadDupe().thenAccept(dupe -> Bukkit.getServer().getScheduler().runTask(Neptune.get(), () -> {
                for (Participant participant : participants) {
                    QueueService.get().remove(participant.getPlayerUUID());
                    participant.sendMessage(MessagesLocale.CREATED_ARENA);
                    kit.addPlaying();
                }

                //Create teams
                Participant playerRed = participants.get(0);
                Participant playerBlue = participants.get(1);

                playerRed.setOpponent(playerBlue);
                playerRed.setColor(ParticipantColor.RED);

                playerBlue.setOpponent(playerRed);
                playerBlue.setColor(ParticipantColor.BLUE);

                //Create match
                SoloFightMatch match = new SoloFightMatch(dupe, kit, duel, participants, playerRed, playerBlue, rounds);

                matches.add(match);

                //Teleport the Players to their spawn
                match.teleportToPositions();

                //Setup players
                match.setupParticipants();

                //Apply kit rules for players
                match.checkRules();

                //Start match start runnable
                new MatchStartRunnable(match, plugin).start(0L, 20L, plugin);
            }));
        } else {
            for (Participant participant : participants) {
                QueueService.get().remove(participant.getPlayerUUID());
                participant.sendMessage(MessagesLocale.CREATED_ARENA);
                kit.addPlaying();
            }

            //Create teams
            Participant playerRed = participants.get(0);
            Participant playerBlue = participants.get(1);

            playerRed.setOpponent(playerBlue);
            playerRed.setColor(ParticipantColor.RED);

            playerBlue.setOpponent(playerRed);
            playerBlue.setColor(ParticipantColor.BLUE);

            //Create match
            SoloFightMatch match = new SoloFightMatch(arena, kit, duel, participants, playerRed, playerBlue, rounds);

            matches.add(match);

            //Teleport the Players to their spawn
            match.teleportToPositions();

            //Setup players
            match.setupParticipants();

            //Apply kit rules for players
            match.checkRules();

            for (Participant participant : participants) {
                QueueService.get().remove(participant.getPlayerUUID());
                participant.sendMessage(MessagesLocale.CREATED_ARENA);
                kit.addPlaying();
            }

            //Start match start runnable
            new MatchStartRunnable(match, plugin).start(0L, 20L, plugin);
        }
    }

    public void startMatch(MatchTeam teamA, MatchTeam teamB, Kit kit, Arena arena) {
        if (arena instanceof StandAloneArena standAloneArena) {
            for (Participant a : teamA.getParticipants()) {
                a.sendMessage(MessagesLocale.CREATING_ARENA);
            }
            for (Participant b : teamB.getParticipants()) {
                b.sendMessage(MessagesLocale.CREATING_ARENA);
            }
            standAloneArena.loadDupe().thenAccept(dupe -> Bukkit.getServer().getScheduler().runTask(Neptune.get(), () -> {
                for (Participant a : teamA.getParticipants()) {
                    a.sendMessage(MessagesLocale.CREATED_ARENA);
                }
                for (Participant b : teamB.getParticipants()) {
                    b.sendMessage(MessagesLocale.CREATED_ARENA);
                }
                for (Participant participant : teamA.getParticipants()) {
                    for (Participant opponent : teamB.getParticipants()) {
                        participant.setOpponent(opponent);
                        participant.setColor(ParticipantColor.RED);
                        opponent.setOpponent(participant);
                        opponent.setColor(ParticipantColor.BLUE);
                    }
                }

                List<Participant> participants = new ArrayList<>(teamA.getParticipants());
                participants.addAll(teamB.getParticipants());

                //Create match
                TeamFightMatch match = new TeamFightMatch(dupe, kit, participants, teamA, teamB);

                matches.add(match);

                //Setup players
                match.setupParticipants();

                //Apply kit rules for players
                match.checkRules();

                //Teleport the Players to their spawn
                match.teleportToPositions();

                //Start match start runnable
                new MatchStartRunnable(match, plugin).start(0L, 20L, plugin);
            }));
        } else {
            for (Participant participant : teamA.getParticipants()) {
                for (Participant opponent : teamB.getParticipants()) {
                    participant.setOpponent(opponent);
                    participant.setColor(ParticipantColor.RED);
                    opponent.setOpponent(participant);
                    opponent.setColor(ParticipantColor.BLUE);
                }
            }

            List<Participant> participants = new ArrayList<>(teamA.getParticipants());
            participants.addAll(teamB.getParticipants());

            //Create match
            TeamFightMatch match = new TeamFightMatch(arena, kit, participants, teamA, teamB);

            matches.add(match);

            //Setup players
            match.setupParticipants();

            //Apply kit rules for players
            match.checkRules();

            //Teleport the Players to their spawn
            match.teleportToPositions();

            //Start match start runnable
            new MatchStartRunnable(match, plugin).start(0L, 20L, plugin);
        }
    }

    public void startMatch(List<Participant> participants, Kit kit, Arena arena) {
        if (arena instanceof StandAloneArena standAloneArena) {
            for (Participant participant : participants) {
                participant.sendMessage(MessagesLocale.CREATING_ARENA);
            }
            standAloneArena.loadDupe().thenAccept(dupe -> Bukkit.getServer().getScheduler().runTask(Neptune.get(), () -> {
                for (Participant participant : participants) {
                    participant.sendMessage(MessagesLocale.CREATED_ARENA);
                }
                for (Participant participant : participants) {
                    participant.setColor(ParticipantColor.RED);
                }

                //Create match
                FfaFightMatch match = new FfaFightMatch(dupe, kit, participants);

                matches.add(match);

                //Setup players
                match.setupParticipants();

                //Apply kit rules for players
                match.checkRules();

                //Teleport the Players to their spawn
                match.teleportToPositions();

                //Start match start runnable
                new MatchStartRunnable(match, plugin).start(0L, 20L, plugin);
            }));
        } else {
            for (Participant participant : participants) {
                participant.setColor(ParticipantColor.RED);
            }

            //Create match
            FfaFightMatch match = new FfaFightMatch(arena, kit, participants);

            matches.add(match);

            //Setup players
            match.setupParticipants();

            //Apply kit rules for players
            match.checkRules();

            //Teleport the Players to their spawn
            match.teleportToPositions();

            //Start match start runnable
            new MatchStartRunnable(match, plugin).start(0L, 20L, plugin);
        }
    }

    public Optional<Match> getMatch(Player player) {
        Profile profile = API.getProfile(player);
        return Optional.ofNullable(profile)
                .map(Profile::getMatch);
    }

    public Optional<Match> getMatch(UUID uuid) {
        Profile profile = API.getProfile(uuid);
        return Optional.ofNullable(profile)
                .map(Profile::getMatch);
    }

    public void stopAllGames() {
        for (Match match : matches) {
            match.resetArena();
        }
    }

}
