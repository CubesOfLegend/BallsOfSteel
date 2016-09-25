package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.commands.CommandHandler;
import com.comze_instancelabs.minigamesapi.config.ArenasConfig;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.cubesoflegend.ballsofsteel.utils.ColorUtils;
import com.cubesoflegend.ballsofsteel.utils.Validator;

public class ICommandHandler extends CommandHandler {

    Main m;
    Validator validator;
    public static LinkedHashMap<String, String> cmddesc;
    static {
        cmddesc = new LinkedHashMap<String, String>();
        cmddesc.put("", "");
        cmddesc.put("setspawn <arena> <team>", "Sets the team spawn point. (One spawn, one team)");
        cmddesc.put("setlobby <arena>", "Sets the lobby point.");
        cmddesc.put("setmainlobby", "Sets the main lobby point.");
        cmddesc.put("setbounds <arena> <low/high>", "Sets the low or high boundary point for later arena regeneration.");
        cmddesc.put("setteambounds <arena> <team> <low/high>", "Sets the low or high boundary point for team base");
        cmddesc.put("setdepotbounds <arena> <team> <low/high>", "Sets the low or high boundary point for team depot");
        cmddesc.put("setcenterbounds <arena> <low/high>", "Sets the low or high boundary point for center arena");
        cmddesc.put("savearena <arena>", "Saves the arena.");
        cmddesc.put(" ", "");
        cmddesc.put("setmaxplayers <arena> <count>", "Sets the max players allowed to join to given count.");
        cmddesc.put("setminplayers <arena> <count>", "Sets the min players needed to start to given count.");
        cmddesc.put("setteammaxplayers <arena> <team> <count>", "Sets the max players allowed to join team to given count.");
        cmddesc.put("setteamminplayers <arena> <team> <count>", "Sets the min players needed in team to given count.");
        cmddesc.put("setitemcollect <arena> <team> [itemid:itemsubid]", "Sets the item to collect for a team in arena (If not specified set item in hand).");
        cmddesc.put("setarenavip <arena> <true/false>", "Sets whether arena needs permission to join.");
        cmddesc.put("removearena <arena>", "Deletes an arena from config.");
        cmddesc.put("removespawn <arena> <count>", "Deletes a spawn from config.");
        cmddesc.put("setenabled", "Enables/Disables the arena.");
        cmddesc.put("join <arena>", "Joins the arena.");
        cmddesc.put("leave", "Leaves the arena.");
        cmddesc.put("start <arena>", "Forces the arena to start.");
        cmddesc.put("stop <arena>", "Forces the arena to stop.");
        cmddesc.put("list", "Lists all arenas.");
        cmddesc.put("reload", "Reloads the config.");
        cmddesc.put("reset <arena>", "Forces the arena to reset.");
        cmddesc.put("setlobbybounds <arena> <low/high>", "Optional: Set lobby boundaries.");
        cmddesc.put("setspecbounds <arena> <low/high>", "Optional: Set extra spectator boundaries.");
        cmddesc.put("setauthor <arena> <author>", "Will always display the author of the map at join.");
        cmddesc.put("setdescription <arena> <description>", "Will always display a description of the map at join.");
        cmddesc.put("setdisplayname <arena> <displayname>", "Allows changing displayname of an arena (whitespaces and colors).");
        cmddesc.put("setdisplayname <arena> <displayname>", "Allows changing displayname of an arena (whitespaces and colors).");
    }

    public ICommandHandler(Main m) {
        this.m = m;
        this.validator = new Validator(this.m.pli);
    }

