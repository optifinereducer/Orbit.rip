package rip.warzone.anticheat.banwave;

import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.banwave.checking.PlayerChecker;
import rip.warzone.anticheat.banwave.checking.ResultTypes;
import rip.warzone.anticheat.player.PlayerData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class BanWaveManager {

    private final ArrayList<UUID> playersToBan;
    public boolean runningBanwave=false;
    public int counter=0;

    public BanWaveManager() {
        playersToBan=new ArrayList<>();
    }

    public void addToBan(UUID uuid) {
        if (!playersToBan.contains(uuid)) {
            playersToBan.add(uuid);
            PlayerData data= AntiCheat.instance.getPlayerDataManager().getPlayerData(Bukkit.getPlayer(uuid));
            data.setAddedToBanwave(System.currentTimeMillis());
        }
    }

    public void addToBanWithChecking(UUID uuid) {
        Player player=Bukkit.getPlayer(uuid);
        PlayerData playerData=AntiCheat.instance.getPlayerDataManager().getPlayerData(player);
        String tpTooltip;
        String tpCommand;

        if (Bukkit.getServer().getPluginManager().getPlugin("Practice") != null) {
            tpTooltip=ChatColor.WHITE + "Click to silently spectate " + ChatColor.RESET + player.getDisplayName() + ChatColor.WHITE + ".";
            tpCommand="/silentfollow " + player.getName();
        } else {
            tpTooltip=ChatColor.WHITE + "Click to teleport to " + ChatColor.RESET + player.getDisplayName() + ChatColor.WHITE + ".";
            tpCommand="/tp " + player.getName();
        }
        FancyMessage banwaveMessage=new FancyMessage("")
                .then(player.getDisplayName())
                .tooltip(tpTooltip)
                .command(tpCommand)
                .color(ChatColor.GRAY)
                .then(" [" + playerData.getPing() + "ms] ")
                .color(ChatColor.GRAY)
                .then("[" + playerData.getClient().getName() + "]")
                .color(ChatColor.RED)
                .then(" has been added to the banwave.");

        if (PlayerChecker.checkPlayer(player) == ResultTypes.FAILED) {
            if (!AntiCheat.instance.getBanWaveManager().getPlayersToBan().contains(uuid)) {
                AntiCheat.instance.getBanWaveManager().addToBan(player.getUniqueId());
                AntiCheat.instance.getAlertsManager().getAlertsToggled().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> {
                    if (p.hasPermission("anticheat.alerts")) {
                        banwaveMessage.send(p);
                    }
                });
            }
        }
    }

    public void removeFromBan(UUID uuid) {
        playersToBan.remove(uuid);
    }

    public ArrayList<UUID> getPlayersToBan() {
        return playersToBan;
    }
}

