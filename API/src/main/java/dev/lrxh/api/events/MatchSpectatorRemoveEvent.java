package dev.lrxh.api.events;


import dev.lrxh.api.match.IMatch;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchSpectatorRemoveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final IMatch match;
    @Getter
    private final Player player;

    public MatchSpectatorRemoveEvent(IMatch match, Player player) {
        this.match = match;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
