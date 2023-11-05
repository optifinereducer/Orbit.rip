package net.frozenorb.foxtrot.partner.impl.bard;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.partner.PartnerPackage;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class PortableBard extends PartnerPackage {

    static final int EFFECT_RANGE = 32;

    static final List<PortableBardEffect> PORTABLE_BARD_EFFECTS = Arrays.asList(
            new PortableBardEffect(Material.BLAZE_POWDER, PotionEffectType.INCREASE_DAMAGE, 6, 10, 1),
            new PortableBardEffect(Material.SUGAR, PotionEffectType.SPEED, 6, 10, 2),
            new PortableBardEffect(Material.FEATHER, PotionEffectType.JUMP, 8, 16, 6),
            new PortableBardEffect(Material.IRON_INGOT, PotionEffectType.DAMAGE_RESISTANCE, 6, 10, 2),
            new PortableBardEffect(Material.GHAST_TEAR, PotionEffectType.REGENERATION, 6, 10, 2),
            new PortableBardEffect(Material.MAGMA_CREAM, PotionEffectType.FIRE_RESISTANCE, 45, 90, 0),
            new PortableBardEffect(Material.INK_SACK, PotionEffectType.INVISIBILITY, 45, 90, 0)
    );

    private static final Map<String, PortableBardEffect> ITEM_STACK_PORTABLE_BARD_EFFECT_MAP = new HashMap<>();

    private static final Material MATERIAL = Material.INK_SACK;

    static {
        for (PortableBardEffect effect : PORTABLE_BARD_EFFECTS) {
            ITEM_STACK_PORTABLE_BARD_EFFECT_MAP.put(effect.toItemStack().getItemMeta().getDisplayName().toLowerCase(), effect);
        }
    }

    public PortableBard() {
        super("PortableBard");
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
            return false;
        }

        PortableBardEffect bardEffect = ITEM_STACK_PORTABLE_BARD_EFFECT_MAP.get(item.getItemMeta().getDisplayName().toLowerCase());

        // if it's null then the only other thing it could be was the main item
        if (bardEffect == null) {
            new PortableBardMenu(event.getItem()).openMenu(player);
            return false;
        }

        // manually handle this here because portable bard is
        // special and allows you to open the menu but not use effects
        if (isOnCooldown(player)) {
            event.setCancelled(true);
            player.sendMessage(getCooldownMessage(player));
            return false;
        }

        TeamHandler handler = Foxtrot.getInstance().getTeamHandler();
        Team team = handler.getTeam(player);
        List<Player> toEffect = new ArrayList<>();
        if (team != null) {
            for (Player onlineMember : team.getOnlineMembers()) {
                if (player != onlineMember && player.getLocation().distance(onlineMember.getLocation()) <= EFFECT_RANGE) {
                    toEffect.add(onlineMember);
                }
            }
        }

        PotionEffect potionEffect = bardEffect.getPotionEffect();

        player.addPotionEffect(potionEffect, true);
        toEffect.forEach(mate -> mate.addPotionEffect(potionEffect, true));

        setCooldown(player);
        setGlobalCooldown(player);

        player.sendMessage(CC.GREEN + "Successfully applied " + bardEffect.getNiceName() + " to your faction!");

        return true;
    }

    @Override
    public long getCooldownTime() {
        return CustomTimerCreateCommand.isPurgeTimer() ? 30L : TimeUnit.MINUTES.toSeconds(2);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(MATERIAL)
                .data((short) 14)
                .name("&d&lPortable Bard")
                .addToLore("&7Click and choose to receive 5 of")
                .addToLore("&7any right-clickable bard effect!")
                .build();
    }

    @Override
    public String getName() {
        return "§d§lPortable Bard";
    }

    @Override
    public int getAmount() {
        return 2;
    }

    @Override
    public boolean isExclusive() {
        return false;
    }

    @Override
    public boolean isPartnerItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        if (item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
            return false;
        }

        PortableBardEffect effect = ITEM_STACK_PORTABLE_BARD_EFFECT_MAP.get(item.getItemMeta().getDisplayName().toLowerCase());
        return effect != null || super.isPartnerItem(item);
    }
}
