package dev.lrxh.blockChanger.wrapper.impl.world;

import dev.lrxh.blockChanger.wrapper.CraftWrapper;
import org.bukkit.World;

public class CraftWorld extends CraftWrapper<World> {
    public CraftWorld(World input) {
        super(input);
    }

    @Override
    protected Object apply(World input) {
        Class<?> craftWorldClass = cb("CraftWorld");
        return craftWorldClass.cast(input);
    }
}
