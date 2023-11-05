package net.frozenorb.foxtrot.ftop;

import net.frozenorb.foxtrot.team.Team;

public final class FTopHandler {

	// index 0 will be the required number of data they need in that field
	// index 1 is the amount of points it gives
	private static final int[] POINTS_KILLS = { 5, 5 };
	private static final int[] POINTS_DEATHS = { 5, -10 };
	private static final int[] POINTS_DIAMONDS_MINED = { 100, 5 };
	private static final int[] POINTS_KOTH_CAPTURES = { 1, 25 };
	private static final int[] POINTS_CITADEL_CAPTURES = { 1, 200 };

	public int getTotalPoints(Team team) {
		int total = 0;

		total += getPoints(team.getKills(), POINTS_KILLS);
		total += getPoints(team.getDeaths(), POINTS_DEATHS);
//		total += getPoints(team.getDiamondsMined(), POINTS_DIAMONDS_MINED);
		total += getPoints(team.getKothCaptures(), POINTS_KOTH_CAPTURES);
		total += getPoints(team.getCitadelsCapped(), POINTS_CITADEL_CAPTURES);
		if (team.isEotwCapped()) {
			total += 100;
		}

		return Math.max(total, 0);
	}

	private int getPoints(int field, int[] data) {
		int points = 0;

		int required = data[0];

		if (field >= required) {
			int remaining = field - (field % required);
			points += ((remaining / required) * data[1]);
		}

		return points;
	}
}
