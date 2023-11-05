package net.frozenorb.foxtrot.map.killstreaks.valortypes;

import com.google.common.collect.ImmutableMap;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.killstreaks.Killstreak;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

import java.util.Map;

public class GemKillstreak extends Killstreak {

	private static final Map<Integer, Integer> KILL_GEM_MAP = ImmutableMap.<Integer, Integer>builder()
			.put(5, 3)
			.put(10, 5)
			.put(25, 20)
			.put(75, 60)
			.put(100, 80)
			.put(200, 150)
			.build();

	private static final int[] KILLS = KILL_GEM_MAP.keySet().stream()
			.mapToInt(Integer::intValue)
			.toArray();

	@Override
	public String getName() {
		return "Gem";
	}

	@Override
	public int[] getKills() {
		return KILLS;
	}

	@Override
	public void apply(Player player, int kills) {
		int gems = KILL_GEM_MAP.get(kills);
		Foxtrot.getInstance().getGemMap().addGems(player.getUniqueId(), gems);
		player.sendMessage(CC.GREEN + "You have received " + CC.DARK_GREEN + "+" + (gems) +
				CC.GREEN + " for a " + CC.DARK_GREEN + kills + CC.GREEN + " killstreak!");
	}

	@Override
	public void apply(Player player) {


	}
}
