package net.frozenorb.foxtrot.shop.blockshop.menu;

import lombok.RequiredArgsConstructor;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.ItemBuilder;
import net.frozenorb.qlib.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ItemButton extends Button {

    private final ItemStack item;
    private final double buyPrice;

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        double balance = FrozenEconomyHandler.getBalance(player.getUniqueId());

        if (buyPrice < 0) {
            player.sendMessage(ChatColor.RED + "This item is not purchasable!");
            return;
        }

        if (balance < buyPrice) {
            player.sendMessage(ChatColor.RED + "You do not have enough money to purchase this!");
            return;
        }

        FrozenEconomyHandler.setBalance(player.getUniqueId(), balance - buyPrice);
        player.getInventory().addItem(item.clone());

        player.sendMessage(ChatColor.GREEN + "You have purchased " + ItemUtils.getName(item) + ChatColor.GREEN + " for $" + buyPrice + ".");
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return ItemBuilder
                .copyOf(item.clone())
                .setLore(getDescription(player))
                .build();
    }

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();

        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            lore.addAll(item.getItemMeta().getLore().stream().map(s -> ChatColor.WHITE + s).collect(Collectors.toList()));
            lore.add("");
        }

        lore.add(ChatColor.YELLOW + "Your balance: " + ChatColor.GOLD + "$" + FrozenEconomyHandler.getBalance(player.getUniqueId()));
        if (buyPrice >= 0)
            lore.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "LEFT CLICK" + ChatColor.GREEN + " to purchase for " + ChatColor.BOLD + "$" + buyPrice);
        else
            lore.add(ChatColor.GRAY + "You are not able to purchase this item.");

        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }
}