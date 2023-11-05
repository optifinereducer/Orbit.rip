package net.frozenorb.foxtrot.partner.impl.purge;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ExplosivePurgePackage extends PartnerPackage {

    private final List<Location> trappedChests = new ArrayList<>();

    public ExplosivePurgePackage() {
        super("Explosive");
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getType() != Material.CHEST) {
            return;
        }
        if (!isPartnerItem(event.getItemInHand())) {
            return;
        }
        if (!CustomTimerCreateCommand.isPurgeTimer()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "This can only be used during the purge.");
            return;
        }

        trappedChests.add(event.getBlock().getLocation());
        sendActivationMessages(
                player,
                new String[]{
                        "You have activated " + getName() + CC.GREEN + "!",
                }, null, null
        );
    }

    @EventHandler
    private void onChestInteract(PlayerInteractEvent event) {
        if (!CustomTimerCreateCommand.isPurgeTimer()) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block != null && block.getState() instanceof Chest && trappedChests.contains(block.getLocation())) {
            Team team = LandBoard.getInstance().getTeam(block.getLocation());
            if (team != null && !team.isMember(player.getUniqueId())) {
                event.getPlayer().getWorld().createExplosion(block.getLocation(), 6);
                trappedChests.remove(block.getLocation());
                block.setType(Material.AIR);
                player.sendMessage(ChatColor.RED + "You opened a trapped chest!");
            }
        }
    }


    @Override
    public long getCooldownTime() {
        return 0;
    }

    @Override
    protected ItemStack partnerItem() {
        return ItemBuilder.of(Material.CHEST)
                .name(getName())
                .addToLore(
                        "&7A chest that gives an instant TNT explosion when ",
                        "&7opened by an enemy faction during the Purge."
                ).build();
    }

    @Override
    public String getName() {
        return "§c§lExplosive Chest";
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
