package net.pixelcade.miner;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.koletar.jj.mineresetlite.Mine;
import com.koletar.jj.mineresetlite.MineResetLite;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class MinerDriver extends JavaPlugin {

	ArrayList<MinerArea> mines;
	private Location serverSpawn;
	private MinerManager mineManager;

	public void onEnable() {
		this.serverSpawn = LocationSerializer.deserialize(this.getConfig().getString("server_spawn"));
		mines = new ArrayList<MinerArea>();
		if (this.getConfig().getConfigurationSection("miner_areas") != null) {
			for (String s : this.getConfig().getConfigurationSection("miner_areas").getKeys(false)) {
				String name = this.getConfig().getString("miner_areas." + s + ".name");
				Location tpLocation = LocationSerializer
						.deserialize(this.getConfig().getString("miner_areas." + s + ".tp_location"));
				Location max = LocationSerializer.deserialize(this.getConfig().getString("miner_areas." + s + ".max"));
				Location min = LocationSerializer.deserialize(this.getConfig().getString("miner_areas." + s + ".min"));
				Player player = null;
				this.mines.add(new MinerArea(name, tpLocation, max, min, player, this));
			}
		}

		this.getServer().getPluginManager().registerEvents(new LeaveListener(this), this);

		this.getCommand("miner").setExecutor(new MinerExecutor(this));
		this.getCommand("mineradmin").setExecutor(new MinerAdminExecutor(this));
		this.setMineManager(new MinerManager(this));
	}

	public Mine getMine(Player player) {
		if (player.hasPermission("miner.donor.access")) {
			List<String> list = this.getConfig().getStringList("mine_order");
			String topMine = "";
			if (!player.isOp()) {
				for (String mineName : list) {
					if (player.hasPermission("ezranks.rank." + mineName)) {
						topMine = mineName;
					}
				}
			} else {
				topMine = list.get(list.size() - 1);
			}
			if (getMRL() == null) {
				Bukkit.broadcastMessage("MRL is nulled");
			}
			for (Mine mine : getMRL().mines) {
				if (mine.getName().equalsIgnoreCase(topMine)) {
					return mine;
				}
			}
		} else {
			if (player.hasPermission("miner.upgrade.blocks.5")) {
				for (Mine mine : getMRL().mines) {
					if (mine.getName().equalsIgnoreCase("upgradefive")) {
						return mine;
					}
				}
			}
			if (player.hasPermission("miner.upgrade.blocks.4")) {
				for (Mine mine : getMRL().mines) {
					if (mine.getName().equalsIgnoreCase("upgradefour")) {
						return mine;
					}
				}
			}
			if (player.hasPermission("miner.upgrade.blocks.3")) {
				for (Mine mine : getMRL().mines) {
					if (mine.getName().equalsIgnoreCase("upgradethree")) {
						return mine;
					}
				}
			}
			if (player.hasPermission("miner.upgrade.blocks.2")) {
				for (Mine mine : getMRL().mines) {
					if (mine.getName().equalsIgnoreCase("upgradetwo")) {
						return mine;
					}
				}
			}
			if (player.hasPermission("miner.upgrade.blocks.1")) {
				for (Mine mine : getMRL().mines) {
					if (mine.getName().equalsIgnoreCase("upgradeone")) {
						return mine;
					}
				}
			}
			
			for (Mine mine : getMRL().mines) {
				if (mine.getName().equalsIgnoreCase("noupgrade")) {
					return mine;
				}
			}
		}
		return null;
	}

	public long timeLeft(Player player) {
		return this.getConfig().getLong("data." + player.getUniqueId().toString() + ".time_left");
	}

	public void subtractTime(Player player, long amount) {
		this.getConfig().set("data." + player.getUniqueId().toString() + ".time_left", this.timeLeft(player) - amount);
		this.saveConfig();
	}

	public void addMine(String name, Location teleport, Location max, Location min) {
		this.getConfig().set("miner_areas." + name + ".name", name);
		this.getConfig().set("miner_areas." + name + ".tp_location", teleport.getWorld().getName() + ","
				+ teleport.getBlockX() + "," + teleport.getBlockY() + "," + teleport.getBlockZ());
		this.getConfig().set("miner_areas." + name + ".max",
				max.getWorld().getName() + "," + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ());
		this.getConfig().set("miner_areas." + name + ".min",
				min.getWorld().getName() + "," + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ());
		this.mines.add(new MinerArea(name, teleport, max, min, null, this));
		this.saveConfig();
	}

	public void onDisable() {
		for (MinerArea mine : mines) {
			if (mine.getPlayer() != null) {
				mine.getPlayer().teleport(this.serverSpawn);
			}
		}
	}

	public boolean isMining(Player player) {
		for (MinerArea mine : mines) {
			if (player.equals(mine.getPlayer())) {
				return true;
			}
		}
		return false;
	}

	public boolean mineExsists(String string) {
		for (MinerArea mine : mines) {
			if (mine.getName().equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}

	public MineResetLite getMRL() {
		if (this.getServer().getPluginManager().getPlugin("MineResetLite").isEnabled()) {
			Plugin plugin = this.getServer().getPluginManager().getPlugin("MineResetLite");
			if (plugin instanceof MineResetLite) {
				return (MineResetLite) plugin;
			}
		}
		return null;
	}

	public WorldEditPlugin getWorldEdit() {
		if (this.getServer().getPluginManager().getPlugin("WorldEdit").isEnabled()) {
			Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldEdit");
			if (plugin instanceof WorldEditPlugin) {
				return (WorldEditPlugin) plugin;
			}
		}
		return null;
	}

	public ArrayList<MinerArea> getMines() {
		return mines;
	}

	public void setMines(ArrayList<MinerArea> mines) {
		this.mines = mines;
	}

	public Location getServerSpawn() {
		return serverSpawn;
	}

	public void setServerSpawn(Location serverSpawn) {
		this.serverSpawn = serverSpawn;
	}

	public MinerManager getMineManager() {
		return mineManager;
	}

	public void setMineManager(MinerManager mineManager) {
		this.mineManager = mineManager;
	}

}
