package net.frozenorb.foxtrot.partner;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.command.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class PartnerPackageType implements ParameterType<PartnerPackage> {

	@Override
	public PartnerPackage transform(CommandSender commandSender, String s) {
		PartnerPackageHandler handler = Foxtrot.getInstance().getPartnerPackageHandler();

		PartnerPackage partnerPackageByName = handler.getPartnerPackageByName(s);
		if (partnerPackageByName == null) {
			String available = handler.getPackages().stream()
					.map(PartnerPackage::getName)
					.map(ChatColor::stripColor)
					.map(str -> str.replace("'", "").replace(" ", "_"))
					.map(String::toLowerCase)
					.collect(Collectors.joining(", "));
			commandSender.sendMessage(CC.RED + "No package with that name found!");
			commandSender.sendMessage(CC.RED + "Available: " + CC.YELLOW + available);
		}
		return partnerPackageByName;
	}

	@Override
	public List<String> tabComplete(Player player, Set<String> set, String s) {
		PartnerPackageHandler handler = Foxtrot.getInstance().getPartnerPackageHandler();

		return handler.getPackages()
				.stream()
				.map(PartnerPackage::getName)
				.map(name -> name.replace(" ", "_"))
				.filter(name -> StringUtils.startsWithIgnoreCase(name, s))
				.collect(Collectors.toList());
	}
}
