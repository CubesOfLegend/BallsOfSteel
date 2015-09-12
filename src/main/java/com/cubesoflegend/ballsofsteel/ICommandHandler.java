package com.cubesoflegend.ballsofsteel;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.commands.CommandHandler;
import com.comze_instancelabs.minigamesapi.util.Util;

public class ICommandHandler extends CommandHandler {
	
	@Override
	public boolean setSpawn(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd, String action, JavaPlugin plugin, Player p){
		Bukkit.getLogger().info("Je passe dans ICommandHandler");
		if (!sender.hasPermission(uber_permission + ".setup")) {
			sender.sendMessage(pli.getMessagesConfig().no_perm);
			return true;
		}
		if (args.length > 2) {
			String team = args[2];
			if (!ArrayUtils.contains(Constants.TeamColors, team)) {
				sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> <team>");
				sender.sendMessage(ChatColor.RED + "Teams possible: Bleu, Rouge, Jaune, Vert, Rose, Violet, Orange, Noir, Blanc");
				return true;
			}
			//pli.arenaSetup.autoSetSpawn(plugin, args[1], p.getLocation());
			Util.saveComponentForArena(plugin, args[1], "spawns.spawn" + team.toLowerCase(), p.getLocation());
			sender.sendMessage(pli.getMessagesConfig().successfully_set.replaceAll("<component>", "spawn for team " + team));
		} else {
			sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> <team>");
		}
		return true;
	}
	
	@Override
	public boolean saveArena(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd, String action, JavaPlugin plugin, Player p) {
		//Si le spawn0 de l'arène n'existe pas, alors on le pose
		FileConfiguration config = MinigamesAPI.getAPI().getPluginInstance(plugin).getArenasConfig().getConfig();
		if(!config.isSet("arenas." + args[1] + ".spawns.spawn0")){
			Bukkit.getLogger().info("spawn0 non mis");
			config.createSection("arenas." + args[1] + ".spawns.spawn0");
		}
		else
		{
			Bukkit.getLogger().info("spawn0 existant");
		}
		if (!sender.hasPermission(uber_permission + ".setup")) {
			sender.sendMessage(pli.getMessagesConfig().no_perm);
			return true;
		}
		if (args.length > 1) {
			
			Arena temp = pli.arenaSetup.saveArena(plugin, args[1]);
			if (temp != null) {
				sender.sendMessage(pli.getMessagesConfig().successfully_saved_arena.replaceAll("<arena>", args[1]));
			} else {
				sender.sendMessage(pli.getMessagesConfig().failed_saving_arena.replaceAll("<arena>", args[1]));
			}
		} else {
			sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena>");
		}
		return true;
	}
}
