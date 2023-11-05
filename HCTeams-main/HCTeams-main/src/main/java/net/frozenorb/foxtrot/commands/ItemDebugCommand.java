package net.frozenorb.foxtrot.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class ItemDebugCommand {

    @Command(names = {"item-debug"}, permission = "op", description = "")
    public static void execute(Player player) {
        ItemStack itemStack = player.getItemInHand();
        player.sendMessage("has meta: " + itemStack.hasItemMeta());

        if (itemStack.hasItemMeta()) {
            player.sendMessage("meta type: " + itemStack.getItemMeta().getClass().getName());
        }

        net.minecraft.server.v1_7_R4.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        player.sendMessage("has tag: " + nmsCopy.hasTag());

        if (nmsCopy.hasTag()) {
            player.sendMessage("NBT data:");
            for (String key : ((Set<String>) nmsCopy.getTag().c())) {
                player.sendMessage("- " + key + " to " + nmsCopy.getTag().get(key));
            }
        }
    }

}
