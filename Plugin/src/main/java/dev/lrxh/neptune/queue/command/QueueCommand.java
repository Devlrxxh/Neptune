package dev.lrxh.neptune.queue.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.queue.QueueEntry;
import dev.lrxh.neptune.queue.QueueService;
import dev.lrxh.neptune.queue.menu.QueueMenu;
import org.bukkit.entity.Player;

public class QueueCommand {

    @Command(name = "menu ", desc = "")
    public void menu(@Sender Player player) {
        new QueueMenu().open(player);
    }

    @Command(name = "", desc = "", usage = "<kit>")
    public void queue(@Sender Player player, Kit kit) {
        QueueService.get().add(new QueueEntry(kit, player.getUniqueId()));
    }
}