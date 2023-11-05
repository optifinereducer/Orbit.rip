package net.frozenorb.foxtrot.partner.impl.bard;

import lombok.Getter;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
public final class PortableBardEffect {

    private final Material material;
    private final PotionEffectType potionEffectType;
    private final int effectTime; // in seconds
    private final int boostedTime; // in seconds
    private final int amplifier; // in seconds
    private final String niceName;

    public PortableBardEffect(Material material, PotionEffectType potionEffectType, int effectTime, int boostedTime, int amplifier) {
        this.material = material;
        this.potionEffectType = potionEffectType;
        this.niceName = convertName(potionEffectType);
        this.effectTime = effectTime;
        this.boostedTime = boostedTime;
        this.amplifier = amplifier;
    }

    public ItemStack toItemStack() {
        return ItemBuilder.of(material)
                .name("&d&l" + niceName)
                .addToLore("&7Right-click to apply the")
                .addToLore("&7effect to your faction!")
                .build();
    }

    public PotionEffect getPotionEffect() {
        int duration = CustomTimerCreateCommand.isPartnerPackageHour() ? this.boostedTime : this.effectTime;
        return new PotionEffect(potionEffectType, 20 * duration, this.amplifier);
    }

    public static String convertName(PotionEffectType potionEffectType) {
        if (potionEffectType == PotionEffectType.INCREASE_DAMAGE) {
            return "Strength II";
        } else if (potionEffectType == PotionEffectType.DAMAGE_RESISTANCE) {
            return "Resistance III";
        } else if (potionEffectType == PotionEffectType.SPEED) {
            return "Speed III";
        } else if (potionEffectType == PotionEffectType.JUMP) {
            return "Jump Boost VII";
        } else if (potionEffectType == PotionEffectType.REGENERATION) {
            return "Regeneration III";
        } else if (potionEffectType == PotionEffectType.FIRE_RESISTANCE) {
            return "Fire Resistance";
        } else if (potionEffectType == PotionEffectType.INVISIBILITY) {
            return "Invisibility";
        } else if (potionEffectType == PotionEffectType.FAST_DIGGING) {
            return "Haste";
        } else if (potionEffectType == PotionEffectType.SLOW_DIGGING) {
            return "Fatigue";
        } else if (potionEffectType == PotionEffectType.SLOW) {
            return "Slowness";
        } else {
            return potionEffectType.getName();
        }
    }

}