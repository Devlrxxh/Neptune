package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class TtlAction {
    private final UUID playerUUID;
    private final Consumer<Player> consumer;
    private NeptuneRunnable runnable;

    public TtlAction(UUID playerUUID, Consumer<Player> consumer) {
        this.playerUUID = playerUUID;
        this.consumer = consumer;
    }

    public void setRunnable(NeptuneRunnable runnable, long leaveTime) {
        this.runnable = runnable;
        runnable.startLater(leaveTime * 20L);
    }
}
