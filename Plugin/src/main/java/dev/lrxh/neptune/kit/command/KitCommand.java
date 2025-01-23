package dev.lrxh.neptune.kit.command;


import com.jonahseguin.drink.annotation.Command;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.database.DatabaseManager;
import dev.lrxh.neptune.database.impl.DataDocument;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.kit.menu.KitManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class KitCommand {

    @Command(name = "list", desc = "")
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

    @Command(name = "manage", desc = "", usage = "<kit>")
    public void manage(Player player, Kit kit) {
        new KitManagementMenu(kit).open(player);
    }

    @Command(name = "create", desc = "", usage = "<name>")
    public void create(Player player, String kitName) {
        if (checkKit(kitName)) {
            player.sendMessage(CC.error("Kit already exists!"));
            return;
        }

        Kit kit = new Kit(kitName, Arrays.asList(player.getInventory().getContents()), player.getInventory().getItemInMainHand());

        KitManager.get().kits.add(kit);
        KitManager.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully created new Kit!"));
    }

    @Command(name = "getinv", desc = "", usage = "<kit>")
    public void getinv(Player player, Kit kit) {
        player.getInventory().setContents(kit.getItems().toArray(new ItemStack[0]));
        player.sendMessage(CC.color("&aSuccessfully given kit load out!"));
    }


    @Command(name = "setinv", desc = "", usage = "<kit>")
    public void setinv(Player player, Kit kit) {
        kit.setItems(Arrays.asList(player.getInventory().getContents()));

        KitManager.get().saveKits();

        player.sendMessage(CC.color("&aSuccessfully set kit load out!"));
        player.sendMessage(CC.color("&4IMPORTANT &8- &cMake sure to run /kit updateDB " + kit.getName()));
    }


    @Command(name = "rename", desc = "", usage = "<kit> <name>")
    public void rename(Player player, Kit kit, String name) {
        kit.setDisplayName(name);

        KitManager.get().saveKits();

        player.sendMessage(CC.color("&aSuccessfully renamed kit display name!"));
    }


    @Command(name = "delete", desc = "", usage = "<kit>")
    public void delete(Player player, Kit kit) {
        kit.delete();

        KitManager.get().saveKits();

        player.sendMessage(CC.color("&aSuccessfully deleted kit!"));
    }

    @Command(name = "updateDB", desc = "", usage = "<kit>")
    public void updateDB(Player player, Kit kit) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PlayerUtil.kick(onlinePlayer, "&cUpdating player data...");
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

    @Command(name = "setIcon", desc = "", usage = "<kit>")
    public void setIcon(Player player, Kit kit) {
        kit.setIcon(player.getInventory().getItemInMainHand());

        KitManager.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully set kit icon!"));
    }

    @Command(name = "setSlot", desc = "", usage = "<kit> <slot>")
    public void setSlot(Player player, Kit kit, int slot) {
        kit.setSlot(slot);

        KitManager.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully set kit slot!"));
    }

    @Command(name = "setIcon", desc = "", usage = "<kit> <arena>")
    public void addArena(Player player, Kit kit, Arena arena) {
        if (kit.getArenas().contains(arena)) {
            player.sendMessage(CC.error("Arena is already added!"));
            return;
        }

        kit.getArenas().add(arena);

        KitManager.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully added arena &7(" + arena.getDisplayName() + "&7) for kit &7(" + kit.getDisplayName() + "&7)!"));
    }

    @Command(name = "removeArena", desc = "", usage = "<kit> <arena>")
    public void removeArena(Player player, Kit kit, Arena arena) {
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