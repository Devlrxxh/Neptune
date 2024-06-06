package dev.lrxh.neptune.kit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.menu.KitManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

@CommandAlias("kit")
@CommandPermission("neptune.admin.kit")
@Description("Command to manage and create new kits.")
public class KitCommand extends BaseCommand {
    private final Neptune plugin = Neptune.get();

    @Subcommand("list")
    public void list(Player player) {
        if (player == null)
            return;
        if (plugin.getKitManager().kits.isEmpty()) {
            player.sendMessage(CC.error("No kits found!"));
            return;
        }
        player.sendMessage(CC.color("&7&m----------------------------------"));
        player.sendMessage(CC.color("&9Kits: "));
        player.sendMessage(" ");
        plugin.getKitManager().kits.forEach(kit -> player.sendMessage(CC.color("&7- &9" + kit.getName() + " &7 | " + kit.getDisplayName())));
        player.sendMessage(CC.color("&7&m----------------------------------"));
    }

    @Subcommand("manage")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void manage(Player player, String kitName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        Kit kit = plugin.getKitManager().getKitByName(kitName);

        new KitManagementMenu(kit).openMenu(player);
    }

    @Subcommand("create")
    @Syntax("<kitName>")
    public void create(Player player, String kitName) {
        if (player == null) return;
        if (checkKit(kitName)) {
            player.sendMessage(CC.error("Kit already exists!"));
            return;
        }

        Kit kit = new Kit(kitName, Arrays.asList(player.getInventory().getContents()), PlayerUtil.getItemInHand(player.getUniqueId()));

        plugin.getKitManager().kits.add(kit);
        plugin.getKitManager().saveKits();
        player.sendMessage(CC.color("&aSuccessfully created new Kit!"));
    }

    @Subcommand("getinv")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void getinv(Player player, String kitName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        Kit kit = plugin.getKitManager().getKitByName(kitName);

        player.getInventory().setContents(kit.getItems().toArray(new ItemStack[0]));

        player.sendMessage(CC.color("&aSuccessfully given kit load out!"));
    }


    @Subcommand("setinv")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void setinv(Player player, String kitName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
            player.sendMessage(CC.error("You need to be in survival mode!"));
            return;
        }

        Kit kit = plugin.getKitManager().getKitByName(kitName);

        kit.setItems(Arrays.asList(player.getInventory().getContents()));

        plugin.getKitManager().saveKits();

        player.sendMessage(CC.color("&aSuccessfully set kit load out!"));
        player.sendMessage(CC.color("&4IMPORTANT &8- &cMake sure to run /kit updateDB " + kitName));
    }

    @Subcommand("delete")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void delete(Player player, String kitName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }

        Kit kit = plugin.getKitManager().getKitByName(kitName);

        kit.delete();

        plugin.getKitManager().saveKits();

        player.sendMessage(CC.color("&aSuccessfully deleted kit!"));
    }

    @Subcommand("updateDB")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void updateDB(Player player, String kitName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        int i = 0;
        Kit kit = plugin.getKitManager().getKitByName(kitName);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PlayerUtil.kick(onlinePlayer.getUniqueId(), "&cUpdating player data...");
        }

        for (Document document : plugin.getMongoManager().collection.find()) {
            Document kitStatistics = (Document) document.get("kitData");
            Document kitDocument = (Document) kitStatistics.get(kit.getName());

            if (kitDocument != null && !Objects.equals(kitDocument.getString("kit"), "")) {
                kitDocument.put("kit", "");
                i++;
            }

            kitStatistics.put("kitData", kitDocument);
            plugin.getMongoManager().collection.replaceOne(Filters.eq("uuid", document.get("uuid")), document, new ReplaceOptions().upsert(true));
        }
        ServerUtils.sendMessage("&aUpdated kit for " + i + " players!");
    }

    @Subcommand("seticon")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void seticon(Player player, String kitName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        Kit kit = plugin.getKitManager().getKitByName(kitName);

        kit.setIcon(PlayerUtil.getItemInHand(player.getUniqueId()));

        plugin.getKitManager().saveKits();
        player.sendMessage(CC.color("&aSuccessfully set kit icon!"));
    }

    @Subcommand("addArena")
    @Syntax("<kit> <arena>")
    @CommandCompletion("@kits @arenas")
    public void addArena(Player player, String kitName, String arenaName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaByName(arenaName);

        if (arena == null) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }

        Kit kit = plugin.getKitManager().getKitByName(kitName);

        if (kit.getArenas().contains(arena)) {
            player.sendMessage(CC.error("Arena is already added!"));
            return;
        }

        kit.getArenas().add(arena);
        plugin.getKitManager().saveKits();
        player.sendMessage(CC.color("&aSuccessfully added arena &7(" + arena.getDisplayName() + "&7) for kit &7(" + kit.getDisplayName() + "&7)!"));
    }

    @Subcommand("removeArena")
    @Syntax("<kit> <arena>")
    @CommandCompletion("@kits @arenas")
    public void removeArena(Player player, String kitName, String arenaName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }

        if (plugin.getArenaManager().getArenaByName(arenaName) == null) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Kit kit = plugin.getKitManager().getKitByName(kitName);
        Arena arena = plugin.getArenaManager().getArenaByName(arenaName);

        if (!kit.getArenas().contains(arena)) {
            player.sendMessage(CC.error("Arena isn't added to the kit!"));
            return;
        }

        kit.getArenas().remove(arena);
        plugin.getKitManager().saveKits();
        player.sendMessage(CC.color("&aSuccessfully removed arena &7(" + arena.getDisplayName() + "&7) from kit &7(" + kit.getDisplayName() + "&7)!"));
    }

    private boolean checkKit(String kitName) {
        return plugin.getKitManager().getKitByName(kitName) != null;
    }
}