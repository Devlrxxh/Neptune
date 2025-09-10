package dev.lrxh.api.events;


import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.match.participant.IParticipant;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchParticipantDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter private final IMatch match;
    @Getter private final IParticipant participant;
    @Getter private final String deathMessage;

    public MatchParticipantDeathEvent(IMatch match, IParticipant participant, String deathMessage) {
        this.match = match;
        this.participant = participant;
        this.deathMessage = deathMessage;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
