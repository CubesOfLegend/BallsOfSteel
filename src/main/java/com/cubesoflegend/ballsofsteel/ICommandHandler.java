package com.cubesoflegend.ballsofsteel;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.commands.CommandHandler;
import com.comze_instancelabs.minigamesapi.config.ArenasConfig;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.cubesoflegend.ballsofsteel.utils.ColorUtils;

public class ICommandHandler extends CommandHandler {

    @Override
    public boolean setSpawn(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd,
            String action, JavaPlugin plugin, Player p) {
        if (!sender.hasPermission(uber_permission + ".setup")) {
            sender.sendMessage(pli.getMessagesConfig().no_perm);
            return true;
        }
        if (args.length > 2) {
            String team = args[2].toLowerCase();
            if (!ColorUtils.bimapColorChatColor.containsKey(team)) {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]"
                        + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> <team>");
                sender.sendMessage(
                        ChatColor.RED + "Teams possible: Bleu, Rouge, Jaune, Vert, Rose, Violet, Orange, Noir, Blanc");
                return true;
            }
            Util.saveComponentForArena(plugin, args[1], "spawns.spawn" + team.toLowerCase(), p.getLocation());
            sender.sendMessage(
                    pli.getMessagesConfig().successfully_set.replaceAll("<component>", "spawn for team " + team));
        } else {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]"
                    + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> <team>");
        }
        return true;
    }

    @Override
    public boolean saveArena(PluginInstance pli, CommandSender sender, String[] args, String uber_permission,
        String cmd, String action, JavaPlugin plugin, Player p) {
        // Si le spawn0 de l'aréne n'existe pas, alors on le pose
        FileConfiguration config = MinigamesAPI.getAPI().getPluginInstance(plugin).getArenasConfig().getConfig();
        if (!config.isSet("arenas." + args[1] + ".spawns.spawn0")) {
            config.createSection("arenas." + args[1] + ".spawns.spawn0");
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
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]"
                    + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena>");
        }
        return true;
    }
    
    @Override
    public boolean removeSpawn(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd, String action, JavaPlugin plugin, Player p) {
        if (!sender.hasPermission(uber_permission + ".setup")) {
            sender.sendMessage(pli.getMessagesConfig().no_perm);
            return true;
        }
        if (args.length > 2) {
            String team = args[2].toLowerCase();
            if (!ColorUtils.bimapColorChatColor.containsKey(team)) {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]"
                        + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> <team>");
                sender.sendMessage(
                        ChatColor.RED + "Teams possible: Bleu, Rouge, Jaune, Vert, Rose, Violet, Orange, Noir, Blanc");
                return true;
            }
            else
            {
                ArenasConfig config = MinigamesAPI.getAPI().getPluginInstance(plugin).getArenasConfig();
                boolean ret = false;
                String path = "arenas." + args[1] + ".spawns.spawn" + team;
                if (config.getConfig().isSet(path)) {
                    config.getConfig().set(path, null);
                    config.saveConfig();
                    sender.sendMessage(pli.getMessagesConfig().successfully_removed.replaceAll("<component>", "spawn " + args[2]));
                    return true;
                }
                else
                {
                    sender.sendMessage(pli.getMessagesConfig().failed_removing_component.replaceAll("<component>", "spawn " + args[2]).replaceAll("<cause>", "Possibly the provided spawn couldn't be found: " + args[2]));
                    return true;
                }
            }
        } else {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> <team>");
        }
        return true;
    }
}
