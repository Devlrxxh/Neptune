package dev.lrxh.neptune.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.lrxh.neptune.game.match.Match;
import lombok.Getter;

public class MatchNewRoundStartEvent extends Event {
  @Getter
  private final Match match;

  public MatchNewRoundStartEvent(Match match) {
    this.match = match;
  }
  
  private static final HandlerList handlers = new HandlerList();
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
