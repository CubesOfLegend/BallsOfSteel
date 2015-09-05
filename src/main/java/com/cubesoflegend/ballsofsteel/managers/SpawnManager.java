package com.cubesoflegend.ballsofsteel.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;

public  class SpawnManager{
	
	public static boolean addSpawn(JavaPlugin plugin, String arena, Location location, String color){
		PluginInstance pinstance = MinigamesAPI.getAPI().pinstances.get(plugin);
		Integer count = getColorNumber(color);
		if(count!=0){
			pinstance.arenaSetup.setSpawn(plugin, arena, location, count);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean removeSpawn(JavaPlugin plugin, String arena, Location location, String color){
		PluginInstance pinstance = MinigamesAPI.getAPI().pinstances.get(plugin);
		Integer count = getColorNumber(color);
		if(count!=0){
			pinstance.arenaSetup.removeSpawn(plugin, arena, count);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private static int getColorNumber(String color){
		int count;
		switch (color) {
		case "Bleu":
			count = 1;
			break;
		case "Rouge":
			count = 2;
			break;
		case "Jaune":
			count = 3;
			break;
		case "Vert":
			count = 4;
			break;
		case "Rose":
			count = 5;
			break;
		case "Violet":
			count = 6;
			break;
		case "Orange":
			count = 7;
			break;
		case "Noir":
			count = 8;
			break;
		case "Blanc":
			count = 9;
			break;
		default:
			return 0;
		}
		return count;
	}
}