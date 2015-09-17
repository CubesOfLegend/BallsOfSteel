package com.cubesoflegend.ballsofsteel.gui;

import java.util.ArrayList;


import org.bukkit.Bukkit;

import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.IconMenu;
import com.cubesoflegend.ballsofsteel.Main;
import com.cubesoflegend.ballsofsteel.Team;

public class TeamSelectorGui {
	PluginInstance pli;
	Main plugin;
	ArrayList<Team> teams;
	
	public TeamSelectorGui(PluginInstance pli, Main plugin, ArrayList<Team> teams){
		this.pli = pli;
		this.plugin = plugin;
		this.teams = teams;
	}
	
	public void openGUI(String playername){
		IconMenu iconm;
		iconm = new IconMenu("Team", 9, new IconMenu.OptionClickEventHandler() {
			
			@Override
			public void onOptionClick(IconMenu.OptionClickEvent event) {
			}
		}, plugin);
		
		int cnt = 0;
		for (Team team : teams) {
			iconm.setOption(cnt, team.getColor().getItemStack(), team.getColor().getChatColor() + team.getName(), "Choisir l'équipe " + team.getName());
			cnt++;
		}
		
		iconm.open(Bukkit.getPlayerExact(playername));
	}

}
