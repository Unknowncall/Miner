package net.pixelcade.miner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class LeaveListener implements Listener {

	private MinerDriver plugin;

	public LeaveListener(MinerDriver minerDriver) {
		this.plugin = minerDriver;
		this.redeeming = new ArrayList<Player>();
	}

	@EventHandler
	public void cmdEvent(PlayerCommandPreprocessEvent event) {
		if (this.plugin.isMining(event.getPlayer())) {
			if (!event.getPlayer().isOp()) {
				List<String> whitelistedCommands = new ArrayList<String>();
				whitelistedCommands.add("/msg");
				whitelistedCommands.add("/miner");
				whitelistedCommands.add("/pay");
				whitelistedCommands.add("/ec");
				whitelistedCommands.add("/enderchest");
				whitelistedCommands.add("/as");
				whitelistedCommands.add("/autosell");
				whitelistedCommands.add("/sell");
				whitelistedCommands.add("/sellall");
				whitelistedCommands.add("/te");
				whitelistedCommands.add("/tokens");
				whitelistedCommands.add("/enchant");
				whitelistedCommands.add("/fly");
				whitelistedCommands.add("/baltop");
				whitelistedCommands.add("/lb");
				whitelistedCommands.add("/rankup");
				whitelistedCommands.add("/maxrankup");
				whitelistedCommands.add("/ru");
				whitelistedCommands.add("balance");
				whitelistedCommands.add("/bal");
				whitelistedCommands.add("/money");
				whitelistedCommands.add("/vote");
				whitelistedCommands.add("/voteparty");
				whitelistedCommands.add("/reply");
				whitelistedCommands.add("/r");
				whitelistedCommands.add("/whisper");
				whitelistedCommands.add("/w");
				whitelistedCommands.add("/tell");
				whitelistedCommands.add("/message");
				whitelistedCommands.add("/echest");
				whitelistedCommands.add("/topminer");
				whitelistedCommands.add("/keys");
				whitelistedCommands.add("/nick");
				whitelistedCommands.add("/refund");
				whitelistedCommands.add("/tokenpay");
				whitelistedCommands.add("/tokensend");
				whitelistedCommands.add("/ignore");
				whitelistedCommands.add("/tokenshop");
				if (!(whitelistedCommands.contains(event.getMessage().split(" ")[0].toLowerCase()))) {
					event.getPlayer().sendMessage(
							ChatColor.RED + "You can't use that command in the miner. To leave type /miner leave");
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void leaveEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		for (MinerArea mine : this.plugin.mines) {
			if (mine.getPlayer() != null) {
				if (mine.getPlayer().equals(player)) {
					mine.removePlayer();
					player.teleport(this.plugin.getServerSpawn());
					return;
				}
			}
		}
	}

	@EventHandler
	public void kickEvent(PlayerKickEvent event) {
		Player player = event.getPlayer();
		for (MinerArea mine : this.plugin.mines) {
			if (mine.getPlayer() != null) {
				if (mine.getPlayer().equals(player)) {
					mine.removePlayer();
					player.teleport(this.plugin.getServerSpawn());
					return;
				}
			}
		}
	}

	private ArrayList<Player> redeeming;

	@EventHandler
	public void interactEvent(PlayerInteractEvent event) {
		if (event.getPlayer().getItemInHand() == null) {
			return;
		}
		if (this.redeeming.contains(event.getPlayer())) {
			return;
		}
		if (event.getAction() == null) {
			return;
		}
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK
				|| event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			ItemStack is = event.getPlayer().getItemInHand();
			if (is == null) {
				return;
			}
			if (is.getItemMeta().getLore() == null) {
				return;
			}
			if (event.getPlayer().isSneaking()) {
				redeeming.add(event.getPlayer());
				int voucherSlot = event.getPlayer().getInventory().getHeldItemSlot();
				new BukkitRunnable() {

					boolean done = false;

					@Override
					public void run() {
						if (done) {
							redeeming.remove(event.getPlayer());
							this.cancel();
						}
						if (plugin.timeLeft(event.getPlayer()) <= -1) {
							event.getPlayer().sendMessage(ChatColor.RED + "An error has occured. Please contact an admin with this information. AM-001");
							plugin.getConfig().set("data." + player.getUniqueId().toString() + ".time_left", 0);
							plugin.saveConfig();
							this.cancel();
							return;
						}
						for (String s : is.getItemMeta().getLore()) {
							if (s.contains("Minute(s)") || s.contains("Hour(s)")) {
								s = ChatColor.stripColor(s);
								String[] spilt = s.split(" ");
								for (int i = 0; i < spilt.length; i++) {
									if (spilt[i].equals("Minute(s)")) {
										long time = TimeUnit.MINUTES.toMillis(Integer.parseInt(spilt[i - 1]));
										plugin.getConfig().set("data." + player.getUniqueId().toString() + ".time_left",
												(time + plugin.getConfig().getLong(
														"data." + player.getUniqueId().toString() + ".time_left")));
										plugin.saveConfig();
										player.sendMessage(ChatColor.GREEN
												+ "Miner token credited to your account. To use your time type /miner or /miner timeleft.");
										{
											if (is.getAmount() > 1) {
												is.setAmount(is.getAmount() - 1);
												event.getPlayer().getInventory().setItem(voucherSlot, is);
											} else {
												done = true;
												player.getInventory()
														.remove(player.getInventory().getItem(voucherSlot));
											}
										}
									} else if (spilt[i].equalsIgnoreCase("Hour(s)")) {
										long time = TimeUnit.HOURS.toMillis(Integer.parseInt(spilt[i - 1]));
										plugin.getConfig().set("data." + player.getUniqueId().toString() + ".time_left",
												(time + plugin.getConfig().getLong(
														"data." + player.getUniqueId().toString() + ".time_left")));
										plugin.saveConfig();
										player.sendMessage(ChatColor.GREEN
												+ "Miner token credited to your account. To use your time type /miner or /miner timeleft.");
										if (is.getAmount() > 1) {
											is.setAmount(is.getAmount() - 1);
											event.getPlayer().getInventory().setItem(voucherSlot, is);
										} else {
											done = true;
											player.getInventory().remove(player.getInventory().getItem(voucherSlot));
										}
									}
								}
							}
						}

					}

				}.runTaskTimer(plugin, 0, 0);

			} else {
				if (plugin.timeLeft(event.getPlayer()) <= -1) {
					event.getPlayer().sendMessage(ChatColor.RED + "An error has occured. Please contact an admin with this information. AM-001");
					plugin.getConfig().set("data." + player.getUniqueId().toString() + ".time_left", 0);
					plugin.saveConfig();
					return;
				}
				for (String s : is.getItemMeta().getLore()) {
					if (s.contains("Minute(s)") || s.contains("Hour(s)")) {
						s = ChatColor.stripColor(s);
						String[] spilt = s.split(" ");
						for (int i = 0; i < spilt.length; i++) {
							if (spilt[i].equals("Minute(s)")) {
								long time = TimeUnit.MINUTES.toMillis(Integer.parseInt(spilt[i - 1]));
								this.plugin.getConfig().set("data." + player.getUniqueId().toString() + ".time_left",
										(time + this.plugin.getConfig()
												.getLong("data." + player.getUniqueId().toString() + ".time_left")));
								this.plugin.saveConfig();
								player.sendMessage(ChatColor.GREEN
										+ "Miner token credited to your account. To use your time type /miner or /miner timeleft.");
								{
									if (is.getAmount() > 1) {
										is.setAmount(is.getAmount() - 1);
										event.getPlayer().getInventory().setItemInHand(is);
									} else {
										player.getInventory().remove(is);
									}
								}
							} else if (spilt[i].equalsIgnoreCase("Hour(s)")) {
								long time = TimeUnit.HOURS.toMillis(Integer.parseInt(spilt[i - 1]));
								this.plugin.getConfig().set("data." + player.getUniqueId().toString() + ".time_left",
										(time + this.plugin.getConfig()
												.getLong("data." + player.getUniqueId().toString() + ".time_left")));
								this.plugin.saveConfig();
								player.sendMessage(ChatColor.GREEN
										+ "Miner token credited to your account. To use your time type /miner or /miner timeleft.");
								if (is.getAmount() > 1) {
									is.setAmount(is.getAmount() - 1);
									event.getPlayer().getInventory().setItemInHand(is);
								} else {
									player.getInventory().remove(is);
								}
							}
						}
					}
				}
			}
		}
	}

}
