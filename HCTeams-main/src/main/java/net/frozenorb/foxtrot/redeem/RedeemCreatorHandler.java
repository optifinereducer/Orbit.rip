package net.frozenorb.foxtrot.redeem;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.FileConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

public final class RedeemCreatorHandler {

	private final FileConfig fileConfig = new FileConfig(Foxtrot.getInstance(), "redeem_creator.yml");

	public RedeemCreatorHandler() {
		loadConfig();
	}

	private void loadConfig() {
		FileConfiguration config = fileConfig.getConfig();
		config.addDefault("partner.dre.commands", Collections.singletonList("/cr givekey {player} dre 1"));

		config.options().copyDefaults(true);
		fileConfig.save();
	}

	public List<String> getPartnerCommands(String partner) {
		String path = "partner." + partner.toLowerCase();
		FileConfiguration config = fileConfig.getConfig();

		List<String> commands = config.getStringList(path + ".commands");
		if (commands == null || commands.isEmpty()) {
			return null;
		}
		return commands;
	}

}
