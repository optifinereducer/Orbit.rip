package net.frozenorb.foxtrot.partner.impl;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.EffectUtil;
import net.frozenorb.foxtrot.util.Pair;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class DreDebuff extends PartnerPackage {

    private static final int HITS = 3; // how many hits it takes for the item to activate

    private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();

    public DreDebuff() {
        super("DreDbuff");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player entity = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            Pair<UUID, UUID> key = new Pair<>(attacker.getUniqueId(), entity.getUniqueId());

            ItemStack item = attacker.getItemInHand();
            boolean partnerItem = isPartnerItem(item);

            if (attackMap.containsKey(key) && !partnerItem) {
                attackMap.remove(key);
                return;
            }

            if (!partnerItem) {
                return;
            }

            if (DTRBitmask.KOTH.appliesAt(attacker.getLocation()) || DTRBitmask.CITADEL.appliesAt(attacker.getLocation())) {
                attacker.sendMessage(CC.RED + "You cannot use this in koth/citadel!");
                return;
            }

            if (isOnCooldown(attacker)) {
                attacker.sendMessage(getCooldownMessage(attacker));
                return;
            }

            int hits = attackMap.getOrDefault(key, 0);

            if (++hits < HITS) {
                attackMap.put(key, hits);
                return;
            }

            attackMap.remove(key);

            setCooldown(attacker);
            consume(attacker, item);
            int duration = CustomTimerCreateCommand.isPartnerPackageHour() ? 30 : 20;
            Arrays.asList(
                    new PotionEffect(PotionEffectType.POISON, duration * 20, 0),
                    new PotionEffect(PotionEffectType.SLOW, duration * 20, 0)
            ).forEach(potionEffect -> {
                entity.addPotionEffect(potionEffect);
                EffectUtil.splash(entity, entity.getLocation());
                EffectUtil.splash(attacker, entity.getLocation());
            });

            sendActivationMessages(
                    attacker,
                    new String[]{
                            "Successfully hit " + CC.DARK_GREEN + entity.getName() + CC.GREEN + " with " + getName() + CC.GREEN + "!",
                            "That player is debuffed for " + duration + " seconds!"

                    },
                    entity,
                    new String[]{
                            attacker.getName() + " has hit you with " + getName() + CC.RED + "!",
                            "You have been debuff'd for " + duration + " seconds!"
                    }
            );

        }
    }

    @EventHandler // cleanup attack map
    private void onQuit(PlayerQuitEvent event) {
        attackMap.entrySet().removeIf(entry -> entry.getKey().first.equals(event.getPlayer().getUniqueId()));
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPartnerPackageHour() ? 60L : TimeUnit.MINUTES.toSeconds(2) + 30;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.RAW_FISH)
                .name(getName())
                .data((byte) 3)
                .addToLore(
                        "&7Hit a player 3 times to double",
                        "&7debuff them for 20 seconds!"
                ).build();
    }

    @Override
    public String getName() {
        return "§9§lDre's Debuff";
    }

    @Override
    public int getAmount() {
        return 6;
    }
}
