package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;

public class IArena extends Arena{
	ArrayList<Team> teams;
	MinigamesAPI api;
	
	public IArena(JavaPlugin plugin, String name){
		super(plugin, name);
		api = MinigamesAPI.getAPI();
		ArrayList<Location> spawns = this.getSpawns();
		//Pour chaque spawn de l'arène on crée une team.
		for (Location location : spawns) {
			Team team = null;
			teams.add(team);
		}
	}

	@Override
	public void joinPlayerLobby(String playername){
	}

	@Override
	public void leavePlayer(String playername, boolean fullLeave) {
	}
	
	@Override
	public void start(boolean tp){
		ArrayList<Location> spawns = this.getSpawns();
		ArrayList<String> players = this.getAllPlayers();
		Bukkit.getLogger().info("getAllplayer renvoie : " + players.size());
		return;
	}
}