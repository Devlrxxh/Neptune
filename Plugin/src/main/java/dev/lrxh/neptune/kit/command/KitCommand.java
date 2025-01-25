package dev.lrxh.neptune.kit.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.database.DatabaseService;
import dev.lrxh.neptune.database.impl.DataDocument;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import dev.lrxh.neptune.providers.tasks.TaskScheduler;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KitCommand {
    @Command(name = "updateDB", desc = "", usage = "<kit>", async = false)
    public void updateDB(@Sender Player player, Kit kit) {
        int i = 0;
        Neptune.get().setAllowJoin(false);
        TaskScheduler.get().startTask(new NeptuneRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    PlayerUtil.kick(p, "&cUpdating Database, please rejoin");
                });
            }
        });
        for (DataDocument document : DatabaseService.get().getDatabase().getAll()) {
            DataDocument kitStatistics = document.getDataDocument("kitData");
            DataDocument kitDocument = kitStatistics.getDataDocument(kit.getName());

            kitDocument.put("kit", "");
            i++;

            kitStatistics.put("kitData", kitDocument);

            DatabaseService.get().getDatabase().replace(document.getString("uuid"), document);
        }
        Neptune.get().setAllowJoin(true);
        ServerUtils.info("Updated kit for " + i + " players!");
    }
}