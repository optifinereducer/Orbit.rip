package net.frozenorb.foxtrot.commands;

import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class FoxtrotGiveCommand {

    @Command(names = {"fxgive"}, permission = "op")
    public static void fxgive(CommandSender sender,
                              @Param(name = "player") Player target,
                              @Param(name = "item") ItemStack item,
                              @Param(name = "amount") int amount,
                              @Param(name = "name") String name,
                              @Param(name = "lore") String lore,
                              @Param(name = "enchants") String enchants) {
        ItemBuilder builder = ItemBuilder.copyOf(item)
                .amount(amount);

        name = !name.isEmpty() && name.contains(";") && name.split(";").length == 0 ? null : name.replace("%s", " ");
        if (name != null) {
            builder.name(name);
        }

        String[] loreSplit = lore.contains(";") ? lore.split(";") : new String[]{lore};
        if (loreSplit.length > 0) {
            builder.addToLore(loreSplit);
        }

        String[] enchantsSplit = enchants.split(";");
        for (String s : enchantsSplit) {
            String[] parts = s.split(":");
            String enchantment = parts[0];
            String enchantmentId = parts[1];
            Enchantment enchant = findEnchantment(enchantment);
            if (enchant != null) {
                builder.enchant(enchant, Integer.parseInt(enchantmentId));
            } else if (Foxtrot.getInstance().isCrazyEnchants()) {
                CrazyEnchantments ce = CrazyEnchantments.getInstance();
                CEnchantments customEnchant = ce.getEnchantments().stream()
                        .filter(custom -> custom.getCustomName().equalsIgnoreCase(enchantment))
                        .findFirst().orElse(null);
                if (customEnchant != null) {
                    ItemStack build = builder.build();
                    ItemStack enchantedItem = ce.addEnchantment(build, customEnchant, Integer.parseInt(enchantmentId));
                    builder = ItemBuilder.copyOf(enchantedItem);
                }
            }
        }

        ItemStack build = builder.build();
        InventoryUtils.addAmountToInventory(target.getInventory(), build, amount);
        if (sender instanceof Player)
            sender.sendMessage(CC.GREEN + "Gave " + CC.DARK_GREEN + amount + CC.GREEN + " of " + CC.DARK_GREEN
                    + item.getType().toString().toLowerCase() + CC.GREEN + " to " + CC.DARK_GREEN + target.getName() + CC.GREEN + "!");

        Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), target::updateInventory, 2L);
    }


    private static Enchantment findEnchantment(String string) {
        string = string.toLowerCase().replaceAll(" ", "_");
        // if it's a number lookup by id
        if (string.matches("\\d+")) {
            int enchantmentId = Integer.parseInt(string);
            for (Enchantment enchantment : Enchantment.values()) {
                if (enchantment.getId() == enchantmentId)
                    return enchantment;
            }
        } else {
            for (Enchantment value : Enchantment.values()) {
                String name = value.getName();
                // custom glow has null name
                if (name == null) continue;
                if (string.equalsIgnoreCase(name.toLowerCase()))
                    return value;
            }
        }

        return null;
    }
}
