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
import com.cubesoflegend.ballsofsteel.gui.TeamSelectorGui;

public class IArena extends Arena {
    public Main m;
    //Liste des teams et de leur joueurs
    ArrayList<Team> teams;
    MinigamesAPI api;
    PluginInstance pli;
    //Liste des spawns de l'arène
    ArrayList<Spawn> spawns;
    //Liste des joueurs participants (Présents au lancement de la partie)
    HashMap<Player, IPlayer> players;
    private TeamSelectorGui teamgui;

    public IArena(Main m, String name) {
        super(m, name);
        this.m = m;
        api = MinigamesAPI.getAPI();
        pli = api.getPluginInstance(m);
        players = new HashMap<Player, IPlayer>();
        teams = new ArrayList<Team>();
        
        // On récupere la configuration
        FileConfiguration config = pli.getArenasConfig().getConfig();
        if (config.isSet("arenas." + name + ".spawns.spawn0")) {
            Location loc;
            ConfigurationSection spawnConfig;
            Set<String> spawnnames = config.getConfigurationSection("arenas." + name + ".spawns").getKeys(false);
            // Boucle sur les noms de spawns
            for (String spawnname : spawnnames) {
                if (!spawnname.replace("spawn", "").equalsIgnoreCase("0")) {
                    spawnConfig = config.getConfigurationSection("arenas." + name + ".spawns." + spawnname);
                    World world = Bukkit.getWorld(spawnConfig.getString("world"));
                    loc = new Location(world, spawnConfig.getDouble("location.x"), spawnConfig.getDouble("location.y"),
                            spawnConfig.getDouble("location.z"), spawnConfig.getLong("location.yaw"),
                            spawnConfig.getLong("location.pitch"));

                    Spawn spawn = new Spawn(spawnname, loc);
                    Team team = new Team(spawnname.replace("spawn", ""), spawn);
                    teams.add(team);
                }
            }
            if (!teams.isEmpty()) {
                teamgui = new TeamSelectorGui(pli, m, teams);
            }
        }
    }

    @Override
    public void joinPlayerLobby(String playername) {
        // Permet de lancer run() après un certain nombre de ticks serveur
        Bukkit.getScheduler().runTaskLater(this.getPlugin(), new Runnable() {

            @Override
            public void run() {
                Player p = Bukkit.getPlayer(playername);
                IPlayer ip = new IPlayer(p);
                players.put(p, ip);
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
            team.teleportTeam(team.getSpawn().location);
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
        verboseArenaData();
        m.lobbyScoreBoard.updateScoreboard(m, this);
    }
    public ArrayList<Team> getTeams(){
        return this.teams;
    }

    public TeamSelectorGui getTeamSelectorGui() {
        return this.teamgui;
    }
    
    public void verboseArenaData(){
        System.out.println(" Printing players ...");
        for (Map.Entry<Player, IPlayer> m_p_ip : players.entrySet()) {
            System.out.println(" - - Player : " + m_p_ip.getValue().getPlayer().getName() + " => " + m_p_ip.getValue().getTeam().getName());
        }
        
        System.out.println(" Printing teams ...");
        for (Team team : teams) {
            System.out.println(" - - Team " + team.getName() + "(" + team.getPlayers().size() +")");
            for (IPlayer player : team.getPlayers()) {
                System.out.println(" - - - - " + player.getPlayer().getName());
            }
        }
    }
}