package dev.lrxh.api.events;


import dev.lrxh.api.match.IMatch;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchNewRoundStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final IMatch match;

    public MatchNewRoundStartEvent(IMatch match) {
        this.match = match;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
