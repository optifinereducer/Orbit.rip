package net.frozenorb.foxtrot.map.bounty.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.bounty.Bounty;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;

public class BountyCommand {

    @Command(names = {"bounty", "setbounty", "addbounty"}, permission = "")
    public static void bounty(Player sender, @Param(name = "target") Player target, @Param(name = "amount") int amount) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }

        if (Foxtrot.getInstance().getBountyCooldownMap().isOnCooldown(sender.getUniqueId())) {
            long millisLeft = Foxtrot.getInstance().getBountyCooldownMap().getCooldown(sender.getUniqueId()) - System.currentTimeMillis();
            sender.sendMessage(ChatColor.GOLD + "Bounty cooldown: " + ChatColor.WHITE + TimeUtils.formatIntoDetailedString((int) millisLeft / 1000));
            return;
        }

        if (amount < 5) {
            sender.sendMessage(CC.RED + "Your bounty must be at least 5 gems!");
            return;
        }

        if (sender == target) {
            sender.sendMessage(CC.RED + "You cannot put a bounty on yourself!");
            return;
        }

        Bounty bounty = Foxtrot.getInstance().getMapHandler().getBountyManager().getBounty(target);

        if (bounty != null && bounty.getGems() >= amount) {
            sender.sendMessage(CC.RED + "Your bounty must be higher than the current bounty of " + bounty.getGems() + " gems!");
            return;
        }

        if (!Foxtrot.getInstance().getGemMap().removeGems(sender.getUniqueId(), amount)) {
            sender.sendMessage(CC.RED + "You do not have enough gems for this!");
            return;
        }

        if (bounty != null) {
            Foxtrot.getInstance().getGemMap().addGems(bounty.getPlacedBy(), bounty.getGems(), true);
        }

        Foxtrot.getInstance().getMapHandler().getBountyManager().placeBounty(sender, target, amount);

        Bukkit.broadcastMessage(CC.GRAY + "[" + CC.GOLD + "Bounty" + CC.GRAY + "] " + sender.getDisplayName() + CC.YELLOW + " placed a bounty on "
                + target.getDisplayName() + CC.YELLOW + " of " + CC.GREEN + amount + " gems" + CC.YELLOW + "!");

        FrozenNametagHandler.reloadPlayer(target);

        Foxtrot.getInstance().getBountyCooldownMap().applyCooldown(sender.getUniqueId(), 30);
    }

    @Command(names = {"bounty list", "bountylist"}, permission = "")
    public static void bountyList(Player sender) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }

        Foxtrot.getInstance().getMapHandler().getBountyManager().getBountyMap()
                .entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEach((entry) ->
                        sender.sendMessage(ChatColor.DARK_GREEN + FrozenUUIDCache.name(entry.getKey()) + ": "
                                + ChatColor.GREEN + entry.getValue().getGems() + " gems"));
    }
}
