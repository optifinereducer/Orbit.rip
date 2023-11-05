package rip.warzone.anticheat.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import rip.warzone.anticheat.AntiCheat;

public class XRayListener
        implements Listener {
    private static final BlockFace[] FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
    private boolean coal = true;
    private boolean iron = true;
    private boolean redstone = true;
    private boolean lapis = true;
    private boolean gold = true;
    private boolean emerald = true;
    private boolean diamond = true;
    private final Map<UUID, Integer> alertsCounted = new HashMap<UUID, Integer>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (this.isValuable(event.getBlock().getType())) {
            return;
        }
        Block block = event.getBlock();
        Bukkit.getScheduler().scheduleAsyncDelayedTask(AntiCheat.instance, () -> {
            Block found = null;
            for (BlockFace face : FACES) {
                if (!this.isValuable(block.getRelative(face).getType())) continue;
                found = block.getRelative(face);
                break;
            }
            if (found == null) {
                return;
            }
            for (BlockFace face : FACES) {
                if (!(found.getRelative(face).getType() == Material.AIR ? !found.getRelative(face).equals(event.getBlock()) : found.getRelative(face).hasMetadata("found"))) continue;
                return;
            }
            String material = WordUtils.capitalize(found.getType().name().replace("_ORE", "").toLowerCase());

            this.alertsCounted.putIfAbsent(player.getUniqueId(), 0);
            this.alertsCounted.put(player.getUniqueId(), this.alertsCounted.get(player.getUniqueId()) + 1);
            FancyMessage message = this.getMessage(player, player.getName() + " might be using X-Ray (" + oreColor(found.getType()) + material +  ChatColor.GRAY + ") [" + this.alertsCounted.get(player.getUniqueId()) + "]");
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("anticheat.xray") || AntiCheat.instance.getAlertsManager().getAlertsToggled().contains(online.getUniqueId())) continue;
                message.send(online);
            }
            found.setMetadata("found", new FixedMetadataValue(AntiCheat.instance, true));
        });
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        block.setMetadata("found", new FixedMetadataValue(AntiCheat.instance, true));
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        event.getBlocks().stream().filter(block -> this.isValuable(block.getType())).forEach(block -> block.setMetadata("found", new FixedMetadataValue(AntiCheat.instance, true)));
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }
        Block block = event.getRetractLocation().getBlock();
        if (this.isValuable(block.getType())) {
            block.setMetadata("found", new FixedMetadataValue(AntiCheat.instance, true));
            block.getRelative(event.getDirection()).setMetadata("found", new FixedMetadataValue(AntiCheat.instance, true));
        }
    }

    private FancyMessage getMessage(Player hacker, String simpleMessage) {
        String name = hacker.getName();
        String displayName = hacker.getDisplayName();
        String action = "teleport to";
        String commands = " [/tp <p>]";
        return new FancyMessage("[").color(ChatColor.RED).then("\u26a0").color(ChatColor.YELLOW).style(ChatColor.BOLD).then("] ").color(ChatColor.RED).then(displayName).tooltip(ChatColor.YELLOW + "Click to " + action + " " + ChatColor.RESET + name + ChatColor.YELLOW + ".").command("/* " + name + " " + commands).then("" + simpleMessage.replace(name, "")).color(ChatColor.GRAY).tooltip(ChatColor.YELLOW + "Click to " + action + " " + ChatColor.RESET + name + ChatColor.YELLOW + ".").command("/* " + name + " " + commands);
    }

    private boolean isValuable(Material material) {
        switch (material) {
            case COAL_ORE: {
                return this.coal;
            }
            case IRON_ORE: {
                return this.iron;
            }
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE: {
                return this.redstone;
            }
            case LAPIS_ORE: {
                return this.lapis;
            }
            case GOLD_ORE: {
                return this.gold;
            }
            case EMERALD_ORE: {
                return this.emerald;
            }
            case DIAMOND_ORE: {
                return this.diamond;
            }
        }
        return false;
    }

    private ChatColor oreColor(Material material) {
        switch (material) {
            case COAL_ORE: {
                return ChatColor.DARK_GRAY;
            }
            case IRON_ORE: {
                return ChatColor.WHITE;
            }
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE: {
                return ChatColor.RED;
            }
            case LAPIS_ORE: {
                return ChatColor.BLUE;
            }
            case GOLD_ORE: {
                return ChatColor.YELLOW;
            }
            case EMERALD_ORE: {
                return ChatColor.GREEN;
            }
            case DIAMOND_ORE: {
                return ChatColor.AQUA;
            }
        }
        return ChatColor.GRAY;
    }
}