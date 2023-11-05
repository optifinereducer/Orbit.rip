package net.frozenorb.foxtrot.dimension.impl;

import net.frozenorb.foxtrot.dimension.AbstractDimension;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;

public class OverworldDimension extends AbstractDimension {

    @Override
    public String getDimensionName() {
        return ChatColor.GRAY + "Overworld";
    }

    @Override
    public String getWorldName() {
        return "world";
    }

    @Override
    public Material getIcon() {
        return Material.GRASS;
    }

    @Override
    public World.Environment getEnvironment() {
        return World.Environment.NORMAL;
    }

    @Override
    public int getPoints() {
        return 0;
    }

    @Override
    public boolean requiresPowerFaction() {
        return false;
    }
}
