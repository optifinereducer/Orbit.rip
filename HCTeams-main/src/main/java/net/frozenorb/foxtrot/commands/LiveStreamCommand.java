package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public final class LiveStreamCommand {

	@Command(names={ "stream" }, permission="foxtrot.stream")
	public static void lives(Player sender, @Param(name = "link", wildcard = true) String link) {
		String lowerCase = link.toLowerCase();
		if (lowerCase.contains("youtu.be") || lowerCase.contains("youtube.com") || lowerCase.contains("twitch.tv")) {
			sender.setMetadata("stream_link", new FixedMetadataValue(Foxtrot.getInstance(), link));
			sender.sendMessage(ChatColor.GREEN + "Set your stream link!");
		} else {
			sender.sendMessage(ChatColor.RED + "You must enter a valid link!");
		}

	}
}
