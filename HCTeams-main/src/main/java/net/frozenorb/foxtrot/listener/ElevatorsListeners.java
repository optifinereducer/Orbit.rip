package net.frozenorb.foxtrot.listener;

import net.minecraft.server.v1_7_R4.Vec3D;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ElevatorsListeners implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onSignCreate(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[Elevator]")) {
            event.setLine(0, ChatColor.DARK_RED + "[Elevator]");
            event.setLine(1, "Up");
            event.setLine(2, "");
            event.setLine(3, "");
        }
    }

    @EventHandler
    public void onSignUse(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN)) {
            Sign sign = (Sign)event.getClickedBlock().getState();
            if (!sign.getLine(0).equals(ChatColor.DARK_RED + "[Elevator]") || (!sign.getLine(1).equals("Up") && !sign.getLine(1).equals("Down"))) {
                return;
            }

            if (!canSee(event.getPlayer(), sign.getBlock().getLocation())) {
                return;
            }

            boolean up = sign.getLine(1).equals("Up");

            if (!up) {
                return;
            }

            int x = event.getClickedBlock().getX();
            int z = event.getClickedBlock().getZ();
            World world = event.getClickedBlock().getWorld();
            boolean foundFirst = false;
            Location location = null;
            for (int y = event.getClickedBlock().getY() + (up ? 1 : -1); y < world.getMaxHeight() && y > 0; y += (up ? 1 : -1)) {
                Block block = world.getBlockAt(x, y, z);
                if (block != null && !canGoThrough(block.getType()))
                    if (!up && !foundFirst) {
                        foundFirst = true;
                    }
                    else {
                        Block up1 = world.getBlockAt(x, y + 1, z);
                        Block up2 = world.getBlockAt(x, y + 2, z);

                        if (up1 != null && up2 != null && canGoThrough(up1.getType()) && canGoThrough(up2.getType())) {
                            location = new Location(event.getClickedBlock().getWorld(), x, (y + 1), z);
                            break;
                        }
                    }
            }

            if (location == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "The elevator isn't setup properly!");
            } else {
                location.setYaw(event.getPlayer().getLocation().getYaw());
                location.setPitch(event.getPlayer().getLocation().getPitch());
                location.setX(location.getX() + 0.5D);
                location.setZ(location.getZ() + 0.5D);
                event.getPlayer().teleport(location);
            }
        }
    }

    public boolean canGoThrough(Material material) {
        if (material.isTransparent()) {
            return true;
        }

        switch (material.ordinal()) {
            case 64:
            case 69:
            case 240:
                return true;
        }
        return false;
    }

    public boolean canSee(Player player, Location loc2) {
        Location loc1 = player.getLocation();
        return (((CraftWorld)loc1.getWorld()).getHandle().a(Vec3D.a(loc1.getX(), loc1.getY() + player.getEyeHeight(), loc1.getZ()), Vec3D.a(loc2.getX(), loc2.getY(), loc2.getZ())) == null);
    }

}
