package dev.lrxh.neptune.events;

import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchParticipantRespawnEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Match match;
    @Getter
    private final Participant participant;

    public MatchParticipantRespawnEvent(Match match, Participant participant) {
        this.match = match;
        this.participant = participant;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
