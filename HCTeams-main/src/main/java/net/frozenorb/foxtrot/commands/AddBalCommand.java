package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddBalCommand {

    @Command(names={ "addBal" }, permission="foxtrot.addbal")
    public static void addBal(CommandSender sender, @Param(name="player") UUID player, @Param(name="amount") float amount) {
        if (amount > 10000 && sender instanceof Player && !sender.isOp()) {
            sender.sendMessage("§cYou cannot add a balance this high. This action has been logged.");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage("§cWhy are you trying to do that?");
            return;
        }


        if (amount > 250000 && sender instanceof Player) {
            sender.sendMessage("§cYou cannot set a balance this high. This action has been logged.");
            return;
        }

        Player targetPlayer = Foxtrot.getInstance().getServer().getPlayer(player);
        FrozenEconomyHandler.setBalance(player, FrozenEconomyHandler.getBalance(player) + amount);

        if (sender != targetPlayer) {
            sender.sendMessage("§6Balance for §e" + player + "§6 set to §e$" + FrozenEconomyHandler.getBalance(player));
        }

        if (sender instanceof Player && (targetPlayer != null)) {
            String targetDisplayName = ((Player) sender).getDisplayName();
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + FrozenEconomyHandler.getBalance(player) + "§a by §6" + targetDisplayName);
        } else if (targetPlayer != null) {
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + FrozenEconomyHandler.getBalance(player) + "§a by §4CONSOLE§a.");
        }

        Foxtrot.getInstance().getWrappedBalanceMap().setBalance(player, FrozenEconomyHandler.getBalance(player));
    }

}