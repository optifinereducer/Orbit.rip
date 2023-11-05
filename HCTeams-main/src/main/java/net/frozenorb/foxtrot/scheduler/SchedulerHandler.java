package net.frozenorb.foxtrot.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class SchedulerHandler {

	private final List<BukkitRunnable> scheduled = new ArrayList<>();

	public SchedulerHandler() {

	}

	// tasks:
	//   -
	public void loadTasks() {
		scheduled.forEach(BukkitRunnable::cancel);
		scheduled.clear();


	}

}
