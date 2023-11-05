package net.frozenorb.foxtrot.shop.blockshop.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.shop.blockshop.BlockShopHandler;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by vape on 10/30/2020 at 3:40 PM.
 */
public class BlockShopAdminCommands {

    private static final BlockShopHandler HANDLER = Foxtrot.getInstance().getMapHandler().getBlockShopHandler();

    @Command(names = {"blockshop disable", "bs disable"}, permission = "foxtrot.admin")
    public static void blockShopDisable(CommandSender sender) {
        HANDLER.setEnabled(!HANDLER.isEnabled());
        sender.sendMessage(ChatColor.YELLOW + "Enabled: " + HANDLER.isEnabled());
    }
}