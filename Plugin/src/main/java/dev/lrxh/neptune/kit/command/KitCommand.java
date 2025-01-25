package dev.lrxh.neptune.kit.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.database.DatabaseService;
import dev.lrxh.neptune.database.impl.DataDocument;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bukkit.entity.Player;

public class KitCommand {
    @Command(name = "updateDB", desc = "", usage = "<kit>")
    public void updateDB(@Sender Player player, Kit kit) {
        int i = 0;
        for (DataDocument document : DatabaseService.get().getDatabase().getAll()) {
            DataDocument kitStatistics = document.getDataDocument("kitData");
            DataDocument kitDocument = kitStatistics.getDataDocument(kit.getName());

            kitDocument.put("kit", "");
            i++;

            kitStatistics.put("kitData", kitDocument);

            DatabaseService.get().getDatabase().replace(document.getString("uuid"), document);
        }
        ServerUtils.info("Updated kit for " + i + " players!");
    }
}