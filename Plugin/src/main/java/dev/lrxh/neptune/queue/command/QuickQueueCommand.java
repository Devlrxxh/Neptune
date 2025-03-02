package dev.lrxh.neptune.queue.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.queue.QueueEntry;
import dev.lrxh.neptune.queue.QueueService;
import org.bukkit.entity.Player;

public class QuickQueueCommand {

    @Command(name = "", desc = "")
    public void queue(@Sender Player player) {
        for (QueueEntry entry : QueueService.get().queue) {
            QueueService.get().add(new QueueEntry(entry.getKit(), player.getUniqueId()));
            break;
        }
    }
}