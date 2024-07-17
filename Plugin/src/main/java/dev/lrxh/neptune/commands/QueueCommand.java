package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.queue.menu.QueueMenu;
import org.bukkit.entity.Player;

@CommandAlias("queue")
@Description("Queue Selection command.")
public class QueueCommand extends BaseCommand {
    private final Neptune plugin = Neptune.get();

    @Default
    public void open(Player player){
        new QueueMenu().openMenu(player.getUniqueId());
    }

    @Default
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void queue(Player player, String kitName) {
        if (plugin.getKitManager().getKitByName(kitName) != null) {
            Queue queue = new Queue(plugin.getKitManager().getKitByName(kitName));

            plugin.getQueueManager().addToQueue(player.getUniqueId(), queue);
            MessagesLocale.QUEUE_JOIN.send(player.getUniqueId(),
                    new Replacement("<kit>", queue.getKit().getDisplayName()));
        }
    }
}