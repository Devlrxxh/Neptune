package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public class TtlAction {
    private final UUID playerUUID;
    private final Consumer<Player> consumer;
    private NeptuneRunnable runnable;

    public void setRunnable(NeptuneRunnable runnable, long leaveTime) {
        this.runnable = runnable;
        Neptune.get().getTaskScheduler().startTaskLater(runnable, leaveTime * 20L);
    }
}