    @Override
    public boolean handleArgs(JavaPlugin plugin, String uber_permission, String cmd, CommandSender sender,
            String args[]) {
        if (args.length > 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Please execute this command ingame.");
                return true;
            }
            Player p = (Player) sender;
            PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(plugin);
            String action = args[0];
            if (action.equalsIgnoreCase("setspawn")) {
                return this.setSpawn(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setspecspawn")) {
                return this.setSpecSpawn(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setlobby")) {
                return this.setLobby(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setmainlobby")) {
                return this.setMainLobby(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setbounds")) {
                return this.setBounds(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setlobbybounds")) {
                return this.setLobbyBounds(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setspecbounds")) {
                return this.setSpecBounds(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("savearena") || action.equalsIgnoreCase("save")) {
                return this.saveArena(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setmaxplayers")) {
                return this.setMaxPlayers(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setminplayers")) {
                return this.setMinPlayers(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setarenavip") || action.equalsIgnoreCase("setvip")) {
                return this.setArenaVIP(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("join")) {
                return this.joinArena(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("leave")) {
                return this.leaveArena(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("start")) {
                return this.startArena(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("stop")) {
                return this.stopArena(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("stopall")) {
                return this.stopAllArenas(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("removearena")) {
                return this.removeArena(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("removespawn")) {
                return this.removeSpawn(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setskull")) {
                return this.setSkull(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setenabled")) {
                return this.setEnabled(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setshowscoreboard")) {
                return this.setShowScoreboard(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("reset")) {
                return this.resetArena(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setauthor")) {
                return this.setAuthor(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setdescription")) {
                return this.setDescription(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setdisplayname")) {
                return this.setArenaDisplayName(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("kit")) {
                return this.setKit(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("spectate")) {
                return this.spectate(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("shop")) {
                return this.openShop(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("leaderboards") || action.equalsIgnoreCase("lb")) {
                return this.getLeaderboards(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("stats")) {
                return this.getStats(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setteambounds")) {
                return this.setTeamBounds(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setdepotbounds")) {
                return this.setDepotBounds(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setcenterbounds")) {
                return this.setCenterBounds(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setteamminplayers")) {
                return this.setTeamMinPlayers(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setteammaxplayers")) {
                return this.setTeamMaxPlayers(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("setitemcollect")) {
                return this.setItemCollect(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase("help")) {
                sendHelp(cmd, sender);
            } else if (action.equalsIgnoreCase("list")) {
                sender.sendMessage(ChatColor.DARK_GRAY + "------- " + ChatColor.BLUE + "Arenas" + ChatColor.DARK_GRAY
                        + " -------");
                for (Arena a : pli.getArenas()) {
                    if (args.length > 1) {
                        sender.sendMessage(ChatColor.GREEN + a.getInternalName() + "["
                                + a.getClass().getSimpleName().toString() + "]");
                    } else {
                        sender.sendMessage(ChatColor.GREEN + a.getInternalName());
                    }
                }
            } else if (action.equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                pli.getMessagesConfig().reloadConfig();
                pli.getArenasConfig().reloadConfig();
                pli.getClassesConfig().reloadConfig();
                pli.getAchievementsConfig().reloadConfig();
                pli.getStatsConfig().reloadConfig();
                pli.getShopConfig().reloadConfig();
                pli.getMessagesConfig().init();
                pli.reloadVariables();
                pli.getRewardsInstance().reloadVariables();
                try {
                    pli.reloadAllArenas();
                } catch (Exception e) {
                    System.out.println("Looks like one arena is invalid, but most arenas should be reloaded just fine. "
                            + e.getMessage());
                }
                sender.sendMessage(pli.getMessagesConfig().successfully_reloaded);
            } else {
                boolean cont = false;
                ArrayList<String> cmds = new ArrayList<String>();
                for (String cmd_ : cmddesc.keySet()) {
                    if (cmd_.toLowerCase().contains(action.toLowerCase())) {
                        cmds.add(cmd_);
                        cont = true;
                    }
                }
                if (cont) {
                    sendHelp(cmd, sender);
                    for (String cmd_ : cmds) {
                        sender.sendMessage(ChatColor.RED + "Did you mean " + ChatColor.DARK_RED + cmd + " " + cmd_
                                + ChatColor.RED + "?");
                    }
                }
            }
        } else {
            this.sendHelp(cmd, sender);
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
        if (args.length == 2) {
            
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
    public boolean setSpawn(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd,
            String action, JavaPlugin plugin, Player p) {

        if (sender.hasPermission(uber_permission + ".setup")) {
            if (args.length > 2) {
                String team = args[2].toLowerCase();
                if (validator.isValidTeamArgument(team)) {
                    Util.saveComponentForArena(plugin, args[1], "spawns.spawn" + team, p.getLocation());
                    sender.sendMessage(m.im.successfully_set_team_component.replaceAll("<component>", "spawn").replaceAll("<team>", ColorUtils.bimapColorChatColor.get(team) + team));
                } else {
                    sender.sendMessage(m.im.team_not_exists.replaceAll("<team>", team));
                    sender.sendMessage(m.im.possible_teams_are.replaceAll("<teams>","Bleu, Rouge, Jaune, Vert, Rose, Violet, Orange, Noir, Blanc"));
                }
            } else {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> [Bleu/Rouge/Jaune/Vert/Rose/Violet/Orange/Noir/Blanc]");
            }
        } else {
            sender.sendMessage(pli.getMessagesConfig().no_perm);
        }
        return true;
    }


    @Override
    public boolean removeSpawn(PluginInstance pli, CommandSender sender, String[] args, String uber_permission,
            String cmd, String action, JavaPlugin plugin, Player p) {
        if (sender.hasPermission(uber_permission + ".setup")) {
            if (args.length == 3) {
                String team = args[2].toLowerCase();
                if (validator.isValidTeamArgument(team)) {

                    ArenasConfig config = MinigamesAPI.getAPI().getPluginInstance(plugin).getArenasConfig();
                    boolean ret = false;
                    String path = "arenas." + args[1] + ".spawns.spawn" + team;
                    if (config.getConfig().isSet(path)) {
                        config.getConfig().set(path, null);
                        config.saveConfig();
                        sender.sendMessage(pli.getMessagesConfig().successfully_removed.replaceAll("<component>",
                                "spawn " + ColorUtils.bimapColorChatColor.get(team) + team));
                    } else {
                        sender.sendMessage(pli.getMessagesConfig().failed_removing_component
                                .replaceAll("<component>", "spawn " + args[2])
                                .replaceAll("<cause>", "Possibly the provided spawn couldn't be found: " + args[2]));
                    }
                } else {
                    sender.sendMessage(m.im.team_not_exists.replaceAll("<team>", team));
                    sender.sendMessage(m.im.possible_teams_are.replaceAll("<teams>",
                            "Bleu, Rouge, Jaune, Vert, Rose, Violet, Orange, Noir, Blanc"));
                }
            } else {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]"
                        + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> [Bleu/Rouge/Jaune/Vert/Rose/Violet/Orange/Noir/Blanc]");
            }
        } else {
            sender.sendMessage(pli.getMessagesConfig().no_perm);
        }
        return true;
    }

    public boolean setTeamBounds(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd, String action, JavaPlugin plugin, Player p) {
        Player player = Bukkit.getPlayer(sender.getName());
        if (sender.hasPermission(uber_permission + ".setup")) {
            if (args.length == 4 && (args[3].equalsIgnoreCase("low") || args[3].equalsIgnoreCase("high"))) {
                    if (validator.spawnExist(args[1] , args[2].toLowerCase())) {
                        if(args[3].equalsIgnoreCase("low")){
                            pli.arenaSetup.setBoundaries(plugin, args[1], player.getLocation(), true, "spawns.spawn"+args[2].toLowerCase());
                            sender.sendMessage(m.im.successfully_set_team_component.replaceAll("<team>", args[2])
                                    .replaceAll("<component>", " low teambase bounds"));
                        }
                        else if(args[3].equalsIgnoreCase("high"))
                        {
                            pli.arenaSetup.setBoundaries(plugin, args[1], player.getLocation(), false, "spawns.spawn"+args[2].toLowerCase());
                            sender.sendMessage(m.im.successfully_set_team_component.replaceAll("<team>", args[2])
                                    .replaceAll("<component>", " high teambase bounds"));
                        }
                    } else {
                        sender.sendMessage("The spawn " + args[2] + " doesn't exists");
                    }
            } else {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]"
                        + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> [Bleu/Rouge/Jaune/Vert/Rose/Violet/Orange/Noir/Blanc] [low/high]");
            }
        } else {
            sender.sendMessage(pli.getMessagesConfig().no_perm);
        }
        return true;
    }
    
    public boolean setDepotBounds(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd, String action, JavaPlugin plugin, Player p){
        
        Player player = Bukkit.getPlayer(sender.getName());
        
        if (sender.hasPermission(uber_permission + ".setup")) {
            if (args.length == 4 && (args[3].equalsIgnoreCase("low") || args[3].equalsIgnoreCase("high"))) {
                    if (validator.spawnExist(args[1] , args[2].toLowerCase())) {
                        if(args[3].equalsIgnoreCase("low")){
                            pli.arenaSetup.setBoundaries(plugin, args[1], player.getLocation(), true, "spawns.spawn"+args[2].toLowerCase()+".depot");
                            sender.sendMessage(m.im.successfully_set_team_component.replaceAll("<team>", args[2])
                                    .replaceAll("<component>", " low depot team bounds"));
                        }
                        else if(args[3].equalsIgnoreCase("high"))
                        {
                            pli.arenaSetup.setBoundaries(plugin, args[1], player.getLocation(), false, "spawns.spawn"+args[2].toLowerCase()+".depot");
                            sender.sendMessage(m.im.successfully_set_team_component.replaceAll("<team>", args[2])
                                    .replaceAll("<component>", " high depot team bounds"));
                        }
                    } else {
                        sender.sendMessage("The spawn " + args[2] + "doesn't exists");
                    }
            } else {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]"
                        + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> [Bleu/Rouge/Jaune/Vert/Rose/Violet/Orange/Noir/Blanc] [low/high]");
            }
        } else {
            sender.sendMessage(pli.getMessagesConfig().no_perm);
        }
        return true;
    }
    
    public boolean setCenterBounds(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd, String action, JavaPlugin plugin, Player p){
        
        Player player = Bukkit.getPlayer(sender.getName());
        
        if (sender.hasPermission(uber_permission + ".setup")) {
            if (args.length == 3 && (args[2].equalsIgnoreCase("low") || args[2].equalsIgnoreCase("high"))) {
                if(args[2].equalsIgnoreCase("low")){
                    pli.arenaSetup.setBoundaries(plugin, args[1], player.getLocation(), true, "center");
                    sender.sendMessage(m.im.successfully_set.replaceAll("<component>", " low center arena bounds"));
                }
                else if(args[2].equalsIgnoreCase("high"))
                {
                    pli.arenaSetup.setBoundaries(plugin, args[1], player.getLocation(), false, "center");
                    sender.sendMessage(m.im.successfully_set.replaceAll("<component>", " high center arena bounds"));
                }
            } else {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]"
                        + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> [low/high]");
            }
        } else {
            sender.sendMessage(pli.getMessagesConfig().no_perm);
        }
        return true;
    }
    
    public boolean setTeamMinPlayers(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd, String action, JavaPlugin plugin, Player p){
        
        if (sender.hasPermission(uber_permission + ".setup")) {
            if (args.length == 4 && validator.isNumeric(args[3])) {
                Integer count = Integer.parseInt(args[3]);
                String team = args[2].toLowerCase();
                if (validator.spawnExist(args[1], team)) {
                    ArenasConfig config = MinigamesAPI.getAPI().getPluginInstance(plugin).getArenasConfig();
                    String path = "arenas." + args[1] + ".spawns.spawn" + team + ".min_players";
                    config.getConfig().set(path, count);
                    config.saveConfig();
                    sender.sendMessage(m.im.successfully_set_team_component.replaceAll("<team>", team).replaceAll("<component>", " minimum players"));
                } else {
                    sender.sendMessage("The spawn " + args[2] + " doesn't exists");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]"
                        + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> [Bleu/Rouge/Jaune/Vert/Rose/Violet/Orange/Noir/Blanc] <nombre>");
            }
        } else {
            sender.sendMessage(pli.getMessagesConfig().no_perm);
        }
        return true;
    }
    
    public boolean setTeamMaxPlayers(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd, String action, JavaPlugin plugin, Player p){
        
        if (sender.hasPermission(uber_permission + ".setup")) {
            if (args.length == 4 && validator.isNumeric(args[3])) {
                Integer count = Integer.parseInt(args[3]);
                String team = args[2].toLowerCase();
                if (validator.spawnExist(args[1], team)) {
                    ArenasConfig config = MinigamesAPI.getAPI().getPluginInstance(plugin).getArenasConfig();
                    String path = "arenas." + args[1] + ".spawns.spawn" + team + ".max_players";
                    config.getConfig().set(path, count);
                    config.saveConfig();
                    sender.sendMessage(m.im.successfully_set_team_component.replaceAll("<team>", team).replaceAll("<component>", " maximum players"));
                } else {
                    sender.sendMessage("The spawn " + args[2] + " doesn't exists");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]"
                        + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> [Bleu/Rouge/Jaune/Vert/Rose/Violet/Orange/Noir/Blanc] <nombre>");
            }
        } else {
            sender.sendMessage(pli.getMessagesConfig().no_perm);
        }
        return true;
    }
    
    public boolean setItemCollect(PluginInstance pli, CommandSender sender, String[] args, String uber_permission, String cmd, String action, JavaPlugin plugin, Player p){
        
        if (sender.hasPermission(uber_permission + ".setup")) {
            
            ItemStack item = null;
            String itemId = "";
            String team = args[2].toLowerCase();
            
            if (args.length == 4) {
                
                itemId = args[3].toLowerCase();
                String[] itemParts = itemId.split(":");
                
                try {
                    item = new ItemStack(Integer.parseInt(itemParts[0]), 1, Short.parseShort(itemParts[1]));
                    
                } catch (Exception e) {
                    
                    itemId = "";
                    
                }
                
            } else if(args.length == 3) {
                
                item = p.getItemInHand();
                Integer intItemId = item.getTypeId(); 
                Integer intItemSubId = Short.toUnsignedInt(p.getItemInHand().getDurability());
                
                if (intItemId != 0) {
                    
                    if (intItemSubId != 0) {
                        
                        itemId = intItemId + ":" + intItemSubId;
                        
                    } else {
                        
                        itemId = intItemId.toString();
                        
                    }
                    
                }
            }
            
            if (validator.spawnExist(args[1], team) && !itemId.isEmpty()) {
                
                ArenasConfig config = MinigamesAPI.getAPI().getPluginInstance(plugin).getArenasConfig();
                String path = "arenas." + args[1] + ".spawns.spawn" + team + ".itemcollect";
                config.getConfig().set(path, itemId);
                config.saveConfig();
                sender.sendMessage(m.im.successfully_set_team_component.replaceAll("<team>", team).replaceAll("<component>", " item to collect ("+ item.getType().toString() + " " + itemId + ")"));
                
            } else {
                sender.sendMessage("The spawn " + args[2] + " doesn't exists or incorrect item specified.");
            }
        } else {
            
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> [Bleu/Rouge/Jaune/Vert/Rose/Violet/Orange/Noir/Blanc] [itemid:subid]");
            sender.sendMessage(pli.getMessagesConfig().no_perm);
        }
        return true;
    }
}
