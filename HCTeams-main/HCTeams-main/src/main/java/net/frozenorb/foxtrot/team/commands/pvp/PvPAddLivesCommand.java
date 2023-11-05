package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPAddLivesCommand {

    @Command(names = {"pvp addlives", "addlives"}, permission = "foxtrot.addlives")
    public static void pvpSetLives(CommandSender sender, @Param(name = "player") UUID player, @Param(name = "amount") int amount) {
        Foxtrot.getInstance().getFriendLivesMap().setLives(player, Foxtrot.getInstance().getFriendLivesMap().getLives(player) + amount);
        sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " " + amount + " lives.");

        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            String suffix = sender instanceof Player ? " from " + sender.getName() : "";
            bukkitPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " lives" + suffix);
        }
    }

}
