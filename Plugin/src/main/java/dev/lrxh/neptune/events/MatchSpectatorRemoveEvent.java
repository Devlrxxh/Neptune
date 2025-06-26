package dev.lrxh.neptune.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.lrxh.neptune.game.match.Match;
import lombok.Getter;

public class MatchSpectatorRemoveEvent extends Event {
  @Getter private final Match match;
  @Getter private final Player player;

  public MatchSpectatorRemoveEvent(Match match, Player player) {
    this.match = match;
    this.player = player;
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
