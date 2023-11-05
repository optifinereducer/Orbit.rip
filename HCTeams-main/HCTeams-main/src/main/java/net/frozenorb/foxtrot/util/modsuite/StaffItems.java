package net.frozenorb.foxtrot.util.modsuite;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StaffItems {
	public static ItemStack COMPASS;
	public static ItemStack INSPECT_BOOK;
	public static ItemStack WAND;
	public static ItemStack GO_VIS;
	public static ItemStack GO_INVIS;
	public static ItemStack ONLINE_STAFF;
	public static ItemStack CARPET;
	public static ItemStack RANDOM_TELEPORT;

	static {
		StaffItems.COMPASS = build(Material.COMPASS, ChatColor.GOLD + "Compass");
		StaffItems.INSPECT_BOOK = build(Material.BOOK, ChatColor.GOLD + "Inspection Book");
		StaffItems.WAND = build(Material.WOOD_AXE, ChatColor.GOLD + "WorldEdit Wand");
		StaffItems.GO_VIS = build(Material.INK_SACK, 1, DyeColor.GRAY.getDyeData(), ChatColor.GREEN + "Become Visible");
		StaffItems.GO_INVIS = build(Material.INK_SACK, 1, DyeColor.LIME.getDyeData(), ChatColor.RED + "Become Invisible");
		StaffItems.ONLINE_STAFF = build(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GOLD + "Online Staff");
		StaffItems.CARPET = build(Material.CARPET, 1, DyeColor.RED.getWoolData(), " ");
		StaffItems.RANDOM_TELEPORT = build(Material.EMERALD, ChatColor.LIGHT_PURPLE + "Random Teleport");
	}

	public static ItemStack build(final Material type, final String displayName) {
		return build(type, 1, (byte) 0, displayName);
	}

	public static ItemStack build(final Material type, final int amount, final byte data, final String displayName) {
		final ItemStack stack = new ItemStack(type, amount, (short) 1, data);
		final ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
		stack.setItemMeta(meta);
		return stack;
	}
}
