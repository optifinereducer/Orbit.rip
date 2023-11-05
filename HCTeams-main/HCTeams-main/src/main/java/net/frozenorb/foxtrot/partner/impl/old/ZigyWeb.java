package net.frozenorb.foxtrot.partner.impl.old;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.BlockUtil;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Pair;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ZigyWeb extends PartnerPackage {

    private final List<Pair<List<Block>, Instant>> webs = new ArrayList<>();

    public ZigyWeb() {
        super("ZigyWeb");
    }

    @Override
    protected Runnable tickTask() {

        return () -> webs.removeIf(pair -> {
            if (Instant.now().isAfter(pair.second)) {
                for (Block block : pair.first) {
                    if (block.getType() == Material.WEB)
                        block.setType(Material.AIR);
                }
                return true;
            }
            return false;
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onSnowBallLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Egg && event.getEntity().getShooter() instanceof Player) {
            Egg snowball = (Egg) event.getEntity();
            Player player = (Player) event.getEntity().getShooter();
            if (!isPartnerItem(player.getItemInHand()))
                return;

            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                player.sendMessage(CC.RED + "You cannot use this in spawn!");
                event.setCancelled(true);
                Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), player::updateInventory, 2L);
                return;
            }

            if (isOnCooldown(player)) {
                player.sendMessage(getCooldownMessage(player));
                event.setCancelled(true);
                Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), player::updateInventory, 2L);
                return;
            }

            setGlobalCooldown(player);
            snowball.setMetadata("bambe_egg", new FixedMetadataValue(Foxtrot.getInstance(), snowball.getLocation()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEggHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Egg))
            return;
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        Player shooter = (Player) event.getEntity().getShooter();

        Egg egg = (Egg) event.getEntity();

        if (!egg.hasMetadata("bambe_egg"))
            return;


        Location hitLocation = egg.getLocation();
        Block hitBlock = hitLocation.getBlock();

        if (DTRBitmask.SAFE_ZONE.appliesAt(hitLocation)) {
            shooter.sendMessage(CC.RED + "You cannot use this in spawn!");
            return;
        }

        if (DTRBitmask.KOTH.appliesAt(hitLocation) || DTRBitmask.CITADEL.appliesAt(hitLocation)) {
            shooter.sendMessage(CC.RED + "You cannot use this in koth/citadel!");
            return;
        }

        if (hitBlock.getType() != Material.AIR) {
            shooter.sendMessage(CC.RED + "You threw into an invalid location!");
            return;
        }

        Team team = LandBoard.getInstance().getTeam(hitBlock.getLocation());
        int liveTime;
        if (team == null) {
            liveTime = CustomTimerCreateCommand.isPartnerPackageHour() ? 20 : 15;
        } else {
            liveTime = CustomTimerCreateCommand.isPartnerPackageHour() ? 10 : 5;
        }

        placeWebs(hitBlock, liveTime);

        setCooldown(shooter);
        sendActivationMessages(shooter,
                new String[]{
                        "You have activated " + getName() + CC.GREEN + "!"
                }, null, null);
    }

    private void placeWebs(Block block, int liveTime) {
        List<Block> blocks = new ArrayList<>();
        blocks.add(block);
        if (CustomTimerCreateCommand.isPartnerPackageHour()) {
            for (Block target : BlockUtil.getBlocksAroundCenter(block.getLocation(), 1)) {
                if (target.getType() == Material.AIR) {
                    blocks.add(target);
                }
            }
        }

        blocks.forEach(target -> target.setType(Material.WEB));
        webs.add(new Pair<>(blocks, Instant.now().plusSeconds(liveTime)));
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

    @Override
    public long getCooldownTime() {
        return TimeUnit.MINUTES.toSeconds(1);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.EGG)
                .name(getName())
                .addToLore(
                        "&7Upon throwing the egg, a cobweb",
                        "&7will be created where it lands!"
                ).build();
    }

    @Override
    public String getName() {
        return "§d§lZigy's Web";
    }

    @Override
    public int getAmount() {
        return 4;
    }
}
