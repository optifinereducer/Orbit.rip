package net.frozenorb.foxtrot.partner.impl.purge;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BackPackPurgePackage extends PartnerPackage {

    private static final String INVENTORY_TITLE = CC.DARK_RED + CC.BOLD + "Purge BackPack";

    public BackPackPurgePackage() {
        super("BPPackage");
    }

    private final Map<UUID, ItemStack[]> contents = new HashMap<>();

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!CustomTimerCreateCommand.isPurgeTimer()) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "This can only be used during the purge.");
            return false;
        }

        event.setCancelled(true);
        Inventory inventory = Bukkit.createInventory(null, 9, INVENTORY_TITLE);
        inventory.setContents(contents.getOrDefault(player.getUniqueId(), new ItemStack[9]));
        player.openInventory(inventory);
        return false;
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (INVENTORY_TITLE.equals(event.getInventory().getTitle())) {
            contents.put(event.getPlayer().getUniqueId(), event.getInventory().getContents());
        }
    }

    @Override
    public long getCooldownTime() {
        return 0;
    }

    @Override
    protected ItemStack partnerItem() {
        return ItemBuilder.of(Material.ENDER_CHEST)
                .name(getName())
                .addToLore(
                        "&7Personal chest during purge."
                ).build();
    }

    @Override
    public String getName() {
        return "§4§lPurge BackPack";
    }

    @Override
    public int getAmount() {
        return 2;
    }

    @Override
    public boolean isPurge() {
        return true;
    }
}
