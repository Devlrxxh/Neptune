package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.arena.Arena;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Kit {
    private String name;
    private String displayName;
    private boolean ranked;
    private List<ItemStack> items;
    private List<ItemStack> armour;
    private HashSet<Arena> arenas;
    private ItemStack icon;
    //RULES
    private boolean build;
    private boolean hunger;
    private boolean sumo;
    private boolean fallDamage;
    private boolean denyMovement;
    private boolean bedwars;


    public List<String> getArenasAsString() {
        List<String> arenasString = null;
        if (arenas != null && !arenas.isEmpty()) {
            arenasString = new ArrayList<>();
            for (Arena arena : arenas) {
                arenasString.add(arena.getName());
            }
        }
        return arenasString;
    }
}

