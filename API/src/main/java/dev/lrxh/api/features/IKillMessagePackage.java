package dev.lrxh.api.features;

import org.bukkit.Material;

import java.util.List;

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
