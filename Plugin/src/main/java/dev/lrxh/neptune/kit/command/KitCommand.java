package dev.lrxh.neptune.kit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaManager;
import dev.lrxh.neptune.database.DatabaseManager;
import dev.lrxh.neptune.database.impl.DataDocument;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
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
    

    @Subcommand("list")
    public void list(Player player) {
        if (player == null)
            return;
        if (KitManager.get().kits.isEmpty()) {
            player.sendMessage(CC.error("No kits found!"));
            return;
        }
        player.sendMessage(CC.color("&7&m----------------------------------"));
        player.sendMessage(CC.color("&9Kits: "));
        player.sendMessage(" ");
        KitManager.get().kits.forEach(kit -> player.sendMessage(CC.color("&7- &9" + kit.getName() + " &7 | " + kit.getDisplayName())));
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
        Kit kit = KitManager.get().getKitByName(kitName);

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

        Kit kit = new Kit(kitName, Arrays.asList(player.getInventory().getContents()), PlayerUtil.getItemInHand(player.getUniqueId()));

        KitManager.get().kits.add(kit);
        KitManager.get().saveKits();
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
        Kit kit = KitManager.get().getKitByName(kitName);

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

        Kit kit = KitManager.get().getKitByName(kitName);

        kit.setItems(Arrays.asList(player.getInventory().getContents()));

        KitManager.get().saveKits();

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
        Kit kit = KitManager.get().getKitByName(kitName);

        kit.setDisplayName(name);

        KitManager.get().saveKits();

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

        Kit kit = KitManager.get().getKitByName(kitName);

        kit.delete();

        KitManager.get().saveKits();

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
        Kit kit = KitManager.get().getKitByName(kitName);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PlayerUtil.kick(onlinePlayer.getUniqueId(), "&cUpdating player data...");
        }

        int i = 0;
        for (DataDocument document : DatabaseManager.get().getDatabase().getAll()) {
            DataDocument kitStatistics = document.getDataDocument("kitData");
            DataDocument kitDocument = kitStatistics.getDataDocument(kit.getName());

            kitDocument.put("kit", "");
            i++;

            kitStatistics.put("kitData", kitDocument);

            DatabaseManager.get().getDatabase().replace(document.getString("uuid"), document);
        }
        ServerUtils.info("Updated kit for " + i + " players!");
    }

    @Subcommand("setIcon")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void setIcon(Player player, String kitName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        Kit kit = KitManager.get().getKitByName(kitName);

        kit.setIcon(PlayerUtil.getItemInHand(player.getUniqueId()));

        KitManager.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully set kit icon!"));
    }

    @Subcommand("setSlot")
    @Syntax("<kit> <slo>")
    @CommandCompletion("@kits")
    public void setSlot(Player player, String kitName, int slot) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        Kit kit = KitManager.get().getKitByName(kitName);

        kit.setSlot(slot);

        KitManager.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully set kit slot!"));
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

        Arena arena = ArenaManager.get().getArenaByName(arenaName);

        if (arena == null) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }

        Kit kit = KitManager.get().getKitByName(kitName);

        if (kit.getArenas().contains(arena)) {
            player.sendMessage(CC.error("Arena is already added!"));
            return;
        }

        kit.getArenas().add(arena);

        KitManager.get().saveKits();
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

        if (ArenaManager.get().getArenaByName(arenaName) == null) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Kit kit = KitManager.get().getKitByName(kitName);
        Arena arena = ArenaManager.get().getArenaByName(arenaName);

        if (!kit.getArenas().contains(arena)) {
            player.sendMessage(CC.error("Arena isn't added to the kit!"));
            return;
        }

        kit.getArenas().remove(arena);
        KitManager.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully removed arena &7(" + arena.getDisplayName() + "&7) from kit &7(" + kit.getDisplayName() + "&7)!"));
    }

    private boolean checkKit(String kitName) {
        return KitManager.get().getKitByName(kitName) != null;
    }
}