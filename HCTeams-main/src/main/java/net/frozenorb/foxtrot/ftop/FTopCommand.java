package net.frozenorb.foxtrot.ftop;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class FTopCommand {

	@Command(names={ "f top", "fac top", "faction top"}, async = true, permission = "")
	public static void ftop(Player sender) {
		FTopHandler top = Foxtrot.getInstance().getTopHandler();
		ArrayList<Team> teams = new ArrayList<>(Foxtrot.getInstance().getTeamHandler().getTeams());
		teams.removeIf(team -> team.getOwner() == null);
		teams.sort(Collections.reverseOrder(Comparator.comparingInt(top::getTotalPoints)));

		sender.sendMessage("§8§m--------------------------------");
		sender.sendMessage("§c» §4§lTop Factions §c«");
		sender.sendMessage(" ");
		for (int i = 0; i < teams.size(); i++) {
			if (i > 4) break;
			Team team = teams.get(i);
			FancyMessage fancyMessage = new FancyMessage("§c" + (i + 1) + ". §7" + team.getName() + " §f- " + top.getTotalPoints(team));
			fancyMessage.tooltip(
					"§4§l" + team.getName(sender),
					" ",
					"§cLeader: §7" + FrozenUUIDCache.name(team.getOwner()),
					"§cKills: §7" + team.getKills(),
					"§cDeaths: §7" + team.getDeaths(),
					" ",
					"§cCitadel Captures: §7" + team.getCitadelsCapped(),
					"§cKOTH Captures: §7" + team.getKothCaptures(),
					"§cEOTW Captured: §7" + (team.isEotwCapped() ? "Yes" : "No"),
					" ",
					"§aClick for faction info"
			);

			fancyMessage.command("/f who " + team.getName());
			fancyMessage.send(sender);
		}
		sender.sendMessage("§8§m--------------------------------");
	}

}
