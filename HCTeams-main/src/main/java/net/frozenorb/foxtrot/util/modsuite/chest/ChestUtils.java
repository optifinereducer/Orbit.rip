package net.frozenorb.foxtrot.util.modsuite.chest;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventory;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class ChestUtils {
	private static Field windowField;

	static {
		ChestUtils.windowField = null;
		try {
			(ChestUtils.windowField = EntityPlayer.class.getDeclaredField("containerCounter")).setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public static void openSilently(final Player p, final Chest c) {
		try {
			p.sendMessage(ChatColor.RED + "Opening chest silently...");
			final EntityPlayer player = ((CraftPlayer) p).getHandle();
			final IInventory inventory = ((CraftInventory) c.getInventory()).getInventory();
			player.nextContainerCounter();
			final int counter = ChestUtils.windowField.getInt(player);
			final SilentContainerChest silentChest = new SilentContainerChest(player.inventory, inventory);
			player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(counter, 0, inventory.getInventoryName(), inventory.getSize(), inventory.k_()));
			player.activeContainer = silentChest;
			player.activeContainer.windowId = counter;
			player.activeContainer.addSlotListener(player);
			c.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
