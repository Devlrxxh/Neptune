package dev.lrxh.neptune.queue.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class QueueSelectButton extends Button {
    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {

        return new ItemBuilder(kit.getIcon()).name(MenusLocale.QUEUE_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(ItemUtils.getLore(MenusLocale.QUEUE_SELECT_LORE.getStringList(),
                        new Replacement("<kit>", kit.getDisplayName()),
                        new Replacement("<playing>", String.valueOf(kit.getPlaying())),
                        new Replacement("<queue>", String.valueOf(kit.getQueue()))))
                .clearFlags()
                .amount(kit.getPlaying())
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        plugin.getQueueManager().add(player.getUniqueId(), new Queue(kit));
        MessagesLocale.QUEUE_JOIN.send(player.getUniqueId(),
                new Replacement("<kit>", kit.getDisplayName()));
        player.closeInventory();
    }
}