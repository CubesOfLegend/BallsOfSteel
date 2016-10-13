package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaSetup;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.config.ArenasConfig;
import com.comze_instancelabs.minigamesapi.config.ClassesConfig;
import com.comze_instancelabs.minigamesapi.config.DefaultConfig;
import com.comze_instancelabs.minigamesapi.config.StatsConfig;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;
import com.cubesoflegend.ballsofsteel.config.IMessagesConfig;
import com.cubesoflegend.ballsofsteel.gui.TeamSelectorGui;
import com.cubesoflegend.ballsofsteel.model.Team;
import com.cubesoflegend.ballsofsteel.utils.BoundsUtil;
import com.cubesoflegend.ballsofsteel.utils.Debug;

public class Main extends JavaPlugin implements Listener {
    
    MinigamesAPI api = null;
    PluginInstance pli = null;
    ICommandHandler cmdhandler;
    
    public IMessagesConfig im;
    public IArenaScoreBoard scoreboard;
    public IArenaLobbyScoreBoard lobbyScoreBoard;
    static Main m = null;
    public Logger logger = Debug.getLogger();

    public void onEnable() {
        
        StopWatch timer = new StopWatch();
        timer.start();
        
        m = this;
        this.im = new IMessagesConfig(this);
        api = MinigamesAPI.getAPI().setupAPI(this, "BallsOfSteel", IArena.class, new ArenasConfig(this), im, new ClassesConfig(this, false), new StatsConfig(this, false), new DefaultConfig(this, false), false);
        
        PluginInstance pinstance = api.getPluginInstance(this);
        pinstance.addArenas(loadArenas(this, pinstance.getArenasConfig()));
        Bukkit.getPluginManager().registerEvents(this, this);
        scoreboard = new IArenaScoreBoard(pinstance, this);
        lobbyScoreBoard = new IArenaLobbyScoreBoard(pinstance, this);
        pinstance.scoreboardLobbyManager = lobbyScoreBoard;
        pinstance.scoreboardManager = scoreboard;
        this.pli = pinstance;
        this.cmdhandler = new ICommandHandler(this);
        
        timer.stop();
        logger.log(Level.INFO, Debug.formatPerf("enabling the plugin", timer.getTime()));
        timer.reset();
        
        
    }

    public static ArrayList<Arena> loadArenas(JavaPlugin plugin, ArenasConfig cf) {
        
        ArrayList<Arena> ret = new ArrayList<Arena>();
        FileConfiguration config = cf.getConfig();
        
        if (!config.isSet("arenas")) {
            return ret;
        }
        
        for (String arena : config.getConfigurationSection("arenas.").getKeys(false)) {
            if (Validator.isArenaValid(plugin, arena, cf.getConfig())) {
                ret.add(initArena(arena));
            }
        }
        
        return ret;
        
    }

