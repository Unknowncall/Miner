package net.pixelcade.miner;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.bukkit.selections.Selection;

import net.md_5.bungee.api.ChatColor;

public class MinerAdminExecutor implements CommandExecutor {

	private MinerDriver plugin;

	public MinerAdminExecutor(MinerDriver minerDriver) {
		this.plugin = minerDriver;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender.hasPermission("mineradmin.admin"))) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
			return true;
		}
		
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("give")) {
				if (Bukkit.getPlayer(args[1]) == null) {
					sender.sendMessage(ChatColor.RED + "That player is not online.");
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);

				try {
					int time = Integer.parseInt(args[2]);
					String serializedItem = this.plugin.getConfig().getString("miner_item");
					if (time > 60) {
						serializedItem = serializedItem.replace("[time]", TimeUnit.MINUTES.toHours(time) + "_Hour(s)");
					} else {
						serializedItem = serializedItem.replace("[time]", time + "_Minute(s)");
					}
					ItemStack is = ItemStackSerializer.deserialize(serializedItem);
					target.getInventory().addItem(is);
					sender.sendMessage(ChatColor.GREEN + "You have given a miner token to " + target.getName() + " for "
							+ time + " minute(s).");
					target.sendMessage(ChatColor.GREEN + "You have received a miner token for " + time + " minute(s).");
					return true;
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "You must specify a number.");
					return true;
				}

			}
			sender.sendMessage(ChatColor.RED + "Wrong usage!");
			return true;
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("create")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
					return true;
				}

				Player player = (Player) sender;

				if (this.plugin.mineExsists(args[1])) {
					player.sendMessage(ChatColor.RED + "A mine with that name already exsists.");
					return true;
				} else {
					Selection sel = this.plugin.getWorldEdit().getSelection(player);
					if (sel == null) {
						player.sendMessage(ChatColor.RED + "You must make a WorldEdit Selection first");
						return true;
					}
					Location p1 = sel.getMaximumPoint();
					Location p2 = sel.getMinimumPoint();
					// Sort coordinates
					if (p1.getX() > p2.getX()) {
						double x = p1.getX();
						p1.setX(p2.getX());
						p2.setX(x);
					}
					if (p1.getY() > p2.getY()) {
						double y = p1.getY();
						p1.setY(p2.getY());
						p2.setY(y);
					}
					if (p1.getZ() > p2.getZ()) {
						double z = p1.getZ();
						p1.setZ(p2.getZ());
						p2.setZ(z);
					}
					this.plugin.addMine(args[1], player.getLocation(), p1, p2);
					player.chat("/rg d " + args[1]);
					player.chat("/rg flag " + args[1] + " block-break allow");
					player.sendMessage(ChatColor.GREEN + "Miner has been created!");
					return true;
				}
			}
		}
		return true;
	}

}
