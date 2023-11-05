package net.frozenorb.foxtrot.partner.impl.old;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public final class ZigyHammer extends PartnerPackage {

    public ZigyHammer() {
        super("ZigyHam");
    }

    private final Map<Player, Integer> launches = new WeakHashMap<>();

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        int launches = this.launches.getOrDefault(player, 0);
        this.launches.put(player, ++launches);

        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setMetadata("zigy_ham", new FixedMetadataValue(Foxtrot.getInstance(), true));
        return false;
    }

    @EventHandler
    private void onSnowBallHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball && event.getEntity().hasMetadata("zigy_ham")) {
            Player shooter = (Player) event.getEntity().getShooter();
            if (!isOnCooldown(shooter)) {
                Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                    int launches = this.launches.getOrDefault(shooter, 0);
                    if (launches >= 3 && !isOnCooldown(shooter)) {
                        setGlobalCooldown(shooter);
                    }
                }, 3L);
            }
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

            if (!snowball.hasMetadata("zigy_ham"))
                return;

            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(shooter);

            if (team != null && team.isMember(target.getUniqueId())) {
                shooter.sendMessage(CC.RED + "You cannot launch this player!");
                return;
            }

            setGlobalCooldown(shooter);
            setCooldown(shooter);

            Location targetLocation = target.getLocation();
            target.getWorld().playSound(targetLocation, Sound.EXPLODE, .75f, 1f);

            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                target.setVelocity(target.getVelocity().multiply(5));
            }, 2L);

            sendActivationMessages(
                    shooter,

                    new String[]{
                            "Successfully hit " + CC.DARK_GREEN + target.getName() + " " + CC.GREEN + "with " +
                                    getName() + CC.GREEN + "!",
                            "Launched!"
                    },
                    target,
                    new String[]{
                            shooter.getName() + " has hit you with " + getName() + CC.RED + "!"
                    }
            );

            launches.remove(shooter);
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        launches.remove(event.getPlayer());
    }


    @Override
    public long getCooldownTime() {
        return TimeUnit.MINUTES.toSeconds(1) + 30;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.IRON_AXE)
                .name(getName())
                .addToLore(
                        "&7Upon right-clicking this axe, a snowball will ",
                        "&7shoot out that will launch a player backwards."
                ).build();
    }

    @Override
    public String getName() {
        return "§d§lZigy's Hammer";
    }

    @Override
    public int getAmount() {
        return 4;
    }
}
