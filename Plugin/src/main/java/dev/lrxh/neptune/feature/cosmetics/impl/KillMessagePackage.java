package dev.lrxh.neptune.feature.cosmetics.impl;

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

    private int slot;

    private List<String> description;
    private List<String> messages;

    /**
     * Returns a random message from the package's list of messages.
     *
     * @return a randomly selected kill message
     */
    public String getRandomMessage() {
        if (messages == null || messages.isEmpty()) {
            return ""; // Return empty string if no messages are available
        }
        return messages.get(RandomUtils.getRandInt(messages.size()));
    }

    /**
     * Returns the permission string required to use this kill message package.
     *
     * @return the permission string
     */
    public String permission() {
        return "neptune.cosmetics.killmessages." + name.toLowerCase();
    }
}
