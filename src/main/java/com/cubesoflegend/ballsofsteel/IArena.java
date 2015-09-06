package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;
import java.util.HashMap;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.Party;

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
		Player main_player = Bukkit.getServer().getPlayer(playername);
		HashMap<String, Party> partys;
		partys = this.api.global_party;
		
		//On parcours les différentes partys crées
		for (String key : partys.keySet()) {
			//Si la clé de la party est le nom du joueur (Donc il est owner)
			if(key == playername){
				ArrayList<String> playernamesteam = new ArrayList<String>();
				playernamesteam = partys.get(key).getPlayers();
				//On téléporte les joueurs de sa party
				for (String playernameteam : playernamesteam) {
					Player partyPlayer = Bukkit.getServer().getPlayer(playernameteam);
					partyPlayer.teleport(this.getWaitingLobbyTemp());
				}
			}
		}
		//On téléporte le joueur
		main_player.teleport(this.getWaitingLobbyTemp());
		return;
	}
	
	@Override
	public void leavePlayer(String playername, boolean fullLeave) {
		Player main_player = Bukkit.getServer().getPlayer(playername);
		HashMap<String, Party> partys;
		partys = this.api.global_party;
		
		//On parcours les différentes partys crées
		for (String key : partys.keySet()) {
			//Si la clé de la party est le nom du joueur (Donc il est owner)
			if(key == playername){
				ArrayList<String> playernamesteam = new ArrayList<String>();
				playernamesteam = partys.get(key).getPlayers();
				//On téléporte les joueurs de sa party
				for (String playernameteam : playernamesteam) {
					Player partyPlayer = Bukkit.getServer().getPlayer(playernameteam);
					partyPlayer.teleport(this.getMainLobbyTemp());
				}
			}
		}
		//On téléporte le joueur
		main_player.teleport(this.getMainLobbyTemp());
		return;
	};
	
	@Override
	public void start(boolean tp){
		ArrayList<Location> spawns = this.getSpawns();
		ArrayList<String> players = this.getAllPlayers();
		Bukkit.getLogger().info("getAllplayer renvoie : " + players.size());
		return;
	}
}