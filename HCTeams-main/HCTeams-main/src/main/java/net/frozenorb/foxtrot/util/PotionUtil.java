package net.frozenorb.foxtrot.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_7_R4.EntityPotion;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public final class PotionUtil {

	public void splashPotion(Player player, ItemStack itemStack) {
		CraftWorld world = (CraftWorld) player.getWorld();
		CraftPlayer craftPlayer = (CraftPlayer) player;
		net.minecraft.server.v1_7_R4.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
		EntityPotion entity = new EntityPotion(world.getHandle(), craftPlayer.getHandle(), stack);
		world.getHandle().addEntity(entity);
	}

}
