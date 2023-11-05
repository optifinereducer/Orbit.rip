package net.frozenorb.foxtrot.team.menu.button;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.dimension.AbstractDimension;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DimensionTeleportButton extends Button {

    private final AbstractDimension dimension;

    @Override
    public String getName(Player player) {
        return ChatColor.WHITE + dimension.getDimensionName() + " Dimension";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();
        description.add(ChatColor.GRAY + "Click to teleport to the " + dimension.getDimensionName() + " Dimension" + ChatColor.GRAY + "!");
        if (dimension.getPoints() > 0) {
            description.add(ChatColor.GRAY + "You must have at least " + dimension.getPoints() + " points to enter this dimension.");
        }
        if (dimension.requiresPowerFaction()) {
            description.add(ChatColor.GRAY + "You must be in a power faction to enter this dimension.");
        }
        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return dimension.getIcon();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        if (team == null) return;
        if (dimension.requiresPowerFaction() && !team.isPowerFaction()) {
            player.sendMessage(ChatColor.RED + "Your team must be a power faction to enter this dimension!");
        } else {
            player.closeInventory();
            player.teleport(Bukkit.getWorld(dimension.getWorldName()).getSpawnLocation());
        }
    }
}
