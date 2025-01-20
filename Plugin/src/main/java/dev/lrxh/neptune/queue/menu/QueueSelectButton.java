package dev.lrxh.neptune.queue.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.leaderboard.LeaderboardManager;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.leaderboard.impl.PlayerEntry;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.queue.QueueManager;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class QueueSelectButton extends Button {
    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();

        MenusLocale.QUEUE_SELECT_LORE.getStringList().forEach(line -> {
            String[] split = line.split("_");

            if (split.length != 3) {
                lore.add(line);
                return;
            }
            LeaderboardType leaderboardType = LeaderboardType.value(split[1]);
            if (leaderboardType == null) {
                lore.add(line);
                return;
            }

            List<PlayerEntry> leaderboard = LeaderboardManager.get().getPlayerEntries(kit, leaderboardType);

            int i = Integer.parseInt(split[2]);
            PlayerEntry playerEntry = null;

            if (i <= leaderboard.size()) {
                playerEntry = LeaderboardManager.get().getLeaderboardSlot(kit, leaderboardType, i);
            }

            if (playerEntry == null) {
                line = line.replaceAll("<player_" + split[1] + "_" + i + ">", "???");
                line = line.replaceAll("<value" + split[1] + "_" + i + ">", "???");
            } else {
                line = line.replaceAll("<player_" + split[1] + "_" + i + ">", playerEntry.getUsername());
                line = line.replaceAll("<value" + split[1] + "_" + i + ">", String.valueOf(playerEntry.getValue()));
            }

            lore.add(line);
        });


        return new ItemBuilder(kit.getIcon()).name(MenusLocale.QUEUE_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(ItemUtils.getLore(lore,
                        new Replacement("<kit>", kit.getDisplayName()),
                        new Replacement("<kitName>", kit.getName()),
                        new Replacement("<playing>", String.valueOf(kit.getPlaying())),
                        new Replacement("<queue>", String.valueOf(kit.getQueue()))), player)
                .clearFlags()
                .amount(kit.getPlaying())
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        QueueManager.get().add(player.getUniqueId(), new Queue(kit));
        player.closeInventory();
    }
}