package dev.lrxh.neptune.events;

import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MatchParticipantDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Match match;
    @Getter
    private final Participant participant;

    public MatchParticipantDeathEvent(Match match, Participant participant) {
        this.match = match;
        this.participant = participant;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
