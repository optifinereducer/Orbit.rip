package net.frozenorb.foxtrot.partner.impl.purge;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.modsuite.ModUtils;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class TrackerPurgePackage extends PartnerPackage {

    public static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat("0.00");

    public TrackerPurgePackage() {
        super("TrackerPackage");
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!CustomTimerCreateCommand.isPurgeTimer()) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "This can only be used during the purge.");
            return false;
        }

        List<Entity> nearby = player.getNearbyEntities(100, 100, 100);
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        Optional<Player> optional = nearby.stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .filter(target -> target != player)
                .filter(target -> !target.hasMetadata(ModUtils.INVISIBILITY_META) || !target.hasMetadata(ModUtils.MOD_MODE_META))
                .filter(target -> {
                    if (playerTeam == null)
                        return true;

                    return !playerTeam.isMember(target.getUniqueId());
                })
                .sorted(Comparator.comparingDouble(o -> o.getLocation().distance(player.getLocation())))
                .limit(1)
                .findFirst();

        if (!optional.isPresent()) {
            player.sendMessage(CC.RED + "There are no players nearby!");
            return false;
        }

        Player target = optional.get();
        Location targetLocation = target.getLocation();

        player.sendMessage(CC.WHITE + target.getDisplayName() + CC.RED + " was found " +
                CC.WHITE + DISTANCE_FORMAT.format(targetLocation.distance(player.getLocation())) + CC.RED + " blocks away!");
        player.setCompassTarget(targetLocation);

        return false;
    }


    @Override
    public long getCooldownTime() {
        return 0;
    }

    @Override
    protected ItemStack partnerItem() {
        return ItemBuilder.of(Material.COMPASS)
                .name(getName())
                .addToLore(
                        "&7Tracks to the nearest player."
                ).build();
    }

    @Override
    public String getName() {
        return "§c§lPurge Tracker";
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
