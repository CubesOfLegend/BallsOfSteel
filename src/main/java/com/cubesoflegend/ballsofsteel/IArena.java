package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;
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
    ArrayList<Team> teams;
    MinigamesAPI api;
    PluginInstance pli;
    ArrayList<Spawn> spawns;
    private TeamSelectorGui teamgui;

    public IArena(Main m, String name) {
        super(m, name);
        this.m = m;
        api = MinigamesAPI.getAPI();
        pli = api.getPluginInstance(m);
        teams = new ArrayList<Team>();

        // On r�cupere la configuration
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
                    Team team = new Team(Constants.colors.get(spawnname.replace("spawn", "")), spawn);
                    this.teams.add(team);
                }
            }
            if (!teams.isEmpty()) {
                teamgui = new TeamSelectorGui(pli, m, teams);
            }
        }
    }

    @Override
    public void joinPlayerLobby(String playername) {
        // Permet de lancer run() apr�s un certain nombre de ticks serveur
        Bukkit.getScheduler().runTaskLater(this.getPlugin(), new Runnable() {

            @Override
            public void run() {
                Player p = Bukkit.getPlayer(playername);
                if (p != null) {
                    if (m.pli.global_players.containsKey(p.getName())) {
                        ItemStack teamselector = new ItemStack(Material.WOOL, 1, (byte) 14);
                        ItemMeta itemm = teamselector.getItemMeta();
                        itemm.setDisplayName(ChatColor.RED + "Team");
                        teamselector.setItemMeta(itemm);
                        p.getInventory().setItem(0, teamselector);
                        p.updateInventory();
                    }
                }
            }
        }, 25L);
        super.joinPlayerLobby(playername);
    }

    @Override
    public void leavePlayer(String playername, boolean fullLeave) {
        super.leavePlayer(playername, fullLeave);
    }

    @Override
    public void start(boolean tp) {
        ArrayList<Location> spawns = this.getSpawns();
        ArrayList<String> players = this.getAllPlayers();
        Bukkit.getLogger().info("getAllplayer renvoie : " + players.size());
        return;
    }

    public ArrayList<Team> getTeams() {
        return this.teams;
    }

    public TeamSelectorGui getTeamSelectorGui() {
        return this.teamgui;
    }
}