package dev.lrxh.api.features;

import java.util.List;

import org.bukkit.Material;

public interface IKillMessagePackage {
    String getName();
    String getDisplayName();
    Material getMaterial();
    List<String> getDescription();
    int getSlot();
    List<String> getMessages();
    String getRandomMessage();
    String permission();
}
