package net.frozenorb.foxtrot.partner;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.partner.impl.*;
import net.frozenorb.foxtrot.partner.impl.bard.PortableBard;
import net.frozenorb.foxtrot.partner.impl.old.DylanRage;
import net.frozenorb.foxtrot.partner.impl.old.HateFooInhibitor;
import net.frozenorb.foxtrot.partner.impl.old.ZigyHammer;
import net.frozenorb.foxtrot.partner.impl.purge.BackPackPurgePackage;
import net.frozenorb.foxtrot.partner.impl.purge.ExplosivePurgePackage;
import net.frozenorb.foxtrot.partner.impl.purge.MaskPurgePackage;
import net.frozenorb.foxtrot.partner.impl.purge.TrackerPurgePackage;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.Pair;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PartnerPackageHandler implements Listener {

    public static final String PREFIX = "&d&lPI Event";

    @Getter
    private final List<PartnerPackage> packages = new ArrayList<>(Arrays.asList(
            // Generic
            new SwitcherBall(),
            new PortableBard(),
            new AntiBuildStick(),

            // Purge
            new ExplosivePurgePackage(),
            new TrackerPurgePackage(),
            new BackPackPurgePackage(),
            new MaskPurgePackage(),

            // Partner
            new EimohFiftyFifty(),
            new DeafedPowerup(),
            new GgusGift(),
            new JeanCLSwitchStick(),
            new KiuubScrambler(),
            new ThreeWordAntiBard(),
            new ThreeWordAntiBard(),
            new DylanRage(),
            new HateFooInhibitor(),
            new ZigyHammer()

    ));

    public PartnerPackageHandler() {
        if (Bukkit.getServerName().equals("Kits")) {
            // Empty for now
        } else if (Bukkit.getServerName().equals("HCF")) {
            // Empty for now
        }

        packages.forEach(partnerPackage -> {
            partnerPackage.loadFromRedis();
            Bukkit.getServer().getPluginManager().registerEvents(partnerPackage, Foxtrot.getInstance());
        });

        Bukkit.getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
        FrozenCommandHandler.registerParameterType(PartnerPackage.class, new PartnerPackageType());

        // clean up cooldown map
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            private void onPlayerQuit(PlayerQuitEvent event) {
                PartnerPackage.GLOBAL_PACKAGE_COOLDOWN.remove(event.getPlayer().getUniqueId());
            }
        }, Foxtrot.getInstance());

        Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            for (PartnerPackage partnerPackage : packages) {
                if (partnerPackage.tickTask() != null)
                    partnerPackage.tickTask().run();
            }
        }, 20L, 20L);

    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        PartnerPackage heldPackage = null;
        for (PartnerPackage partnerPackage : packages) {
            if (partnerPackage.isPartnerItem(item)) {
                heldPackage = partnerPackage;
            }
        }

        if (heldPackage == null) {
            return;
        }

        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (heldPackage.isOnCooldown(player)) {
                player.sendMessage(heldPackage.getCooldownMessage(player));
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location location = player.getLocation();

            if (DTRBitmask.KOTH.appliesAt(location) || DTRBitmask.CITADEL.appliesAt(location)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Partner items cannot be used in koths/citadels!");
                return;
            }

            if (!(heldPackage instanceof PortableBard) && heldPackage.isOnCooldown(player)) {
                event.setCancelled(true);
                player.sendMessage(heldPackage.getCooldownMessage(player));
                return;
            }

            if (heldPackage.onUse(event)) {
                heldPackage.consume(player, item);

                if (player.getWorld().getEnvironment() == World.Environment.NETHER || player.getWorld().getEnvironment() == World.Environment.THE_END) {
                    Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
                    if (team != null) {
                        for (Player teamPlayer : team.getOnlineMembers()) {
                            if (player == teamPlayer) {
                                continue;
                            }

                            heldPackage.setCooldown(teamPlayer);
                            teamPlayer.sendMessage(ChatColor.RED + "A faction cooldown has been applied because a member used the " + ChatColor.BOLD + heldPackage.getName() + ChatColor.RED + " partner item!");
                        }
                    }
                }

                if (Foxtrot.getInstance().getBattlePassHandler() != null) {
                    Foxtrot.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                        progress.setPartnerItemsUsed(progress.getPartnerItemsUsed() + 1);
                        progress.requiresSave();

                        Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                    });
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPlayerHitPlayer(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            FixedMetadataValue metadata = new FixedMetadataValue(Foxtrot.getInstance(),
                    new Pair<>(event.getDamager().getUniqueId(), Instant.now())
            );
            event.getEntity().setMetadata("last_attack", metadata);
        }
    }

    public PartnerPackage getPartnerPackageByName(String name) {
        for (PartnerPackage partnerPackage : packages) {
            String packageName = ChatColor.stripColor(partnerPackage.getName())
                    .toLowerCase()
                    .replace(" ", "_")
                    .replace("'", "");
            if (name.equalsIgnoreCase(packageName))
                return partnerPackage;
        }
        return null;
    }

    public <T extends PartnerPackage> T getPackage(Class<T> clazz) {
        for (PartnerPackage partnerPackage : packages) {
            if (clazz.isInstance(partnerPackage))
                return clazz.cast(partnerPackage);
        }
        return null;
    }

}
