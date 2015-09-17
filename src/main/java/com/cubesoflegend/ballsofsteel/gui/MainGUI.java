package com.cubesoflegend.ballsofsteel.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.cubesoflegend.ballsofsteel.Main;

public class MainGUI {
	PluginInstance pli;
	
	
	public MainGUI(PluginInstance pli, Player p){
		if(pli.global_players.containsKey(p.getName())){
			ItemStack teamselector = new ItemStack(Material.WOOL, 1, (byte) 14);
			ItemMeta itemm = teamselector.getItemMeta();
			itemm.setDisplayName(ChatColor.RED + "Team");
			teamselector.setItemMeta(itemm);
			p.getInventory().setItem(0, teamselector);
			p.updateInventory();
		}
	}
	
}
