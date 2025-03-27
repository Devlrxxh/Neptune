package dev.lrxh.neptune.game.match.impl.team;


import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Data
public class MatchTeam {
    private final List<Participant> participants;
    private final List<Participant> deadParticipants;
    private boolean loser;
    private int points; // Points for portal goal feature
    private Location bedLocation; // Added for bedwars
    private boolean bedDestroyed; // Added for bedwars

    public MatchTeam(List<Participant> participants) {
        this.participants = participants;
        this.deadParticipants = new ArrayList<>();
        this.loser = false;
        this.points = 0;
        this.bedDestroyed = false; // Initialize bed status
    }

    public boolean isLoser() {
        return deadParticipants.size() >= participants.size();
    }

    public int getAliveParticipants() {
        return participants.size() - deadParticipants.size();
    }

    /**
     * Add a point to this team's score
     */
    public void addPoint() {
        this.points++;
    }

    /**
     * Get the current points for this team
     *
     * @return The points scored
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Set the location of this team's bed
     * @param location The bed location
     */
    public void setBedLocation(Location location) {
        this.bedLocation = location;
    }

    /**
     * Get the location of this team's bed
     * @return The bed location
     */
    public Location getBedLocation() {
        return this.bedLocation;
    }

    /**
     * Check if this team's bed has been destroyed
     * @return True if the bed is destroyed
     */
    public boolean isBedDestroyed() {
        return this.bedDestroyed;
    }

    /**
     * Set the bed destroyed status
     * @param destroyed Whether the bed is destroyed
     */
    public void setBedDestroyed(boolean destroyed) {
        this.bedDestroyed = destroyed;
        
        // Update bed status for all participants
        if (destroyed) {
            forEachParticipant(participant -> participant.setBedBroken(true));
        }
    }

    public void sendTitle(String header, String footer, int duration) {
        forEachParticipant((participant) -> participant.sendTitle(header, footer, duration));
    }

    public String getTeamNames() {
        StringBuilder playerNames = new StringBuilder();
        for (Participant participant : participants) {
            if (!playerNames.isEmpty()) {
                playerNames.append(MessagesLocale.MATCH_COMMA.getString());
            }
            playerNames.append(participant.getNameUnColored());
        }
        return playerNames.toString();
    }

    public void forEachParticipant(Consumer<Participant> action) {
        for (Participant participant : participants) {
            Player player = participant.getPlayer();
            if (player != null) {
                action.accept(participant);
            }
        }
    }
}
