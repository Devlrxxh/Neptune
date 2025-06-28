package dev.lrxh.neptune.events;

import dev.lrxh.neptune.game.match.Match;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Match match;

    public MatchStartEvent(Match match) {
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
