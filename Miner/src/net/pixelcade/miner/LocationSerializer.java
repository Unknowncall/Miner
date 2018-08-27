package net.pixelcade.miner;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializer {

	public static String serialize(Location location) {
		return location.getWorld() + "," + location.getBlockX() + "," + location.getBlockY() + ","
				+ location.getBlockZ() + "," + location.getYaw() + "," + location.getPitch();
	}

	public static Location deserialize(String string) {
		String[] spilt = string.split(",");
		if (spilt.length == 4) {
			String world = spilt[0];
			double blockX = Double.parseDouble(spilt[1]);
			double blockY = Double.parseDouble(spilt[2]);
			double blockZ = Double.parseDouble(spilt[3]);
			return new Location(Bukkit.getWorld(world), blockX, blockY, blockZ);
		} else if (spilt.length == 6) {
			String world = spilt[0];
			double blockX = Double.parseDouble(spilt[1]);
			double blockY = Double.parseDouble(spilt[2]);
			double blockZ = Double.parseDouble(spilt[3]);
			float yaw = Float.parseFloat(spilt[4]);
			float pitch = Float.parseFloat(spilt[5]);
			return new Location(Bukkit.getWorld(world), blockX, blockY, blockZ, yaw, pitch);
		}

		return null;
	}

}
