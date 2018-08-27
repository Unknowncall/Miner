package net.pixelcade.miner;

import java.util.concurrent.TimeUnit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class MinerExecutor implements CommandExecutor {

	private MinerDriver plugin;

	public MinerExecutor(MinerDriver minerDriver) {
		this.plugin = minerDriver;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
			return true;
		}
		Player player = (Player) sender;

		if (args.length == 0) {
			if (this.plugin.isMining(player)) {
				player.sendMessage(ChatColor.RED + "You are already in a miner.");
				return true;
			}
			
			if (this.plugin.timeLeft(player) <= 0 && (!player.hasPermission("miner.perm"))) {
				player.sendMessage(ChatColor.RED + "You have no time left in the miner!");
				return true;
			}
			
			for (MinerArea mine : this.plugin.mines) {
				if (mine.getPlayer() == null) {
					mine.addPlayer(player);
					player.sendMessage(ChatColor.YELLOW + "You have been teleported to an auto mining area.");
					return true;
				}
			}
			player.sendMessage(ChatColor.RED + "All mines are currently occupied.");
			return true;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("leave")) {
				for (MinerArea mine : this.plugin.mines) {
					if (mine.getPlayer() != null) {
						if (mine.getPlayer().equals(player)) {
							mine.removePlayer();
							player.teleport(this.plugin.getServerSpawn());
							player.sendMessage(ChatColor.YELLOW + "Leaving the miner...");
							return true;
						}
					}
				}
				player.sendMessage(ChatColor.RED + "You are not currently in a mine area.");
				return true;
			}
			if (args[0].equalsIgnoreCase("timeleft")) {
				if (player.hasPermission("miner.perm")) {
					player.sendMessage(ChatColor.GREEN + "You have unlimited time remaining in the miner.");
					return true;
				} else {
					player.sendMessage(ChatColor.GREEN + "You have " + TimeUnit.MILLISECONDS.toMinutes(this.plugin.timeLeft(player)) + " minutes(s) remaining in the autominer.");
					return true;
				}
			}
		}

		player.sendMessage(ChatColor.RED + "Wrong usage. /miner or /miner leave");
		return true;
	}

}
