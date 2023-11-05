package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.util.modsuite.ModUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class StaffUtilsListener implements Listener {

    private Location lastDamageLocation;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            lastDamageLocation = event.getEntity().getLocation();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (lastDamageLocation != null
                && event.getItem() != null
                && event.getItem().getType() == Material.DIAMOND
                && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            event.getPlayer().teleport(lastDamageLocation);
        }
    }

    @EventHandler
    public void onPlayerInteract2(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && ModUtils.isInvisible(event.getPlayer()) && (event.getClickedBlock().getType() == Material.CROPS || event.getClickedBlock().getType() == Material.SOIL)) {
            event.setCancelled(true);
        }
    }

}