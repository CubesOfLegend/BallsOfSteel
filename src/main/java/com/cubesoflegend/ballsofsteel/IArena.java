package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaConfigStrings;
import com.comze_instancelabs.minigamesapi.ArenaMessageStrings;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.Cuboid;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;
import com.cubesoflegend.ballsofsteel.gui.TeamSelectorGui;
import com.cubesoflegend.ballsofsteel.model.Base;
import com.cubesoflegend.ballsofsteel.model.Depot;
import com.cubesoflegend.ballsofsteel.model.Team;
import com.cubesoflegend.ballsofsteel.utils.Debug;

public class IArena extends Arena {
    
    public Main m;
    ArrayList<Team> teams;
    MinigamesAPI api;
    PluginInstance pli;
    ArrayList<Base> spawns;
    HashMap<Player, IPlayer> players;
    private TeamSelectorGui teamgui;
    private Cuboid center;
    
    /**
     * Match statistics: The last damager of players; helps fetching the player that kills another one.
     */
    private final HashMap<String, String> lastdamager = new HashMap<>();
    
    /**
     * Match statistics: Kill count of players.
     */
    HashMap<String, Integer> temp_kill_count = new HashMap<>();
    
    /**
     * Match statistics: Death count of players.
     */
    HashMap<String, Integer> temp_death_count = new HashMap<>();

