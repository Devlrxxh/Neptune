package dev.lrxh.neptune.profile.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class KitData {
    private int wins;
    private int losses;
    private int bestStreak;
    private int currentStreak;
    private int elo;
    private List<ItemStack> kit;

    public KitData() {
        this.kit = new ArrayList<>();
    }
}

