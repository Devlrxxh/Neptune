package dev.lrxh.neptune.cosmetics.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.security.SecureRandom;
import java.util.List;

@Getter
@AllArgsConstructor
public class KillMessagePackage {
    private final SecureRandom secureRandom = new SecureRandom();
    private String name;
    private String displayName;
    private Material material;
    private List<String> description;
    private int slot;
    private List<String> messages;

    public String getRandomMessage() {
        return messages.get(secureRandom.nextInt(messages.size()));
    }

    public String permission() {
        return "neptune.cosmetics.killmessages." + name.toLowerCase();
    }
}
