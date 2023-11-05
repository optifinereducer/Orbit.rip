package net.frozenorb.foxtrot.purge.reward;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public final class PurgeRewardMap extends PersistMap<Integer> {

	public PurgeRewardMap() {
		super("PurgeR", "PurgeRewards");
	}

	@Override
	public String getRedisValue(Integer integer) {
		return Integer.toString(integer);
	}

	@Override
	public Object getMongoValue(Integer integer) {
		return integer;
	}

	@Override
	public Integer getJavaObject(String str) {
		return Integer.parseInt(str);
	}

	public boolean hasReward(UUID uuid) {
		if (!contains(uuid))
			return false;
		return getValue(uuid) > 0;
	}

	public int getRewards(UUID uuid) {
		return contains(uuid) ? getValue(uuid) : 0;
	}

	public void setRewards(UUID uuid, int rewards) {
		updateValueAsync(uuid, rewards);
	}

	public void addRewards(UUID uuid, int rewards) {
		updateValueAsync(uuid, getRewards(uuid) + rewards);
	}
}
