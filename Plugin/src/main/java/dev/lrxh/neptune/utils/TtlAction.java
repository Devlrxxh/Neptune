package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class TtlAction {
    private final UUID playerUUID;
    private final Consumer<Player> consumer;
    private final Neptune plugin;
    private NeptuneRunnable runnable;

    public TtlAction(UUID playerUUID, Consumer<Player> consumer, Neptune plugin) {
        this.playerUUID = playerUUID;
        this.consumer = consumer;
        this.plugin = plugin;
    }

    public void setRunnable(NeptuneRunnable runnable, long leaveTime) {
        this.runnable = runnable;
        plugin.getTaskScheduler().startTaskLater(runnable, leaveTime * 20L);
    }
}
