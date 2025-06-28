package dev.lrxh.neptune.events;

import dev.lrxh.neptune.game.match.Match;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchSpectatorAddEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Match match;
    @Getter
    private final Player player;

    public MatchSpectatorAddEvent(Match match, Player player) {
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
