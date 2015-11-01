package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.Cuboid;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.cubesoflegend.ballsofsteel.gui.TeamSelectorGui;
import com.cubesoflegend.ballsofsteel.model.Base;
import com.cubesoflegend.ballsofsteel.model.Team;

public class IArena extends Arena {
    public Main m;
    ArrayList<Team> teams;
    MinigamesAPI api;
    PluginInstance pli;
    ArrayList<Base> spawns;
    HashMap<Player, IPlayer> players;
    private TeamSelectorGui teamgui;

    /**
     * Constructor IArena
     * @param Main m
     * @param String name
     */
    public IArena(Main m, String name) {
        super(m, name);
        this.m = m;
        api = MinigamesAPI.getAPI();
        pli = api.getPluginInstance(m);
        players = new HashMap<Player, IPlayer>();
        teams = new ArrayList<Team>();
        spawns = new ArrayList<Base>();
        
        // On récupere la configuration
        FileConfiguration config = pli.getArenasConfig().getConfig();
        if (config.isSet("arenas." + name + ".spawns.spawn0")) {
            ConfigurationSection spawnConfig;
            Set<String> spawnnames = config.getConfigurationSection("arenas." + name + ".spawns").getKeys(false);
            // Boucle sur les noms de spawns
            for (String spawnname : spawnnames) {
                if (!spawnname.replace("spawn", "").equalsIgnoreCase("0")) {
                    spawnConfig = config.getConfigurationSection("arenas." + name + ".spawns." + spawnname);
                    World world = Bukkit.getWorld(spawnConfig.getString("world"));
                    Location spawnLoc = Util.getComponentForArena(m, name, "spawns."+spawnname);
                    Base spawn = new Base(spawnname, spawnLoc);
                    Location lowSpawnBound = Util.getComponentForArena(m, name, "spawns."+spawnname+".bounds.low");
                    Location highSpawnBound = Util.getComponentForArena(m, name, "spawns."+spawnname+".bounds.high");
                    spawn.setBounds(new Cuboid(lowSpawnBound, highSpawnBound));
                    spawns.add(spawn);
                    Team team = new Team(spawnname.replace("spawn", ""), spawn);
                    teams.add(team);
                }
            }
            if (!teams.isEmpty()) {
                teamgui = new TeamSelectorGui(pli, m, teams);
            }
        }
    }

    //Getters
    /**
     * Return teams
     * @return ArrayList<Team>
     */
    public ArrayList<Team> getTeams(){
        return this.teams;
    }
    
    /**
     * Return players
     * @return HashMap<Player, IPlayer>
     */
    public HashMap<Player, IPlayer> getPlayers(){
        return this.players;
    }
    
    /**
     * Return arena associated TeamSelectorGUI 
     * @return TeamSelectorGUI
     */
    public TeamSelectorGui getTeamSelectorGui() {
        return this.teamgui;
    }
    
    //Listeners
    @Override
    public void joinPlayerLobby(String playername) {
        
        Player p = Bukkit.getPlayer(playername);
        IPlayer ip = new IPlayer(p);
        players.put(p, ip);

        // Permet de lancer run() après un certain nombre de ticks serveur
        Bukkit.getScheduler().runTaskLater(this.getPlugin(), new Runnable() {

            @Override
            public void run() {
                if (p != null) {
                    if (m.pli.global_players.containsKey(p.getName())) {
                        ItemStack teamselector = new ItemStack(Material.WOOL, 1, (byte) 14);
                        ItemMeta itemm = teamselector.getItemMeta();
                        itemm.setDisplayName(ChatColor.RED + "Team");
                        teamselector.setItemMeta(itemm);
                        p.getInventory().setItem(2, teamselector);
                        p.updateInventory();
                    }
                }
            }
        }, 25L);
        super.joinPlayerLobby(playername);
        m.lobbyScoreBoard.updateScoreboard(m, this);
    }

    @Override
    public void leavePlayer(String playername, boolean fullLeave) {
        IPlayer ip = players.get(Bukkit.getPlayer(playername));
        m.scoreboard.removeScoreboard(this.getName(), ip.getPlayer());
        players.remove(ip.getPlayer());
        //Si il est associé à une équipe
        if(ip.getTeam() != null){
            //On le vire de son équipe
            ip.getTeam().removePlayer(ip);
        }
        super.leavePlayer(playername, fullLeave);
    }

    @Override
    public void start(boolean tp) {
        //On renvoie toutes les teams à leur spawns respectifs
        for (Team team : teams) {
            team.teleportTeam(team.getBase().getLocation());
        }
        super.start(false);
        return;
    }
    
    public void changePlayerToTeam(Player p, Team team){
        IPlayer player = players.get(p);
        //Si il a une team on le supprime de sa team
        if(player.getTeam() != null){
            teams.get(teams.indexOf(player.getTeam())).removePlayer(player);
        }
        teams.get(teams.indexOf(team)).addPlayer(player);
        players.get(p).setTeam(team);
        m.lobbyScoreBoard.updateScoreboard(m, this);
    }
    
    
    //Dev
    public void verboseArenaData(){
        try {
            if (players.size()!=0) {
                System.out.println(" Printing players ...");
                for (Map.Entry<Player, IPlayer> m_p_ip : players.entrySet()) {
                    String playerInfo = " - - Player : " + m_p_ip.getValue().getPlayer().getName();
                    if(m_p_ip.getValue().getTeam() == null){
                        playerInfo += " => no team";
                    }
                    else
                    {
                        playerInfo += " => " + m_p_ip.getValue().getTeam().getName();
                    }
                    System.out.println(playerInfo);
                }
            }
            else
            {
                System.out.println("No players...");
            }
            
            if(teams.size()!=0){
                System.out.println(" Printing teams ...");
                for (Team team : teams) {
                    System.out.println(" - - Team " + team.getName() + "(" + team.getPlayers().size() +")");
                    for (IPlayer player : team.getPlayers()) {
                        System.out.println(" - - - - " + player.getPlayer().getName());
                    }
                }
            }
            else
            {
                System.out.println("No teams...");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}