package net.frozenorb.foxtrot.map.listener;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import java.util.List;

public class BlockDecayListeners implements Listener {

    private List<Material> PREVENTED_STATES = Lists.newArrayList(Material.DIRT, Material.GRASS, Material.WATER, Material.STATIONARY_WATER, Material.ICE);

    @EventHandler
    public void onBlockFadeEvent(BlockFadeEvent event) {
        if (PREVENTED_STATES.contains(event.getNewState().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysicsEvent(BlockPhysicsEvent event) {
        if (event.getChangedType() == Material.GRASS) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecayEvent(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

}
