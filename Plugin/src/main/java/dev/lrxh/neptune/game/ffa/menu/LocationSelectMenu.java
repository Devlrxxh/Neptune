package dev.lrxh.neptune.game.ffa.menu;

import dev.lrxh.neptune.game.ffa.FFAArena;
import dev.lrxh.neptune.game.ffa.FFAService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LocationSelectMenu extends Menu {

    private final Kit kit;

    private FFAArena arena;

    public LocationSelectMenu(Kit kit) {
        super("Select Spawn", 27, Filter.NONE);
        this.kit = kit;
        this.arena = FFAService.get().getKitArena(kit);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        Profile profile = ProfileService.get().getByUUID(player.getUniqueId());
        if (profile == null) return buttons;

        List<String> spawnKeys = new ArrayList<>(arena.getSpawnLocations().keySet());
        int count = spawnKeys.size();
        if (count == 0) return buttons;

        int[] slots = getCenteredSlots(count);

        for (int i = 0; i < count; i++) {
            final String spawnName = spawnKeys.get(i);
            int slot = slots[i];

            buttons.add(new LocationSelectButton(slot, arena, spawnName, kit) {});
        }

        return buttons;
    }

    private int[] getCenteredSlots(int count) {
        switch (count) {
            case 1: return new int[]{13}; // Middle
            case 2: return new int[]{12, 14}; // Split around middle
            case 3: return new int[]{11, 13, 15};
            case 4: return new int[]{10, 12, 14, 16};
            case 5: return new int[]{10, 12, 13, 14, 16};
            case 6: return new int[]{10, 11, 12, 14, 15, 16};
            case 7: return new int[]{10, 11, 12, 13, 14, 15, 16};
            case 8: return new int[]{9, 10, 11, 12, 14, 15, 16, 17};
            case 9: return new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17};
            default:
                int[] slots = new int[count];
                for (int i = 0; i < count; i++) {
                    slots[i] = i < 27 ? i : 0; // Safety: cap at 27
                }
                return slots;
        }
    }
}
