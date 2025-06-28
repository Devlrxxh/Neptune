package dev.lrxh.neptune.events;

import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.lrxh.neptune.game.match.Match;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class MatchReadyEvent extends Event implements Cancellable {
  @Getter
  private final Match match;
  @Setter
  private boolean cancelled;

  public MatchReadyEvent(Match match) {
    this.match = match;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  private static final HandlerList handlers = new HandlerList();
  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
