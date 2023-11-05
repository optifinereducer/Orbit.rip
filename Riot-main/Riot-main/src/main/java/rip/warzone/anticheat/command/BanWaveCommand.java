package rip.warzone.anticheat.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.banwave.BanWave;
import rip.warzone.anticheat.banwave.BanWaveManager;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.CC;
import rip.warzone.sprite.profile.Profile;
import rip.warzone.sprite.rank.Rank;
import rip.warzone.sprite.utils.SpriteAPI;

import java.util.ArrayList;
import java.util.UUID;

public class BanWaveCommand implements Listener {

    //This command runs the banwave when players are added to the banwave listeners
    @Command(names={"ac banwave start", "banwave start"}, permission="op")
    public static void execute(Player sender) {
        sender.sendMessage(ChatColor.GREEN + "Starting Ban wave...");

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f\u2588\u2588\u2588\u2588&c\u2588&f\u2588\u2588\u2588\u2588"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f\u2588\u2588\u2588&c\u2588&6\u2588&c\u2588&f\u2588\u2588\u2588" + " &7&m------- " + "&c&lBAN WAVE STARTED" + " &7&m-------"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588" + " &cThere are a total of " + "&41"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f\u2588&c\u2588&6\u2588\u2588&0\u2588&6\u2588\u2588&c\u2588&f\u2588" + " &cplayers in the ban wave!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f\u2588&c\u2588&6\u2588\u2588\u2588\u2588\u2588&c\u2588&f\u2588"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588\u2588&0\u2588&6\u2588\u2588\u2588&c\u2588" + " &7&m------- " + "&c&lBAN WAVE STARTED" + " &7&m-------"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588"));
        BanWave.runBanWave();
    }

    private static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    //This command is to add players to the banwave whilst they're possibly cheating
    @Command(names={"ac banwave add", "banwave add", "wave add"}, permission="anticheat.banwave")
    public static void onAdd(Player sender, @Param(name="player") Player target) {
        BanWaveManager waveManager= AntiCheat.instance.getBanWaveManager();
        if (waveManager.getPlayersToBan().contains(target.getUniqueId()))
            return;

        waveManager.addToBan(target.getUniqueId());
        sender.sendMessage((color(ChatColor.RED + "[" + ChatColor.YELLOW + ChatColor.BOLD + "⚠" + ChatColor.RED + "] " + ChatColor.GRAY + "[" + Bukkit.getServerName() + "] " + ChatColor.YELLOW + target.getDisplayName() + " &chas been added to the ban queue and will be banned!")));
    }

    //This command is to remove players that are not cheating / were falsely added to the judgement day
    @Command(names={"ac banwave remove", "banwave remove", "wave remove"}, permission="op")
    public static void onRemove(Player sender, @Param(name="player") Player target) {
        BanWaveManager waveManager=AntiCheat.instance.getBanWaveManager();

        waveManager.removeFromBan(target.getUniqueId());
        sender.sendMessage((color(ChatColor.RED + "[" + ChatColor.YELLOW + ChatColor.BOLD + "⚠" + ChatColor.RED + "] " + ChatColor.GRAY + "[" + Bukkit.getServerName() + "] " + ChatColor.YELLOW + target.getDisplayName() + " &chas been removed to the ban queue and will NOT be banned!")));
    }

    //This command opens a GUI, displaying the time they were added, the total amount of logs they've set off, and the PING/Sens of the player
    @Command(names={"ac banwave list", "banwave list"}, permission="anticheat.banwave")
    public static void banwaveList(Player sender) {
        BanWaveManager waveManager = AntiCheat.instance.getBanWaveManager();

        //CBA WITH FUCKING GUIS

        Inventory inventory = Bukkit.createInventory(null, 54, "Riot | Banwave List");

        waveManager.getPlayersToBan().stream().forEach(uuid -> {
            OfflinePlayer user = Bukkit.getOfflinePlayer(uuid);
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            Profile profile = SpriteAPI.INSTANCE.getProfile(uuid);
            PlayerData data = AntiCheat.instance.getPlayerDataManager().getPlayerData(user.getUniqueId());
            meta.setDisplayName(SpriteAPI.INSTANCE.formatName(uuid));
            ArrayList<String> lores = new ArrayList<>();
            lores.add("");
            Rank rank = SpriteAPI.INSTANCE.getHighestRank(uuid,Bukkit.getServerName());
            lores.add(ChatColor.GRAY + "Rank: " + rank.getColor() + rank.getDisplayName());
            lores.add(ChatColor.GRAY + "Violation Level: " + ChatColor.RED + data.violations);
            lores.add(ChatColor.GRAY + "Added At: " + ChatColor.RED + "" + TimeUtils.formatIntoMMSS((int) ((System.currentTimeMillis() - data.getAddedToBanwave()) / 1000L)));
            lores.add(ChatColor.GRAY + "Ping: " + ChatColor.RED + data.getPing() + "ms");
            lores.add(ChatColor.GRAY + "Mouse Sensitivity: " + ChatColor.RED + Math.round(data.getSensitivity() * 200) + "%");
            lores.add("");
            meta.setLore(lores);
            item.setItemMeta(meta);
            inventory.addItem(item);
        });
        sender.openInventory(inventory);
    }
}
