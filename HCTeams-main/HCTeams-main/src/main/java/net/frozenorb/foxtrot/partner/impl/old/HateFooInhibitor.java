package net.frozenorb.foxtrot.partner.impl.old;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Pair;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class HateFooInhibitor extends PartnerPackage {

    private static final int EFFECT_TIME = 15; // the length in seconds that effects should be given for
    private static final int EFFECT_BOOST_TIME = 20; // the length in seconds that effects should be given for

    public HateFooInhibitor() {
        super("Inhib");
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        List<MetadataValue> attackMeta = player.getMetadata("last_attack");
        Pair<UUID, Instant> lastAttack = null;
        Player attacker = null;
        if (!attackMeta.isEmpty()) {
            //noinspection unchecked
            lastAttack = (Pair<UUID, Instant>) attackMeta.get(0).value();
            attacker = Bukkit.getPlayer(lastAttack.first);
        }

        if (lastAttack == null || attacker == null) {
            player.sendMessage(ChatColor.RED + "The last person who attacked you could not be found!");
            return false;
        }

        Team attackerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(attacker);
        if (attackerTeam != null && attackerTeam.isMember(event.getPlayer().getUniqueId())) {
            return false;
        }

        if (Instant.now().isAfter(lastAttack.second.plusSeconds(10))) {
            player.sendMessage(ChatColor.RED + "It has been more than 10 seconds since that player attacked you!");
            return false;
        }

        attacker.getActivePotionEffects()
                .forEach(potionEffect -> {
                    player.addPotionEffect(new PotionEffect(potionEffect.getType(), getEffectTime() * 20, potionEffect.getAmplifier()));
                });

        setCooldown(player);
        sendActivationMessages(player,
                new String[]{
                        "Successfully received " + CC.DARK_GREEN + attacker.getName() + "'s " + CC.GREEN + "effects!",
                        "You have received all of their effects for " + getEffectTime() + " seconds!"
                },
                null,
                null);
        return true;
    }

    @Override
    public long getCooldownTime() {
        return TimeUnit.MINUTES.toSeconds(2);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.BLAZE_ROD)
                .name("&c&lInhibitor")
                .addToLore(
                        "&7Right-click to receive 15 seconds",
                        "&7of the enemy's effects who last",
                        "&7hit you within 10 seconds."
                ).build();
    }

    @Override
    public String getName() {
        return "§c§lInhibitor";
    }

    @Override
    public int getAmount() {
        return 4;
    }

    private int getEffectTime() {
        return CustomTimerCreateCommand.isPartnerPackageHour() ? EFFECT_BOOST_TIME : EFFECT_TIME;
    }
}
