package dev.lrxh.neptune.queue.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class QueueSelectButton extends Button {
    private final Kit kit;
    private final boolean ranked;

    public QueueSelectButton(Kit kit, boolean ranked) {
        this.kit = kit;
        this.ranked = ranked;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getIcon().getType()).name(MenusLocale.QUEUE_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .durability(kit.getIcon().getDurability()).clearFlags().build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        plugin.getQueueManager().addToQueue(player.getUniqueId(), new Queue(kit, ranked));
        MessagesLocale.QUEUE_JOIN.send(player.getUniqueId(),
                "<type>", ranked ? "Ranked" : "Unranked", "<kit>", kit.getDisplayName());
        player.closeInventory();
    }
}