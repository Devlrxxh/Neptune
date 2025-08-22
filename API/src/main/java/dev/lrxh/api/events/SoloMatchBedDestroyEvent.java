package dev.lrxh.api.events;


import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.match.participant.IParticipant;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SoloMatchBedDestroyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final IMatch match;
    @Getter
    private final IParticipant bedOwner;
    @Getter
    private final IParticipant bedBreaker;

    public SoloMatchBedDestroyEvent(IMatch match, IParticipant bedOwner, IParticipant bedBreaker) {
        this.match = match;
        this.bedOwner = bedOwner;
        this.bedBreaker = bedBreaker;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
