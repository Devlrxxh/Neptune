package dev.lrxh.neptune.queue.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QueueSelectButton extends Button {
    private final Kit kit;
    private final boolean ranked;

    public QueueSelectButton(Kit kit, boolean ranked) {
        this.kit = kit;
        this.ranked = ranked;
    }

    @Override
    public ItemStack getButtonItem(Player player) {

        List<String> lore = new ArrayList<>();

        if (!ranked) {
            MenusLocale.QUEUE_SELECT_UNRANKED_LORE.getStringList().forEach(line -> {
                line = line.replaceAll("<playing>", String.valueOf(kit.getUnrankedPlaying()));
                line = line.replaceAll("<queue>", String.valueOf(kit.getUnrankedQueue()));
                line = line.replaceAll("<kit>", kit.getDisplayName());
                lore.add(line);
            });
        } else {
            MenusLocale.QUEUE_SELECT_RANKED_LORE.getStringList().forEach(line -> {
                line = line.replaceAll("<playing>", String.valueOf(kit.getRankedPlaying()));
                line = line.replaceAll("<queue>", String.valueOf(kit.getRankedQueue()));
                line = line.replaceAll("<kit>", kit.getDisplayName());
                lore.add(line);
            });
        }

        return new ItemBuilder(kit.getIcon().getType()).name(MenusLocale.QUEUE_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .amount(ranked ? kit.getRankedPlaying() : kit.getUnrankedPlaying())
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        plugin.getQueueManager().addToQueue(player.getUniqueId(), new Queue(kit, ranked));
        MessagesLocale.QUEUE_JOIN.send(player.getUniqueId(),
                new Replacement("<type>", ranked ? "Ranked" : "Unranked"),
                new Replacement("<kit>", kit.getDisplayName()));
        player.closeInventory();
    }
}