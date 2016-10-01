package com.cubesoflegend.ballsofsteel;

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

    
    @Override
    protected void initCmdDesc() {
        
        cmddesc.put("", null);
        cmddesc.put("setcenterbounds <arena> <low/high>", "Sets the low or high boundary point for center arena");
        cmddesc.put("", null);
        cmddesc.put("setspawn <arena> <team>", "Sets a team spawn point. (One spawn, one team)");       
        cmddesc.put("removespawn <arena> <team>", "Remove a team spawn point. (One spawn, one team)");
        cmddesc.put("setteambounds <arena> <team> <low/high>", "Sets the low or high boundary point for team base");
        cmddesc.put("setdepotbounds <arena> <team> <low/high>", "Sets the low or high boundary point for team depot");
        cmddesc.put("setitemcollect <arena> <team> [itemid:itemsubid]", "Sets the item to collect for a team in arena (If not specified set item in hand).");
        
        super.initCmdDesc();
        
        //Remove overrided commands.
        cmddesc.remove("setspawn <arena>");
        cmddesc.remove("removespawn <arena> <count>");
        
    }

    public ICommandHandler(Main m) {
        this.m = m;
        this.validator = new Validator(this.m.pli);
    }
    
    @Override
    public boolean handleArgs(JavaPlugin plugin, String uber_permission, String cmd, CommandSender sender,
            String[] args) {
        
        
        if (args.length > 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Please execute this command ingame.");
                return true;
            }
            Player p = (Player) sender;
            PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(plugin);
            String action = args[0];
            if (action.equalsIgnoreCase(CommandStrings.SET_SPAWN)) {
                return this.setSpawn(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase(CommandStrings.REMOVE_SPAWN)) {
                return this.removeSpawn(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase(CommandStrings.SET_TEAM_BOUNDS)) {
                return this.setTeamBounds(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase(CommandStrings.SET_DEPOT_BOUNDS)) {
                return this.setDepotBounds(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase(CommandStrings.SET_CENTER_BOUNDS)) {
                return this.setCenterBounds(pli, sender, args, uber_permission, cmd, action, plugin, p);
            } else if (action.equalsIgnoreCase(CommandStrings.SET_ITEM_COLLECT)) {
                return this.setItemCollect(pli, sender, args, uber_permission, cmd, action, plugin, p);
            }
        }
        
        return super.handleArgs(plugin, uber_permission, cmd, sender, args);
        
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
