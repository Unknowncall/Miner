package net.pixelcade.miner;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import com.koletar.jj.mineresetlite.Mine;

public class MinerArea extends Mine {

	private String name;
	private Location teleportLocation;
	private Location max;
	private Location min;
	private Player player;
	private MinerDriver plugin;

	public MinerArea(String name, Location teleportLocation, Location max, Location min, Player player,
			MinerDriver plugin) {
		super(max.getBlockX(), max.getBlockY(), max.getBlockZ(), min.getBlockX(), min.getBlockY(), min.getBlockZ(),
				name, teleportLocation.getWorld());
		this.name = name;
		this.teleportLocation = teleportLocation;
		this.max = max;
		this.min = min;
		this.player = player;
		this.plugin = plugin;
	}

	private long lastBlockBreak;

	@SuppressWarnings("deprecation")
	public void reset() {
		if (isInside(player)) {
			player.teleport(this.getTeleportLocation());
		}

		Random rand = new Random();
		for (int x = super.getMinX(); x <= super.getMaxX(); ++x) {
			for (int y = super.getMinY(); y <= super.getMaxY(); ++y) {
				for (int z = getMinZ(); z <= super.getMaxZ(); ++z) {
					if (!super.getFillMode() || super.getWorld().getBlockTypeIdAt(x, y, z) == 0) {
						if (y == super.getMaxY() && super.getSurface() != null) {
							super.getWorld().getBlockAt(x, y, z).setTypeIdAndData(super.getSurface().getBlockId(),
									super.getSurface().getData(), false);
							continue;
						}
						double r = rand.nextDouble();
						List<CompositionEntry> probabilityMap = mapComposition(
								this.plugin.getMine(player).getComposition());
						for (CompositionEntry ce : probabilityMap) {
							if (r <= ce.getChance()) {
								super.getWorld().getBlockAt(x, y, z).setTypeIdAndData(ce.getBlock().getBlockId(),
										ce.getBlock().getData(), false);
								break;
							}
						}
					}
				}
			}
		}
	}

	public long getLastBlockBreak() {
		return lastBlockBreak;
	}

	public void setLastBlockBreak(long lastBlockBreak) {
		this.lastBlockBreak = lastBlockBreak;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public void addPlayer(Player player) {
		this.setLastBlockBreak(System.currentTimeMillis());
		this.setPlayer(player);
		this.player.teleport(this.teleportLocation);
		this.reset();
		this.lastXMined = super.getMaxX();
		this.lastYMined = super.getMaxY();
		this.lastZMined = super.getMaxZ();
	}

	boolean isDone = false;

	public void removePlayer() {
		this.isDone = true;
		this.setPlayer(null);
	}

	int lastXMined = 0;
	int lastYMined = 0;
	int lastZMined = 0;

	public void mine() {
		for (int y = lastYMined; y >= getMinY(); --y) {
			for (int x = lastXMined; x >= getMinX(); --x) {
				for (int z = lastZMined; z >= getMinZ(); --z) {
					if (getWorld().getBlockAt(new Location(getWorld(), x, y, z)).getType() != Material.AIR) {
						Block block = getWorld().getBlockAt(new Location(getWorld(), x, y, z));
						BlockBreakEvent event = new BlockBreakEvent(block, player);
						plugin.getServer().getPluginManager().callEvent(event);
						this.lastXMined = x;
						this.lastYMined = y;
						this.lastZMined = z;
						y = getMinY();
						x = getMinX();
						z = getMinZ();
					} else if (getWorld().getBlockAt(new Location(getWorld(), x, y, z)).getType() == Material.AIR
							&& x == getMinX() && y == getMinY() && z == getMinZ()) {
						reset();

					}
				}
			}
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getTeleportLocation() {
		return teleportLocation;
	}

	public void setTeleportLocation(Location teleportLocation) {
		this.teleportLocation = teleportLocation;
	}

	public Location getMax() {
		return max;
	}

	public void setMax(Location max) {
		this.max = max;
	}

	public Location getMin() {
		return min;
	}

	public void setMin(Location min) {
		this.min = min;
	}

	public MinerDriver getPlugin() {
		return plugin;
	}

	public void setPlugin(MinerDriver plugin) {
		this.plugin = plugin;
	}

	public int getLastXMined() {
		return lastXMined;
	}

	public void setLastXMined(int lastXMined) {
		this.lastXMined = lastXMined;
	}

	public int getLastYMined() {
		return lastYMined;
	}

	public void setLastYMined(int lastYMined) {
		this.lastYMined = lastYMined;
	}

	public int getLastZMined() {
		return lastZMined;
	}

	public void setLastZMined(int lastZMined) {
		this.lastZMined = lastZMined;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}