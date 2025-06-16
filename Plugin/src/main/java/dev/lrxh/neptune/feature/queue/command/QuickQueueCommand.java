package dev.lrxh.neptune.feature.queue.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.kit.Kit;
import org.bukkit.entity.Player;

public class QuickQueueCommand {

    @Command(name = "", desc = "")
    public void queue(@Sender Player player) {
        for (Kit kit : QueueService.get().getAllQueues().keySet()) {
            QueueService.get().add(new QueueEntry(kit, player.getUniqueId()), true);
            break;
        }
    }
}