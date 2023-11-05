package rip.warzone.anticheat.listener;

import mkremins.fanciful.FancyMessage;
import net.minecraft.server.v1_7_R4.PacketDataSerializer;
import net.minecraft.server.v1_7_R4.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.util.io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.banwave.checking.PlayerChecker;
import rip.warzone.anticheat.banwave.checking.ResultTypes;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.event.PlayerAlertEvent;
import rip.warzone.anticheat.event.PlayerBanEvent;
import rip.warzone.anticheat.log.Log;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.sprite.utils.CC;
import rip.warzone.sprite.utils.SpriteAPI;

import java.util.*;

public class PlayerListener implements Listener {

    private final Map<UUID, Long> lastFire = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        AntiCheat.instance.getPlayerDataManager().addPlayerData(event.getPlayer());

        AntiCheat.instance.getServer().getScheduler().runTaskLaterAsynchronously(AntiCheat.instance, () -> {
            PlayerConnection playerConnection = ((CraftPlayer) event.getPlayer()).getHandle().playerConnection;

            playerConnection.sendPacket(new PacketPlayOutCustomPayload(
                    "REGISTER",
                    new PacketDataSerializer(Unpooled.wrappedBuffer("CB-Client".getBytes()))
            ));

            playerConnection.sendPacket(new PacketPlayOutCustomPayload(
                    "REGISTER",
                    new PacketDataSerializer(Unpooled.wrappedBuffer("Lunar-Client".getBytes()))
            ));

            playerConnection.sendPacket(new PacketPlayOutCustomPayload(
                    "REGISTER",
                    new PacketDataSerializer(Unpooled.wrappedBuffer("FML|HS".getBytes()))
            ));

            playerConnection.sendPacket(new PacketPlayOutCustomPayload(
                    "REGISTER",
                    new PacketDataSerializer(Unpooled.wrappedBuffer("CC".getBytes()))
            ));
        }, 10L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (AntiCheat.instance.getAlertsManager().hasAlertsToggled(event.getPlayer())) {
            AntiCheat.instance.getAlertsManager().toggleAlerts(event.getPlayer());
        }

        AntiCheat.instance.getPlayerDataManager().removePlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = AntiCheat.instance.getPlayerDataManager().getPlayerData(player);

        if (playerData != null) {
            playerData.setInventoryOpen(false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileLaunch(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player shooter = (Player) event.getEntity();

        Long lastFired = this.lastFire.get(shooter.getUniqueId());

        if (lastFired != null && System.currentTimeMillis() - lastFired < 500L) {
            event.setCancelled(true);
            this.lastFire.put(shooter.getUniqueId(), System.currentTimeMillis());
            return;
        }

        this.lastFire.put(shooter.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerAlert(PlayerAlertEvent event) {
        if (!AntiCheat.instance.isAntiCheatEnabled()) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        PlayerData playerData = AntiCheat.instance.getPlayerDataManager().getPlayerData(player);

        if (playerData == null) {
            return;
        }

        // display a different tooltip and execute a different command on potpvp
        String tpTooltip;
        String tpCommand;

        if (Bukkit.getServer().getPluginManager().getPlugin("Practice") != null) {
            tpTooltip = ChatColor.WHITE + "Click to silently spectate " + ChatColor.RESET + player.getDisplayName() + ChatColor.WHITE + ".";
            tpCommand = "/silentfollow " + player.getName();
        } else {
            tpTooltip = ChatColor.WHITE + "Click to teleport to " + ChatColor.RESET + player.getDisplayName() + ChatColor.WHITE + ".";
            tpCommand = "/tp " + player.getName();
        }

        /*ResultTypes types= PlayerChecker.checkPlayer(player);
        String stage=ChatColor.RED + " (unsure)";
        if (types == ResultTypes.PASS) {
            stage=ChatColor.RED + " (unsure)";
        }
        if (types == ResultTypes.UNSURE) {
            stage=ChatColor.YELLOW + " (Possibly)";
        }
        if (types == ResultTypes.FAILED) {
            stage=ChatColor.GREEN + " (Cheating)";
        }*/

        if(!event.getCheckName().contains("Banned for") && !event.getCheckName().contains("Manually banned by")) {
            List<String> data = new ArrayList<>();

            Arrays.stream(event.getData()).forEach(data1 -> data.add(data1.getName() + " " + data1.getValue()));

            FancyMessage alertMessage = new FancyMessage(CC.translate("&c[&e⚠&c] &r" + SpriteAPI.INSTANCE.formatName(player.getUniqueId()) + "&r &7failed &c" + event.getCheckName() + "&r&7. " + String.join(" ", data))).tooltip(CC.translate("&7Type: &c" + event.getAlertType().name() + "\n&cClick to teleport")).command("/tp " + event.getPlayer().getName());
            FancyMessage banwaveMessage = new FancyMessage(CC.translate("&c[&e⚠&c] &r" + SpriteAPI.INSTANCE.formatName(player.getUniqueId()) + "&r &7was added to the banwave."));
            playerData.violations++;

            if (PlayerChecker.checkPlayer(event.getPlayer()) == ResultTypes.FAILED) {
                if (!AntiCheat.instance.getBanWaveManager().getPlayersToBan().contains(event.getPlayer().getUniqueId())) {
                    AntiCheat.instance.getBanWaveManager().addToBan(event.getPlayer().getUniqueId());
                    AntiCheat.instance.getAlertsManager().getAlertsToggled().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> {
                        if (p.hasPermission("anticheat.alerts")) {
                            banwaveMessage.send(p);
                        }
                    });
                }
            }

            FancyMessage basicAlerts = new FancyMessage(CC.translate("&c[&e⚠&c] &r" + SpriteAPI.INSTANCE.formatName(player.getUniqueId()) + "&r &7failed &c" + event.getCheckName() + "&r&7.")).tooltip("&cClick to teleport").command("/tp " + event.getPlayer().getName());
            AntiCheat.instance.getAlertsManager().getAlertsToggled().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> {
                if (p.hasPermission("anticheat.alerts")) {
                    return;
                }
                if (p.hasPermission("anticheat.alerts.basic")) {
                    basicAlerts.send(p);
                }
            });

            if (System.currentTimeMillis() - playerData.getLastFlag() > 75) {
                AntiCheat.instance.getAlertsManager().getAlertsToggled().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> {
                    if (AntiCheat.instance.getPlayerDataManager().getPlayerData(p).staffalerts && p.hasPermission("anticheat.alerts")) {
                        alertMessage.send(p);
                    }
                });
            }
        }

        AntiCheat.instance.getLogManager().getLogQueue().add(new Log(event.getPlayer().getUniqueId(), event.getCheckName() + " " + event.concatData(), Bukkit.spigot().getTPS()[0]));

        playerData.setLastFlag(System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerBan(PlayerBanEvent event) {
        if (!AntiCheat.instance.isAntiCheatEnabled()) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        //Removes the player from banwave if they was autobanned.
        if (AntiCheat.instance.getBanWaveManager().getPlayersToBan().contains(event.getPlayer().getUniqueId())) {
            AntiCheat.instance.getBanWaveManager().removeFromBan(event.getPlayer().getUniqueId());
        }
        AntiCheat.instance.getServer().getScheduler().runTask(AntiCheat.instance, () -> {
            AntiCheat.instance.getServer().dispatchCommand(AntiCheat.instance.getServer().getConsoleSender(), "ban " + player.getName() + " [AC] Unfair Advantage");
            Bukkit.broadcastMessage("§c§m----------------------------------------------");
            Bukkit.broadcastMessage("§cRiot removed §e" + event.getPlayer().getName() + " §cfrom the network ");
            Bukkit.broadcastMessage("§cReason: §eUnfair Advantage");
            Bukkit.broadcastMessage("§c§m----------------------------------------------");

            PlayerAlertEvent alertEvent = new PlayerAlertEvent(AlertType.RELEASE, player, (!event.getReason().contains("Manual") ? "Banned for " : "") + event.getReason());
            AntiCheat.instance.getServer().getPluginManager().callEvent(alertEvent);
        });
    }

    public String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
