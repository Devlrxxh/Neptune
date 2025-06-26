package dev.lrxh.neptune.events.impl;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.lrxh.neptune.game.match.Match;
import lombok.Getter;

public class MatchEndEvent extends Event {
  @Getter
  private final Match match;

  public MatchEndEvent(Match match) {
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
