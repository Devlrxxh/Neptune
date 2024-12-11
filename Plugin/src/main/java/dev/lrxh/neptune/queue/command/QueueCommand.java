package dev.lrxh.neptune.queue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.queue.menu.QueueMenu;
import org.bukkit.entity.Player;

@CommandAlias("queue")
@Description("Queue Selection command.")
public class QueueCommand extends BaseCommand {
    private final Neptune plugin = Neptune.get();

    @Default
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void queue(Player player, String kitName) {
        if (plugin.getKitManager().getKitByName(kitName) != null) {
            Queue queue = new Queue(plugin.getKitManager().getKitByName(kitName));
            plugin.getQueueManager().add(player.getUniqueId(), queue);
        } else if (kitName.equals("queueLast")) {
            Kit kit = plugin.getKitManager().getKitByName(plugin.getAPI().getProfile(player).getGameData().getLastKit());
            if (kit != null) {
                plugin.getQueueManager().add(player.getUniqueId(), new Queue(kit));
            }
        } else {
            new QueueMenu().openMenu(player.getUniqueId());
        }
    }
}