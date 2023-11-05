package net.frozenorb.foxtrot.dimension.impl;

import net.frozenorb.foxtrot.dimension.AbstractDimension;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SpaceDimension extends AbstractDimension {

    @Override
    public String getDimensionName() {
        return ChatColor.AQUA + "Space";
    }

    @Override
    public String getWorldName() {
        return "Space_Dimension";
    }

    @Override
    public Material getIcon() {
        return Material.ENDER_STONE;
    }

    @Override
    public World.Environment getEnvironment() {
        return World.Environment.THE_END;
    }

    @Override
    public int getPoints() {
        return 0;
    }

    @Override
    public boolean requiresPowerFaction() {
        return true;
    }

}
