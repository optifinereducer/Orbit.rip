package net.frozenorb.foxtrot.dimension;

import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractDimension {

    public abstract String getDimensionName();
    public abstract String getWorldName();
    public abstract Material getIcon();
    public abstract World.Environment getEnvironment();
    public abstract int getPoints();
    public abstract boolean requiresPowerFaction();

    public void onEnter(Player player){

    }

    public void onExit(Player player){

    }

}
