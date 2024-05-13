package dev.lrxh.neptune.profile.data;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Data
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

    public List<ItemStack> getKit(){
        return kit == null ? new ArrayList<>() : kit;
    }

    public double getKdr() {
        double kd = wins;
        if (losses > 0) {
            kd = (double) wins / losses;
            BigDecimal bd = new BigDecimal(kd);
            BigDecimal bd2 = bd.setScale(1, RoundingMode.HALF_UP);
            kd = bd2.doubleValue();
        }
        return kd;
    }
}

