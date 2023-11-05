package net.frozenorb.foxtrot.server.pearl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EnderpearlHitListeners implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getDamager() instanceof EnderPearl) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final EnderPearl enderPearl = (EnderPearl) event.getDamager();
        if (!(enderPearl.getShooter() instanceof Player)) {
            return;
        }

        final Player damager = (Player) enderPearl.getShooter();
        final Player target = (Player) event.getEntity();

        if (damager == target) {
            return;
        }

        final Location targetLocation = target.getLocation().clone();
        final BlockFace blockFace = getDirection(damager);

        if (blockFace == BlockFace.NORTH) {
            targetLocation.setZ(targetLocation.getZ() + 0.5);
        }

        if (blockFace == BlockFace.SOUTH) {
            targetLocation.setZ(targetLocation.getZ() - 0.5);
        }

        if (blockFace == BlockFace.WEST) {
            targetLocation.setX(targetLocation.getX() + 0.5);
        }

        if (blockFace == BlockFace.EAST) {
            targetLocation.setX(targetLocation.getX() - 0.5);
        }

        if (targetLocation.getBlock().getType() != Material.AIR) {
            return;
        }

        targetLocation.setYaw(damager.getLocation().getYaw());
        targetLocation.setPitch(damager.getLocation().getPitch());

        damager.teleport(targetLocation);

        EnderpearlCooldownHandler.resetEnderpearlTimer(damager);
    }

    private BlockFace getDirection(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH;
        } else if (yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw < 225) {
            return BlockFace.NORTH;
        } else if (yaw < 315) {
            return BlockFace.EAST;
        }
        return BlockFace.NORTH;
    }

}
