package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPSetLivesCommand {

    @Command(names={ "pvptimer setlives", "timer setlives", "pvp setlives", "pvptimer setlives", "timer setlives", "pvp setlives" }, permission="foxtrot.setlives")
    public static void pvpSetLives(Player sender, @Param(name="player") UUID player, @Param(name="amount") int amount) {
        Foxtrot.getInstance().getFriendLivesMap().setLives(player, amount);
        sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + "'s friend life count to " + amount + ".");
    }

}