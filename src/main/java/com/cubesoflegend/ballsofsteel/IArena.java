package com.cubesoflegend.ballsofsteel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.Arena;

public class IArena extends Arena{
	
	public IArena(JavaPlugin plugin, String name){
		super(plugin, name);
	}
	
	@Override
	public void joinPlayerLobby(String playername){
		Player player = Bukkit.getServer().getPlayer(playername);
		player.teleport(getMainLobbyTemp());
		player.chat("Bienvenue au main lobby du BallsOfSteel");
		return;
	}
	
	@Override
	public void start(boolean tp){
		Bukkit.getLogger().info("Test start()");
		return;
	}
}
