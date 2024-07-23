package dev.lrxh.neptune.kit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.database.impl.DataDocument;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.menu.KitManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

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

        new KitManagementMenu(kit).openMenu(player.getUniqueId());
    }

    @Subcommand("create")
    @Syntax("<kitName>")
    public void create(Player player, String kitName) {
        if (player == null) return;
        if (checkKit(kitName)) {
            player.sendMessage(CC.error("Kit already exists!"));
            return;
        }

        Kit kit = new Kit(kitName, Arrays.asList(player.getInventory().getContents()), PlayerUtil.getItemInHand(player.getUniqueId()), plugin);

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

    @Subcommand("rename")
    @Syntax("<kit> <name>")
    @CommandCompletion("@kits")
    public void rename(Player player, String kitName, String name) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        Kit kit = plugin.getKitManager().getKitByName(kitName);

        kit.setDisplayName(name);

        plugin.getKitManager().saveKits();

        player.sendMessage(CC.color("&aSuccessfully renamed kit display name!"));
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
        Kit kit = plugin.getKitManager().getKitByName(kitName);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PlayerUtil.kick(onlinePlayer.getUniqueId(), "&cUpdating player data...");
        }

        int i = 0;
        for (DataDocument document : plugin.getDatabaseManager().getDatabase().getAll()) {
            DataDocument kitStatistics = document.getDataDocument("kitData");
            DataDocument kitDocument = kitStatistics.getDataDocument(kit.getName());

            kitDocument.put("kit", "");
            i++;

            kitStatistics.put("kitData", kitDocument);

            plugin.getDatabaseManager().getDatabase().replace(document.getString("uuid"), document);
        }
        ServerUtils.sendMessage("Updated kit for " + i + " players!");
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

        if (arena instanceof StandAloneArena standAloneArena) {
            standAloneArena.addCopiesToKits();
        }

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

        if (arena instanceof StandAloneArena standAloneArena) {
            for (StandAloneArena copy : standAloneArena.getCopies()) {
                standAloneArena.removeCopyFromKits(copy);
            }
        }

        kit.getArenas().remove(arena);
        plugin.getKitManager().saveKits();
        player.sendMessage(CC.color("&aSuccessfully removed arena &7(" + arena.getDisplayName() + "&7) from kit &7(" + kit.getDisplayName() + "&7)!"));
    }

    private boolean checkKit(String kitName) {
        return plugin.getKitManager().getKitByName(kitName) != null;
    }
}