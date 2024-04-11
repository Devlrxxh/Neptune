package dev.lrxh.neptune.utils.assemble.events;

import lombok.Getter;
import lombok.Setter;
import dev.lrxh.neptune.utils.assemble.AssembleBoard;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class AssembleBoardCreatedEvent extends Event {

    @Getter
    public static HandlerList handlerList = new HandlerList();
    private final AssembleBoard board;
    private boolean cancelled = false;

    /**
     * Assemble Board Created Event.
     *
     * @param board of player.
     */
    public AssembleBoardCreatedEvent(AssembleBoard board) {
        this.board = board;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
