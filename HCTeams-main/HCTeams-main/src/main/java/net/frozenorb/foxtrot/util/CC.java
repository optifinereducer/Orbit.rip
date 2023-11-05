package net.frozenorb.foxtrot.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@UtilityClass
public final class CC {

	public final String STAR = "✦";

	public final String BLACK = ChatColor.BLACK.toString();
	public final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
	public final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
	public final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
	public final String DARK_RED = ChatColor.DARK_RED.toString();
	public final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
	public final String GOLD = ChatColor.GOLD.toString();
	public final String GRAY = ChatColor.GRAY.toString();
	public final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
	public final String BLUE = ChatColor.BLUE.toString();
	public final String GREEN = ChatColor.GREEN.toString();
	public final String AQUA = ChatColor.AQUA.toString();
	public final String RED = ChatColor.RED.toString();
	public final String PINK = ChatColor.LIGHT_PURPLE.toString();
	public final String YELLOW = ChatColor.YELLOW.toString();
	public final String WHITE = ChatColor.WHITE.toString();
	public final String MAGIC = ChatColor.MAGIC.toString();
	public final String BOLD = ChatColor.BOLD.toString();
	public final String STRIKETHROUGH = ChatColor.STRIKETHROUGH.toString();
	public final String UNDERLINE = ChatColor.UNDERLINE.toString();
	public final String ITALIC = ChatColor.ITALIC.toString();
	public final String RESET = ChatColor.RESET.toString();

	public String translate(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public String translate(String string, Object... format) {
		return String.format(ChatColor.translateAlternateColorCodes('&', string), format);
	}

	public void broadcast(String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(message);
		}
	}

}