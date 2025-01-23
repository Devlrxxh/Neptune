package dev.lrxh.neptune.queue.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.queue.QueueManager;
import org.bukkit.entity.Player;

public class QueueCommand {

    @Command(name = "", desc = "", usage = "<kit>")
    public void queue(@Sender Player player, Kit kit) {
        QueueManager.get().add(player.getUniqueId(), new Queue(kit));
    }
}