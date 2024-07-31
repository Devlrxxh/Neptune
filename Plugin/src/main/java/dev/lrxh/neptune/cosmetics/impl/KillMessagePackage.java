package dev.lrxh.neptune.cosmetics.impl;

import dev.lrxh.neptune.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

@Getter
@AllArgsConstructor
public class KillMessagePackage {
    private String name;
    private String displayName;
    private Material material;
    private List<String> description;
    private int slot;
    private List<String> messages;

    public String getRandomMessage() {
        return messages.get(RandomUtils.getNextInt(messages.size()));
    }

    public String permission() {
        return "neptune.cosmetics.killmessages." + name.toLowerCase();
    }
}