    /**
     * Constructor IArena
     * @param Main m
     * @param String name
     */
    public IArena(Main m, String name) {
        
        super(m, name);
        
        StopWatch timer = new StopWatch();
        timer.start();
        
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
            //Récupération des données de l'aréne
            Location lowCenterArena = Util.getComponentForArena(m, name, "center.bounds.low");
            Location highCenterArena = Util.getComponentForArena(m, name, "center.bounds.high");
            this.center = new Cuboid(lowCenterArena, highCenterArena);
            // Boucle sur les noms de spawns
            for (String spawnname : spawnnames) {
                if (!spawnname.replace("spawn", "").equalsIgnoreCase("0")) {
                    
                    spawnConfig = config.getConfigurationSection("arenas." + name + ".spawns." + spawnname);
                    
                    Location spawnLoc = Util.getComponentForArena(m, name, "spawns."+spawnname);
                    Base spawn = new Base(spawnname, spawnLoc);
                    Location lowSpawnBound = Util.getComponentForArena(m, name, "spawns."+spawnname+".bounds.low");
                    Location highSpawnBound = Util.getComponentForArena(m, name, "spawns."+spawnname+".bounds.high");
                    String strItemCollect = config.getString("arenas." + name + ".spawns."+spawnname+".itemcollect");
                    
                    ItemStack itemCollect = null;
                    if (strItemCollect != null) {
                        
                        Material material = null;
                        ItemStack itemStack = null;
                        
                        if (strItemCollect.contains(":")) {
                            
                            String[] itemCollectParts = strItemCollect.split(":");
                            material = Material.getMaterial(itemCollectParts[0]);
                            Short itemMetadata = Short.parseShort(itemCollectParts[1]);
                            itemStack = new ItemStack(material, 1, itemMetadata);
                            
                        } else {
                            
                            material = Material.getMaterial(strItemCollect);
                            itemStack = new ItemStack(material, 1);
                            
                        }
                        
                        itemCollect = itemStack;
                        
                    }
                    
                    
                    spawn.setBounds(new Cuboid(lowSpawnBound, highSpawnBound));
                    spawns.add(spawn);
                    
                    Location lowDepotBound = Util.getComponentForArena(m, name, "spawns."+spawnname+".depot.bounds.low");
                    Location highDepotBound = Util.getComponentForArena(m, name, "spawns."+spawnname+".depot.bounds.high");
                    Depot depot = new Depot(new Cuboid(lowDepotBound, highDepotBound));
                    Team team = new Team(spawnname.replace("spawn", ""), spawn, depot, itemCollect);
                    
                    teams.add(team);
                }
            }
            if (!teams.isEmpty()) {
                System.out.println("TeamSelectorGUI instanciate");
                teamgui = new TeamSelectorGui(pli, m, teams);
            }
            
            timer.stop();
            m.logger.log(Level.INFO, Debug.formatPerf("IArena()", timer.getTime()));
            
        }
    }
    
    @Override
    public void spectate(String playername) {
        
        if (Validator.isPlayerValid(this.getPlugin(), playername, this))
        {
            this.onEliminated(playername);
            final Player p = Bukkit.getPlayer(playername);
            
            if (p == null)
            {
                return;
            }
            
            this.pli.global_lost.put(playername, this);
            
            this.pli.getSpectatorManager().setSpectate(p, true);
            if (!this.getPlugin().getConfig().getBoolean(ArenaConfigStrings.CONFIG_SPECTATOR_AFTER_FALL_OR_DEATH))
            {
                this.leavePlayer(playername, false, false);
                this.pli.scoreboardManager.updateScoreboard(this.getPlugin(), this);
                return;
            }
            this.spectateGame(playername);
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
    
    /**
     * Return arena center
     * @return Cuboid
     */
    public Cuboid getCenter(){
        return this.center;
    }
    
    //Listeners
    @Override
    public void joinPlayerLobby(String playername) {
        
        StopWatch timer = new StopWatch();
        timer.start();
        
        System.out.println("Join player Lobby");
        
        Player p = Bukkit.getPlayer(playername);
        IPlayer ip = new IPlayer(p);
        players.put(p, ip);

        // Permet de lancer run() après un certain nombre de ticks serveur
        Bukkit.getScheduler().runTaskLater(m, new Runnable() {

            @Override
            public void run() {
                
                StopWatch timer = new StopWatch();
                timer.start();
                
                if (p != null) {
                    if (m.pli.containsGlobalPlayer(p.getName())) {
                        
                        System.out.println("Update the inventory");
                        
                        ItemStack teamselector = new ItemStack(Material.WOOL, 1, (byte) 14);
                        ItemMeta itemm = teamselector.getItemMeta();
                        itemm.setDisplayName(ChatColor.RED + "Team");
                        teamselector.setItemMeta(itemm);
                        p.getInventory().setItem(2, teamselector);
                        p.updateInventory();
                    }
                }
                
                timer.stop();
                m.logger.log(Level.INFO, Debug.formatPerf("IArena:joinPlayerLobby():run()", timer.getTime()));
                
            }
        }, 25L);
        
        super.joinPlayerLobby(p.getUniqueId());
        m.lobbyScoreBoard.updateScoreboard(m, this);
        
        timer.stop();
        m.logger.log(Level.INFO, Debug.formatPerf("IArena:joinPlayerLobby()", timer.getTime()));
        
        return;
    }

    @Override
    public void leavePlayer(String playername, boolean fullLeave, boolean endOfTheGame) {
        
        IPlayer ip = players.get(Bukkit.getPlayer(playername));
        m.scoreboard.removeScoreboard(this.getInternalName(), ip.getPlayer());
        players.remove(ip.getPlayer());
        
        //Si il est associé à une équipe
        if(ip.getTeam() != null){
            //On le vire de son équipe
            ip.getTeam().removePlayer(ip);
        }
        
        super.leavePlayer(playername, fullLeave, endOfTheGame);
    }

    @Override
    public void start(boolean tp) {
        
        StopWatch timer = new StopWatch();
        timer.start();
        
        //On prend le nombre de joueur que l'on divise par le nombre de team.
        Integer countPlayersByTeam = Math.floorDiv(players.size(), teams.size());
        
        System.out.println("On veut " + countPlayersByTeam + " joueurs par team.");
        
        //On prepare la liste des joueurs ayant essayer de rejoindre une team dans le lobby 1.
        ArrayList<IPlayer> lobbyOne = new ArrayList<>(); 
        //On prepare la liste des joueurs n'ayant pas choisi de team dans le lobby 2.
        ArrayList<IPlayer> lobbyTwo = new ArrayList<>();
        ArrayList<Team> completedTeams = new ArrayList<>();
        
        //On recupére les players sans team (LobbyTwo)
        for (IPlayer ip : players.values()) {
            if (ip.getTeam() == null) {
                lobbyTwo.add(ip);
            }
        }
        
        //On récupére les joueurs qui sont en trop dans chacune des teams.
        for (Team team : teams) {
            
            System.out.println(" - Team : " + team.getName() + " size = " + team.getPlayers().size());
            
            //Joueurs en trop
            if (team.getPlayers().size() > countPlayersByTeam) {
                
                System.out.println(" -- Trop de joueurs");
                
                //Nombre de joueur à virer.
                Integer countPlayersToMove = team.getPlayers().size() - countPlayersByTeam;
                
                //Récupération des joueurs en trop que l'on vire.
                for (int i = 0; i < countPlayersToMove; i++) {
                    
                    Integer lastPlayerIndex = team.getPlayers().size() - 1;
                    
                    Player p = team.getPlayers().get(lastPlayerIndex).getPlayer();
                    System.out.println(" --- Moving " + p.getName() + "(" + lastPlayerIndex + ") to lobby one ( was " + team.getName() + ")");
                    
                    //Le joueur est déplacé dans un lobby prioritaire.
                    lobbyOne.add(team.getPlayers().get(lastPlayerIndex));
                    changePlayerToTeam(p, null);
                    
                }
            }
            
        }
        
        Integer maxLoop = 0;
        
        while (completedTeams.size() != teams.size() && maxLoop < 100) {
            
            maxLoop++;
            
            //On parcours les teams tant qu'elle ne sont pas complétes.
            for (Team team : teams) {
                
                System.out.println(" - Team : " + team.getName() + " size = " + team.getPlayers().size());
                
                if (team.getPlayers().size() < countPlayersByTeam) {
                    
                    Integer countPlayersToMove = countPlayersByTeam - team.getPlayers().size();
                    System.out.println(" -- Team non complete");
                    
                    //Récupération du nombre de joueurs qu'il manque.
                    for (int i = 0; i < countPlayersToMove; i++) {
                        
                        Player p;
                        
                        if (!lobbyOne.isEmpty()) {
                            
                            p = lobbyOne.get(0).getPlayer();
                            lobbyOne.remove(0);
                            
                        } else {
                            
                            p = lobbyTwo.get(0).getPlayer();
                            lobbyTwo.remove(0);
                            
                        }
                        
                        changePlayerToTeam(p, team);
                        
                        System.out.println(" --- Moving " + p.getName() + " to team " + team.getName() + ")");
                        
                        
                    }
                    
                //Team compléte
                } else {
                    
                    if (!completedTeams.contains(team)) {
                        System.out.println("Team " + team.getName() + " is completed !");
                        completedTeams.add(team);
                        team.teleportTeam(team.getBase().getLocation());
                    }
                }
            }
            
        }
        
        //On vire les joueurs restant dans les lobbys.
        for (IPlayer ip : lobbyOne) {
            this.leavePlayer(ip.getPlayer().getName(), true, false);
        }
        
        for (IPlayer ip : lobbyTwo) {
            this.leavePlayer(ip.getPlayer().getName(), true, false);
        }
        
        super.start(false);
        
        timer.stop();
        m.logger.log(Level.INFO, Debug.formatPerf("IArena:start()", timer.getTime()));
        
        return;
    }
    
    @Override
    public void started() {
        //m.scoreboard.updateScoreboard(m, this);
        super.started();
    }
    
    public void changePlayerToTeam(Player p, Team team){
        
        IPlayer player = players.get(p);
        
        //Si il a une team on le supprime de sa team
        if(player.getTeam() != null){
            teams.get(teams.indexOf(player.getTeam())).removePlayer(player);
        }
        
        //Aucune team asssignée 
        if (team == null) {
            
            players.get(p).setTeam(null);
            
        } else {
            
            teams.get(teams.indexOf(team)).addPlayer(player);
            players.get(p).setTeam(team);
            
            
        }
        
        m.lobbyScoreBoard.updateScoreboard(m, this);
        
    }
    
    @Override
    public void onEliminated(String playername) {
        
        System.out.println("On eliminated");
        
        if (this.lastdamager.containsKey(playername))
        {
            final Player killer = Bukkit.getPlayer(this.lastdamager.get(playername));
            if (killer != null && !playername.equals(killer.getName()))
            {
                this.pli.getStatsInstance().addDeath(playername);
                this.temp_kill_count.put(killer.getName(), this.temp_kill_count.containsKey(killer.getName()) ? this.temp_kill_count.get(killer.getName()) + 1 : 1);
                this.temp_death_count.put(playername, this.temp_death_count.containsKey(playername) ? this.temp_death_count.get(playername) + 1 : 1);
                this.pli.getRewardsInstance().giveKillReward(killer.getName());
                
                Player player = Bukkit.getPlayer(playername);
                IPlayer ip = players.get(player);
                
                Inventory inventory = player.getInventory();
                
                Integer nbLostItem = 0;
                for (int i = 0; i < inventory.getContents().length; i++) {
                    
                    ItemStack itemStack = inventory.getContents()[i];
                    
                    if (itemStack != null && itemStack.isSimilar(ip.getTeam().getItemCollect())) {
                        
                        nbLostItem += nbLostItem + itemStack.getAmount();
                        
                    }
                    
                }
                
                
                Util.sendMessage(this.getPlugin(), killer, pli.getMessagesConfig().you_got_a_kill
                        .replaceAll(ArenaMessageStrings.PLAYER, playername)
                        .replaceAll(IArenaMessageStrings.COUNT, nbLostItem.toString())
                        .replaceAll(IArenaMessageStrings.ITEM, ip.getTeam().getItemCollect().toString())
                        );
                
                for (final String p_ : this.getAllPlayers())
                {
                    if (!p_.equalsIgnoreCase(killer.getName()))
                    {
                        if (Validator.isPlayerOnline(p_))
                        {
                            Bukkit.getPlayer(p_).sendMessage(pli.getMessagesConfig().player_was_killed_by.replaceAll(ArenaMessageStrings.PLAYER, playername)
                                    .replaceAll(ArenaMessageStrings.KILLER, killer.getName())
                                    .replaceAll(IArenaMessageStrings.COUNT, nbLostItem.toString())
                                    .replaceAll(IArenaMessageStrings.ITEM, ip.getTeam().getItemCollect().toString())
                                    );
                        }
                    }
                }
            }
            this.lastdamager.remove(playername);
        }
        else
        {
            this.pli.getStatsInstance().addDeath(playername);
        }
        
    }
    
    @Override
    public void stop() {
        //TODO get all the depot and count the number of diamonds into.
        super.stop();
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