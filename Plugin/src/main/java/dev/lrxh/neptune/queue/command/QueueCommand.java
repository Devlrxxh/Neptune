package dev.lrxh.neptune.queue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.queue.QueueManager;
import dev.lrxh.neptune.queue.menu.QueueMenu;
import org.bukkit.entity.Player;

@CommandAlias("queue")
@Description("Queue Selection command.")
public class QueueCommand extends BaseCommand {


    @Default
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void queue(Player player, String kitName) {
        if (KitManager.get().getKitByName(kitName) != null) {
            Queue queue = new Queue(KitManager.get().getKitByName(kitName));
            QueueManager.get().add(player.getUniqueId(), queue);
        } else if (kitName.equals("queueLast")) {
            Kit kit = KitManager.get().getKitByName(API.getProfile(player).getGameData().getLastKit());
            if (kit != null) {
                QueueManager.get().add(player.getUniqueId(), new Queue(kit));
            }
        } else {
            new QueueMenu().openMenu(player.getUniqueId());
        }
    }
}