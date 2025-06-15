package dev.lrxh.neptune.feature.queue.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.kit.Kit;
import org.bukkit.entity.Player;

public class QueueCommand {

    @Command(name = "", desc = "", usage = "<kit>")
    public void queue(@Sender Player player, Kit kit) {
        QueueService.get().add(new QueueEntry(kit, player.getUniqueId()), true);
    }
}