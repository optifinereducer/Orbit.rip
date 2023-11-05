package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.Players;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class KitMapListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();

        // 1. killer should not be null
        // 2. victim should not be equal to killer
        // 3. victim should not be naked
        Player killer = victim.getKiller();
        if (killer != null && !victim.getUniqueId().equals(killer.getUniqueId()) && !Players.isNaked(victim)) {

            if (Foxtrot.getInstance().getMapHandler().getScoreboardTitle().contains("cane")) {
                return;
            }
            String killerName = killer.getName();
            FrozenEconomyHandler.deposit(killer.getUniqueId(), 100 + getAdditional(killer));
            killer.sendMessage(ChatColor.RED + "You received a reward for killing " + ChatColor.GREEN + victim.getName() + ChatColor.RED + ".");

//            Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
//                int kills = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(victim.getKiller()).getKills();
//                if (kills % 10 == 0) {
//                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + killerName + " OP 1");
//                    victim.getKiller().sendMessage(ChatColor.GREEN + "You received an OP key for 10 kills!");
//                }
//            });
        }
    }

    private int getAdditional(Player killer) {
        if (killer.hasPermission("hcteams.killreward.ghoul")) {
            return 5;
        } else if (killer.hasPermission("hcteams.killreward.poltergeist")) {
            return 5;
        } else if (killer.hasPermission("hcteams.killreward.sorcerer")) {
            return 10;
        } else if (killer.hasPermission("hcteams.killreward.suprive")) {
            return 25;
        } else if (killer.hasPermission("hcteams.killreward.juggernaut")) {
            return 50;
        } else if (killer.hasPermission("hcteams.killreward.myth")) {
            return 75;
        } else if (killer.hasPermission("hcteams.killreward.sapphire")) {
            return 100;
        } else if (killer.hasPermission("hcteams.killreward.pearl")) {
            return 125;
        } else if (killer.hasPermission("hcteams.killreward.ruby")) {
            return 150;
        } else if (killer.hasPermission("hcteams.killreward.velt")) {
            return 175;
        } else if (killer.hasPermission("hcteams.killreward.velt-plus")) {
            return 200;
        } else {
            return 0;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), event.getEntity()::remove, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());
        if (team != null && event.getEntity() instanceof Arrow && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        if (event.getCause() != TeleportCause.NETHER_PORTAL) {
            return;
        }

        if (event.getTo().getWorld().getEnvironment() != Environment.NETHER) {
            return;
        }

        event.setTo(event.getTo().getWorld().getSpawnLocation().clone());
    }

}
