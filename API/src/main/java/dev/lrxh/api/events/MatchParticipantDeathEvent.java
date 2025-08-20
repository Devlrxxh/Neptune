package dev.lrxh.api.events;


import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.match.participant.IParticipant;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchParticipantDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final IMatch match;
    @Getter
    private final IParticipant participant;

    public MatchParticipantDeathEvent(IMatch match, IParticipant participant) {
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
