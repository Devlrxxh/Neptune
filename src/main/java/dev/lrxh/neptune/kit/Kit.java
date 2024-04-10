package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;

@Getter
@Setter
public class Kit {
    private final HashSet<Arena> arenas = new HashSet<>();
    private ArrayList<ItemStack> items;
    private ArrayList<ItemStack> armour;
    private String name;
    private String displayName;
    private boolean ranked;
    //RULES
    private boolean build;
}
