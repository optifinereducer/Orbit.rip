package net.frozenorb.foxtrot.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

@UtilityClass
public final class GlowUtil {

    public Enchantment getGlowEnchant() {
        return Enchantment.getById(79);
    }

    @SneakyThrows
    public void init() {
        Field acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
        acceptingNew.setAccessible(true);
        acceptingNew.set(null, true);
        Enchantment.registerEnchantment(new Enchantment(79) {
            @Override
            public String getName() {
                return "GLOW";
            }

            @Override
            public int getMaxLevel() {
                return 0;
            }

            @Override
            public int getStartLevel() {
                return 0;
            }

            @Override
            public EnchantmentTarget getItemTarget() {
                return null;
            }

            @Override
            public boolean conflictsWith(Enchantment enchantment) {
                return false;
            }

            @Override
            public boolean canEnchantItem(ItemStack itemStack) {
                return false;
            }
        });
    }

}
