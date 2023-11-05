package net.frozenorb.foxtrot.team.button.claimbuildbuttons;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.BlockRunnable;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class NetherRackButton extends Button {
    public void clicked(final Player player, final int i, final ClickType clickType) {
        if (!player.hasPermission("")) {
            player.sendMessage(ChatColor.RED + "No Permission!");
            return;
        }
        final Team faction = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());
        final List<Claim> claims = faction.getClaims();
        if (claims.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Your faction has no claims!");
            player.closeInventory();
            return;
        }
        final Claim claim = claims.stream().findFirst().orElse(null);
        final Location min = claim.getMinimumPoint();
        final Location max = claim.getMaximumPoint();
        final Location center = claim.getCenter();
        final int minY = center.getWorld().getHighestBlockAt(center).getY();
        new BlockRunnable(BlockRunnable.BlockOperation.X, Material.NETHERRACK, minY, min, max, 50).runTaskTimer((Plugin) Foxtrot.getInstance(), 0L, 1L);
        new BlockRunnable(BlockRunnable.BlockOperation.Z, Material.NETHERRACK, minY, min, max, 50).runTaskTimer((Plugin) Foxtrot.getInstance(), 0L, 1L);
    }

    public String getName(final Player player) {
        return ChatColor.RED + "Generate NetherRack Box";
    }

    public List<String> getDescription(final Player player) {
        return new ArrayList<String>();
    }

    public byte getDamageValue(final Player player) {
        return 0;
    }

    public Material getMaterial(final Player player) {
        return Material.NETHERRACK;
    }
}
