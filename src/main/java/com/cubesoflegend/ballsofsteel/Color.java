package com.cubesoflegend.ballsofsteel;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class Color{
	private String name;
	private ChatColor chatColor;
	private ItemStack itemStack;
	
	public Color(String name, ItemStack itemStack, ChatColor chatColor){
		this.name = name;
		this.chatColor = chatColor;
		this.itemStack = itemStack;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ChatColor getChatColor() {
		return chatColor;
	}

	public void setChatColor(ChatColor chatColor) {
		this.chatColor = chatColor;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

}
