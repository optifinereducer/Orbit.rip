package net.frozenorb.foxtrot.server.deathban;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.LastInvCommand;
import net.frozenorb.foxtrot.persist.maps.DeathbanMap;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathbanListener implements Listener {

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        LastInvCommand.recordInventory(event.getEntity());

        EnderpearlCooldownHandler.clearEnderpearlTimer(event.getEntity());

        if (!Foxtrot.getInstance().getServerHandler().isEOTW() && Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        if (Foxtrot.getInstance().getInDuelPredicate().test(event.getEntity())) {
            return;
        }

        int seconds = (int) Foxtrot.getInstance().getServerHandler().getDeathban(event.getEntity());
        Foxtrot.getInstance().getDeathbanMap().deathban(event.getEntity().getUniqueId(), seconds);

        final String time = TimeUtils.formatIntoDetailedString(seconds);

        new BukkitRunnable() {
            public void run() {
                if (!event.getEntity().isOnline()) {
                    return;
                }

                if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
                    event.getEntity().kickPlayer(ChatColor.YELLOW + "Come back tomorrow for SOTW!");
                } else {
                    event.getEntity().kickPlayer(ChatColor.YELLOW + "Come back in " + time + "!");
                }
            }
        }.runTaskLater(Foxtrot.getInstance(), 10 * 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean shouldBypass = event.getPlayer().isOp();

        if (!shouldBypass) {
            shouldBypass = event.getPlayer().hasPermission("hcteams.staff");
        }

        if (shouldBypass) {
            Foxtrot.getInstance().getDeathbanMap().revive(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        DeathbanMap deathbanMap = Foxtrot.getInstance().getDeathbanMap();
        if (deathbanMap.isDeathbanned(event.getUniqueId())) {
            if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Come back tomorrow for SOTW!");
            } else {
                int friendLives = Foxtrot.getInstance().getFriendLivesMap().getLives(event.getUniqueId());
                if (friendLives > 0) {
                    Foxtrot.getInstance().getFriendLivesMap().setLives(event.getUniqueId(), friendLives - 1);
                    deathbanMap.revive(event.getUniqueId());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Player player = Bukkit.getPlayer(event.getUniqueId());
                            if (player != null) {
                                player.sendMessage(ChatColor.GREEN + "You have used a Friend Life to revive yourself!");
                            }
                        }
                    }.runTaskLaterAsynchronously(Foxtrot.getInstance(), 2L);
                } else {
                    long seconds = (deathbanMap.getDeathban(event.getUniqueId()) - System.currentTimeMillis()) / 1000;
                    String message = "You are currently deathbanned! Come back in " + TimeUtils.formatLongIntoDetailedString(seconds) + "!";
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + message);
                }
            }
        }
    }

}