package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;
import java.util.Collection;

import javax.sound.midi.Synthesizer;
import javax.swing.plaf.BorderUIResource.MatteBorderUIResource;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Chest;
import org.bukkit.plugin.java.JavaPlugin;
import org.omg.CORBA.Bounds;

import com.avaje.ebeaninternal.server.transaction.TransactionLogBuffer.LogEntry;
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
import com.cubesoflegend.ballsofsteel.model.Base;
import com.cubesoflegend.ballsofsteel.model.Team;
import com.cubesoflegend.ballsofsteel.utils.BoundsUtil;

public class Main extends JavaPlugin implements Listener {
    
    MinigamesAPI api = null;
    PluginInstance pli = null;
    ICommandHandler cmdhandler;
    
    public IMessagesConfig im;
    public IArenaScoreBoard scoreboard;
    public IArenaLobbyScoreBoard lobbyScoreBoard;
    static Main m = null;

    public void onEnable() {
        m = this;
        this.im = new IMessagesConfig(this);
        api = MinigamesAPI.getAPI().setupAPI(this, "BallsOfSteel", IArena.class, new ArenasConfig(this), im, new ClassesConfig(this, false), new StatsConfig(this, false), new DefaultConfig(this, false), false);
        PluginInstance pinstance = api.pinstances.get(this);
        pinstance.addLoadedArenas(loadArenas(this, pinstance.getArenasConfig()));
        Bukkit.getPluginManager().registerEvents(this, this);
        scoreboard = new IArenaScoreBoard(this);
        lobbyScoreBoard = new IArenaLobbyScoreBoard(pinstance, this);
        pinstance.scoreboardLobbyManager = lobbyScoreBoard;
        pinstance.scoreboardManager = scoreboard;
        this.pli = pinstance;
        this.cmdhandler = new ICommandHandler(this);
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
        ArenaSetup s = MinigamesAPI.getAPI().pinstances.get(m).arenaSetup;
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
            IArena a = (IArena) pli.global_players.get(event.getPlayer().getName());
            IPlayer ip = a.getPlayers().get(event.getPlayer());
            if(a.getArenaState() == ArenaState.INGAME){
                for (Team team : a.teams) {
                    //Joueur entrant dans une base ennemie.
                    if(BoundsUtil.isInArea(event.getTo(), team.getBase().getBounds()) && ip.getTeam() != team){
                        ip.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', im.not_allowed_enter_in_base.replace("<team>", team.getChatColoredName())));
                        event.setCancelled(true);
                    }
                    if(BoundsUtil.isInArea(event.getTo(), team.getBase().getBounds()) && ip.getTeam() != team){
                        ip.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', im.not_allowed_enter_in_base.replace("<team>", team.getChatColoredName())));
                        event.setCancelled(true);
                    }
                }
            }
            if(BoundsUtil.isInCuboid(ip.getPlayer().getLocation(), a.getCenter())){
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if(pli.containsGlobalPlayer(event.getPlayer().getName()) && !pli.containsGlobalLost(event.getPlayer().getName())){
            IArena a = (IArena) pli.global_players.get(event.getPlayer().getName());
            
            //On interdit la destruction d'un bloc dans le centre d'une aréne
            if(BoundsUtil.isInCuboid(event.getBlock().getLocation(), a.getCenter())){
                    event.setCancelled(true);
            }
            
            /*
            if(a.getArenaState() == ArenaState.INGAME && event.getBlock().getType() == Material.DIAMOND_ORE){
                Collection<ItemStack> drops = event.getBlock().getDrops();
                for (ItemStack itemStack : drops) {
                    if(itemStack.getType() == Material.DIAMOND){
                        ip.setDiamondsMined(ip.getDiamondsMined() + itemStack.getAmount());
                        //Get amount envoie toujours 1...
                        System.out.println(event.getPlayer().getName() + " has break diamond ore which drops " + itemStack.getAmount());
                        System.out.println(event.getPlayer().getName() + " has " + ip.getDiamondsMined() + " diamonds");
                    }

            }
            */
        }
    }
    
    @EventHandler
    //Clic du joueur dans le menu
    public void onInteract(final PlayerInteractEvent event) {
        System.out.println("On interact");
        if(pli.containsGlobalPlayer(event.getPlayer().getName()) && !pli.containsGlobalLost(event.getPlayer().getName())){
            IArena ia = (IArena) pli.global_players.get(event.getPlayer().getName());
            IPlayer ip = ia.getPlayers().get(event.getPlayer());
            if (ia.getArenaState() != ArenaState.INGAME) {
                //Open teamselector
                if(!ia.isArcadeMain() && !ia.getIngameCountdownStarted() && event.hasItem() && event.getItem().getType() == Material.WOOL){
                    TeamSelectorGui teamgui = ia.getTeamSelectorGui();
                    teamgui.openGUI(event.getPlayer().getName());
                }
                //Forbid interact with all blocks in arena if arena is not in game
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction()==Action.LEFT_CLICK_BLOCK){
                    event.setCancelled(true);
                }
            }
            //Aréne en état de jeu
            else{
                //Toute interaction avec un block
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction()==Action.LEFT_CLICK_BLOCK){

                    for (Team team : ia.teams) {
                        //bloc se trouvant dans une base ennemie
                        if(BoundsUtil.isInArea(event.getClickedBlock().getLocation(), team.getBase().getBounds()) && ip.getTeam() != team){
                            event.setCancelled(true);
                        } 
                        else if(BoundsUtil.isInCuboid(event.getClickedBlock().getLocation(), team.getDepot().getBounds())){
                            //bloc se trouvant dans le depot d'une autre team
                            if(ip.getTeam() != team){
                                event.setCancelled(true);
                            //bloc se trouvant dans le depot de sa propre base    
                            } else {
                                
                            }
                        }
                    }
                }
            }
        }
    }
    
    //Une entité qui subit des dégats
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        
        if(event.getEntity() instanceof Player){
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
            else{
                event.setCancelled(true);
            }
        }
       
    }
}
