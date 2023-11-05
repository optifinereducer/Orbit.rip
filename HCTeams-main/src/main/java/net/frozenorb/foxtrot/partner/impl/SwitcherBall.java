package net.frozenorb.foxtrot.partner.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public final class SwitcherBall extends PartnerPackage {

    public SwitcherBall() {
        super("Switcher");
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onSnowBallLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Snowball && event.getEntity().getShooter() instanceof Player) {
            Snowball snowball = (Snowball) event.getEntity();
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

            setCooldown(player);
            snowball.setMetadata("LaunchLocation", new FixedMetadataValue(Foxtrot.getInstance(), snowball.getLocation()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onSnowBallHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Snowball))
            return;

        Snowball snowball = (Snowball) event.getDamager();
        if (event.getEntity() instanceof Player && snowball.getShooter() instanceof Player) {
            Player shooter = (Player) snowball.getShooter();
            Player target = (Player) event.getEntity();

            if (!snowball.hasMetadata("LaunchLocation"))
                return;

            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(shooter);

            if (team != null && team.isMember(target.getUniqueId())) {
                shooter.sendMessage(CC.RED + "You cannot switch this player!");
                return;
            }

            Location shooterLocation = shooter.getLocation();
            if (DTRBitmask.SAFE_ZONE.appliesAt(shooterLocation)) {
                shooter.sendMessage(CC.RED + "You cannot use this in spawn!");
                return;
            }

            Location targetLocation = target.getLocation();
            if (DTRBitmask.SAFE_ZONE.appliesAt(targetLocation)) {
                shooter.sendMessage(CC.RED + "That player is in spawn!");
                return;
            }

            double distance = CustomTimerCreateCommand.isPartnerPackageHour() ? 16.00D : 8.00D;

            if (shooterLocation.distance(targetLocation) > distance) {
                shooter.sendMessage(
                        CC.RED + "You hit " + CC.DARK_RED + target.getName() +
                                CC.RED + " with " + CC.PINK + CC.BOLD + getName() +
                                CC.RED + ", but they were out of range!"
                );
                return;
            }

            setGlobalCooldown(shooter);

            shooter.teleport(targetLocation);
            target.teleport(shooterLocation);

            sendActivationMessages(
                    shooter,
                    new String[]{
                            "Successfully hit " + CC.DARK_GREEN + target.getName() + " " + CC.GREEN + "with " +
                                    getName() + CC.GREEN + "!",
                            "Your positions have been switched!"
                    },
                    target,
                    new String[]{
                            shooter.getName() + " has hit you with a " + getName() + CC.RED + "!",
                            "Your positions have been switched!"
                    }
            );

            EnderpearlCooldownHandler.clearEnderpearlTimer(target);
        }
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.SNOW_BALL)
                .name("&d&lSwitcher Ball")
                .addToLore("&7Switch positions with an enemy")
                .addToLore("&7within 8 blocks of you!")
                .build();
    }

    @Override
    public String getName() {
        return "§d§lSwitcher Ball";
    }

    @Override
    public int getAmount() {
        return 2;
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPartnerPackageHour() ? 5L : 15L;
    }

    @Override
    public boolean isExclusive() {
        return false;
    }
}
