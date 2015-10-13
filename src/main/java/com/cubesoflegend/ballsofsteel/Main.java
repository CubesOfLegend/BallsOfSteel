package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
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

public class Main extends JavaPlugin implements Listener {

    MinigamesAPI api = null;
    PluginInstance pli = null;
    ICommandHandler cmdhandler = new ICommandHandler();
    public IArenaScoreBoard scoreboard;
    public IArenaLobbyScoreBoard lobbyScoreBoard;
    static Main m = null;

    public void onEnable() {
        m = this;
        api = MinigamesAPI.getAPI().setupAPI(this, "BallsOfSteel", IArena.class, new ArenasConfig(this),
                new IMessagesConfig(this), new ClassesConfig(this, false), new StatsConfig(this, false),
                new DefaultConfig(this, false), false);
        PluginInstance pinstance = api.pinstances.get(this);
        pinstance.addLoadedArenas(loadArenas(this, pinstance.getArenasConfig()));
        Bukkit.getPluginManager().registerEvents(this, this);
        scoreboard = new IArenaScoreBoard(this);
        lobbyScoreBoard = new IArenaLobbyScoreBoard(pinstance, this);
        pinstance.scoreboardLobbyManager = lobbyScoreBoard;
        pinstance.scoreboardManager = scoreboard;
        pli = pinstance;
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

    /*
    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if(pli.containsGlobalPlayer(event.getPlayer().getName()) && !pli.containsGlobalLost(event.getPlayer().getName())){
            IArena a = (IArena) pli.global_players.get(event.getPlayer().getName());
            IPlayer ip = a.getPlayers().get(event.getPlayer());
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
            }
        }
    }
    */
    
    @EventHandler
    //Clic du joueur dans le menu
    public void onInteract(final PlayerInteractEvent event) {
        if (event.hasItem()) {
            if (pli.global_players.containsKey(event.getPlayer().getName())) {
                //On récupére l'IArena dans laquelle se trouve le joueur
                IArena ia = (IArena) pli.global_players.get(event.getPlayer().getName());
                if (event.getItem().getType() == Material.WOOL) {
                    //Si l'arene est en jeu && 
                    if (ia.getArenaState() != ArenaState.INGAME && !ia.isArcadeMain() && !ia.getIngameCountdownStarted()) {
                        TeamSelectorGui teamgui = ia.getTeamSelectorGui();
                        teamgui.openGUI(event.getPlayer().getName());
                    }
                }
            }
        }
    }
    
    
    
}