    public static IArena initArena(String arena) {
        
        IArena a = new IArena(m, arena);
        ArenaSetup s = MinigamesAPI.getAPI().getPluginInstance(m).arenaSetup;
        a.init(Util.getSignLocationFromArena(m, arena), Util.getAllSpawns(m, arena), Util.getMainLobby(m),
        Util.getComponentForArena(m, arena, "lobby"), s.getPlayerCount(m, arena, true),
        s.getPlayerCount(m, arena, false), s.getArenaVIP(m, arena));
        
        return a;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        return cmdhandler.handleArgs(this, "bos", "/" + cmd.getName(), sender, args);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        
        
        if(pli.containsGlobalPlayer(event.getPlayer().getName()) && !pli.containsGlobalLost(event.getPlayer().getName())){
            
            IArena a = (IArena) pli.getArenaByGlobalPlayer(event.getPlayer().getName());
            IPlayer ip = a.getPlayers().get(event.getPlayer());
            if(a.getArenaState() == ArenaState.INGAME){
                for (Team team : a.teams) {
                    //Joueur entrant dans une base ennemie.
                    if(BoundsUtil.isInArea(event.getTo(), team.getBase().getBounds()) && ip.getTeam() != team){
                        ip.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', im.not_allowed_enter_in_base.replace("<team>", team.getChatColoredName())));
                        event.setCancelled(true);
                    }
                }
            }
            
        }
        
        
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if(pli.containsGlobalPlayer(event.getPlayer().getName()) && !pli.containsGlobalLost(event.getPlayer().getName())){
            IArena a = (IArena) pli.getArenaByGlobalPlayer(event.getPlayer().getName());
            IPlayer ip = a.getPlayers().get(event.getPlayer());
            
            if (a.getArenaState() == ArenaState.INGAME) {
                
                for (Team team : a.teams) {
                    //Bloc se trouvant dans une base ennemie.
                    if(BoundsUtil.isInArea(event.getBlock().getLocation(), team.getBase().getBounds()) && ip.getTeam() != team){
                        event.setCancelled(true);
                    //Bloc se trouvant dans le depot ennemi.
                    } else if (BoundsUtil.isInCuboid(event.getBlock().getLocation(), team.getDepot().getBounds()) && ip.getTeam() != team) {
                        event.setCancelled(true);
                    } 
                }

                //Bloc se trouvant dans le centre de l'arène.
                if (BoundsUtil.isInCuboid(event.getBlock().getLocation(), a.getCenter())) {
                    event.setCancelled(true);
                }
                
                //Bloc se trouvant à l'extérieur de l'aréne.
                if (!BoundsUtil.isInCuboid(event.getBlock().getLocation(), a.getBoundaries())){
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if(pli.containsGlobalPlayer(event.getPlayer().getName()) && !pli.containsGlobalLost(event.getPlayer().getName())){
            IArena a = (IArena) pli.getArenaByGlobalPlayer(event.getPlayer().getName());
            IPlayer ip = a.getPlayers().get(event.getPlayer());
            
            if (a.getArenaState() == ArenaState.INGAME) {
                for (Team team : a.teams) {
                    //Bloc se trouvant dans une base ennemie.
                    if(BoundsUtil.isInArea(event.getBlock().getLocation(), team.getBase().getBounds()) && ip.getTeam() != team){
                        event.setCancelled(true);
                    //Bloc se trouvant dans le depot ennemi.
                    } else if (BoundsUtil.isInCuboid(event.getBlock().getLocation(), team.getDepot().getBounds()) && ip.getTeam() != team) {
                        event.setCancelled(true);
                    } 
                }

                //Bloc se trouvant dans le centre de l'arène.
                if (BoundsUtil.isInCuboid(event.getBlock().getLocation(), a.getCenter())) {
                    event.setCancelled(true);
                }
                
                //Bloc se trouvant à l'extérieur de l'aréne.
                if (!BoundsUtil.isInCuboid(event.getBlock().getLocation(), a.getBoundaries())){
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    //Clic du joueur dans le menu
    public void onInteract(final PlayerInteractEvent event) {
        
            Player p = event.getPlayer();
            
            if(pli.containsGlobalPlayer(p.getName())){
                
                IArena ia = (IArena) pli.getArenaByGlobalPlayer(event.getPlayer().getName());
                
                switch (ia.getArenaState()) {
                case JOIN:
                    
                    if (event.hasItem() && event.getItem().getType() == Material.WOOL) {
                        
                        TeamSelectorGui teamgui = ia.getTeamSelectorGui();
                        teamgui.openGUI(p.getName());
                        
                    }
                    
                    break;
                    
                case INGAME:
                    
                    Block block = event.getClickedBlock();
                    
                    if (block != null) {
                        
                        IPlayer ip = ia.getPlayers().get(p);
                        
                        for (Team team : ia.teams) {
                            //bloc se trouvant dans une base ennemie
                            if(BoundsUtil.isInArea(block.getLocation(), team.getBase().getBounds()) && ip.getTeam() != team){
                                event.setCancelled(true);
                            } 
                            else if(BoundsUtil.isInCuboid(block.getLocation(), team.getDepot().getBounds()) && ip.getTeam() != team){
                                event.setCancelled(true);
                            }
                        }
                        
                        //Bloc se trouvant à l'extérieur de l'aréne.
                        if (!BoundsUtil.isInCuboid(block.getLocation(), ia.getBoundaries())){
                            event.setCancelled(true);
                        }
                    }
                    
                    break;

                default:
                    break;
                }
                
            }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        //TODO fix errror CraftPlayer cannot be cast to IPlayer 
        if(event.getEntity() instanceof Player){
            /*
            IPlayer ip = (IPlayer) event.getEntity();
            IArena ia = (IArena) pli.global_players.get(ip.getPlayer().getName());
            //Aréne en jeu
            if(ia.getArenaState() == ArenaState.INGAME){
                //Le joueur se trouve dans le centre de l'aréne
                if(BoundsUtil.isInCuboid(ip.getPlayer().getLocation(), ia.getCenter())){
                    event.setCancelled(true);
                }
                //Le joueur se trouve dans sa propre base
                else if(BoundsUtil.isInCuboid(ip.getPlayer().getLocation(), ip.getTeam().getBase().getBounds())){
                    event.setCancelled(true);
                }
            }
            */
        }
    }
    
    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event){
        
        
        if(pli.containsGlobalPlayer(event.getPlayer().getName()) && !pli.containsGlobalLost(event.getPlayer().getName())){
            
            IArena ia = (IArena) pli.getArenaByGlobalPlayer(event.getPlayer().getName());
            IPlayer ip = ia.getPlayers().get(event.getPlayer());
            
            if (ia.getArenaState() == ArenaState.INGAME) {
                
                if (event.getInventory().getType().equals(InventoryType.CHEST)) {
                    
                    
                    if (BoundsUtil.isInArea(event.getInventory().getLocation(), ip.getTeam().getDepot().getBounds())) {
                        
                        Inventory inventoryClosed = event.getInventory();
                        Block blockClosed = inventoryClosed.getLocation().getBlock();
                        
                        Chest chest = (Chest) blockClosed.getState();
                        
                        for (int i = 0; i < chest.getInventory().getContents().length; i++) {
                            
                            ItemStack itemStack = chest.getInventory().getContents()[i];
                            
                            //On a trouvé un itemStack que l'on doit compter, seul problème : on ne peut supprimer un itemStack sans virer les autres de même amount
                            if (itemStack != null && itemStack.isSimilar(ip.getTeam().getItemCollect())) {
                                
                                //On compte alors le nombre d'itemStack strictement egaux dans l'inventaire :
                                Integer nbItemStack = chest.getInventory().all(itemStack).size();
                                //On ajoute au score la multiplication du amount et du nombre d'itemn stacks.
                                ip.getTeam().addScore(itemStack.getAmount() * nbItemStack);
                                //On peut donc supprimer les items comptés.
                                chest.getInventory().remove(itemStack);
                            }
                            
                        }
                        
                        m.scoreboard.updateScoreboard(m, ia);
                    }
                }
                
            }
        }
    }
}


