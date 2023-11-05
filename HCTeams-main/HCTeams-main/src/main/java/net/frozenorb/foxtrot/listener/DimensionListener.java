package net.frozenorb.foxtrot.listener;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.dimension.AbstractDimension;
import net.frozenorb.foxtrot.dimension.impl.HellDimension;
import net.frozenorb.foxtrot.dimension.impl.OverworldDimension;
import net.frozenorb.foxtrot.dimension.impl.SpaceDimension;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.ArrayList;
import java.util.List;

public class DimensionListener implements Listener {

    @Getter private static final List<AbstractDimension> dimensions = new ArrayList<>();

    public DimensionListener(){

        dimensions.add(new OverworldDimension());
        dimensions.add(new SpaceDimension());
        dimensions.add(new HellDimension());

        dimensions.forEach(dimension -> {
            World world = Bukkit.getWorld(dimension.getWorldName());
            if(world == null){
                System.out.println("World for dimension '" + dimension.getDimensionName() + "' not found! Creating it for you...");
                Bukkit.createWorld(new WorldCreator(dimension.getWorldName()).environment(dimension.getEnvironment()));
            }
        });

    }

    @EventHandler
    public static void dimensionChange(PlayerChangedWorldEvent event){

        Player player = event.getPlayer();
        World world = event.getPlayer().getWorld();

        AbstractDimension fromDimension = dimensions.stream().filter(dimension -> dimension.getWorldName().equalsIgnoreCase(event.getFrom().getName())).findFirst().orElse(null);
        AbstractDimension dimension = dimensions.stream().filter(d -> d.getWorldName().equalsIgnoreCase(world.getName())).findFirst().orElse(null);

        player.sendMessage(ChatColor.YELLOW + "Now leaving: " + (fromDimension != null ? fromDimension.getDimensionName() + " Dimension" : (event.getFrom().getEnvironment() == World.Environment.NORMAL ? ChatColor.GRAY + "The Overworld" : (event.getFrom().getEnvironment() == World.Environment.NETHER ? ChatColor.RED + "The Nether" : ChatColor.DARK_PURPLE + "The End"))));
        player.sendMessage(ChatColor.YELLOW + "Now entering: " + (dimension != null ? dimension.getDimensionName() + " Dimension" : (world.getEnvironment() == World.Environment.NORMAL ? ChatColor.GRAY + "The Overworld" : (world.getEnvironment() == World.Environment.NETHER ? ChatColor.RED + "The Nether" : ChatColor.DARK_PURPLE + "The End"))));
        if(fromDimension != null) fromDimension.onExit(player);
        if(dimension != null) dimension.onEnter(player);

    }

}
