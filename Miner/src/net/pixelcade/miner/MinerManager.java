package net.pixelcade.miner;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class MinerManager {

	private MinerDriver plugin;

	public MinerManager(MinerDriver plugin) {
		this.plugin = plugin;
		new BukkitRunnable() {

			@Override
			public void run() {
				runLoop();

			}

		}.runTaskTimer(plugin, 0, 1);
	}

	private int i = 0;

	public void runLoop() {
		if (i > 200000) {
			i = 0;
		}
		i++;

		for (MinerArea miner : this.plugin.mines) {
			if (miner.getPlayer() != null) {

				Player player = miner.getPlayer();
				float speed = 15L;
				boolean mined = false;
				boolean isDone = false;

				if (isDone == true) {
					player.teleport(plugin.getServerSpawn());
					miner.setPlayer(null);
				}
				if (!player.hasPermission("miner.perm")) {
					if (plugin.timeLeft(player) <= 0) {
						player.teleport(plugin.getServerSpawn());
						miner.setPlayer(null);
						player.sendMessage(ChatColor.RED + "You are out of time in the miner.");
						isDone = true;
					} else {
						plugin.subtractTime(player, (System.currentTimeMillis() - miner.getLastBlockBreak()));
						miner.setLastBlockBreak(System.currentTimeMillis());
				}

				if (!isDone || miner.getPlayer() != null) {

					if (player.isOp()) {
						if (i % 1 == 0 && !(mined)) {
							mined = true;
							miner.mine();
						}
					}
					if (player.hasPermission("miner.upgrade.5")) {
						if (i % 2 == 0 && (!mined)) {
							mined = true;
							miner.mine();
						}
					}
					if (player.hasPermission("miner.upgrade.4")) {
						if (i % 3 == 0 && (!mined)) {
							mined = true;
							miner.mine();
						}
					}
					if (player.hasPermission("miner.upgrade.3")) {
						if (i % 6 == 0 && (!mined)) {
							mined = true;
							miner.mine();
						}
					}
					if (player.hasPermission("miner.upgrade.2")) {
						if (i % 9 == 0 && (!mined)) {
							mined = true;
							miner.mine();
						}
					}
					if (player.hasPermission("miner.upgrade.1")) {
						if (i % 12 == 0 && (!mined)) {
							mined = true;
							miner.mine();
						}
					}

					if (i % speed == 0) {
						mined = true;
						miner.mine();
					}
				}
			}
		}

	}

}
