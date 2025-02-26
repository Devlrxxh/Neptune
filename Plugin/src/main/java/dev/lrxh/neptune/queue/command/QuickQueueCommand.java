package dev.lrxh.neptune.queue.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.queue.QueueService;
import dev.lrxh.neptune.queue.menu.QueueMenu;
import org.bukkit.entity.Player;

public class QuickQueueCommand {

    @Command(name = "", desc = "")
    public void queue(@Sender Player player) {
        for (Queue entry : QueueService.get().queues.values()) {
            QueueService.get().add(player.getUniqueId(), new Queue(entry.getKit()));
            break;
        }
    }
}