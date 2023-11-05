package net.frozenorb.foxtrot.dimension.impl;

import net.frozenorb.foxtrot.dimension.AbstractDimension;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;

public class HellDimension extends AbstractDimension {

    @Override
    public String getDimensionName() {
        return ChatColor.RED + "Hell";
    }

    @Override
    public String getWorldName() {
        return "Hell_Dimension";
    }

    @Override
    public Material getIcon() {
        return Material.NETHERRACK;
    }

    @Override
    public World.Environment getEnvironment() {
        return World.Environment.NETHER;
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
