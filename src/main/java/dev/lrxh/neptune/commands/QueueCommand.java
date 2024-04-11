package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.queue.Queue;
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
            Queue queue = new Queue(plugin.getKitManager().getKitByName(kitName), false);

            plugin.getQueueManager().addToQueue(player.getUniqueId(), queue);
            MessagesLocale.QUEUE_JOIN.send(player.getUniqueId(),

                    "<type>", queue.isRanked() ? "Ranked" : "Unranked", "<kit>", queue.getKit().getDisplayName());
        }
    }
}