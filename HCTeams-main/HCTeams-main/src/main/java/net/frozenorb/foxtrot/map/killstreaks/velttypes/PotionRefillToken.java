package net.frozenorb.foxtrot.map.killstreaks.velttypes;

import net.frozenorb.foxtrot.map.killstreaks.Killstreak;
import net.frozenorb.qlib.util.ItemBuilder;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PotionRefillToken extends Killstreak {

    @Override
    public String getName() {
        return "Potion Refill Token";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                15
        };
    }

    @Override
    public void apply(Player player) {
        give(player, ItemBuilder.of(Material.NETHER_STAR).name("&5&k! &d&lPotion Refill Token &5&k!").setUnbreakable(true).setLore(ImmutableList.of("&7Right click to fill your inventory with potions!")).build());
    }

}