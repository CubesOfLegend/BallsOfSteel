package com.cubesoflegend.ballsofsteel.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.HashBiMap;

public final class ColorUtils {
    
    public static final HashBiMap<String, ItemStack> bimapColorItemStack;
    static{
        
        bimapColorItemStack = HashBiMap.create();
        bimapColorItemStack.put("bleu", new ItemStack(Material.WOOL, 1, (byte) 11));
        bimapColorItemStack.put("rouge", new ItemStack(Material.WOOL, 1, (byte) 14));
        bimapColorItemStack.put("jaune", new ItemStack(Material.WOOL, 1, (byte) 4));
        bimapColorItemStack.put("vert", new ItemStack(Material.WOOL, 1, (byte) 5));
        bimapColorItemStack.put("rose", new ItemStack(Material.WOOL, 1, (byte) 6));
        bimapColorItemStack.put("violet", new ItemStack(Material.WOOL, 1, (byte) 10));
        bimapColorItemStack.put("orange", new ItemStack(Material.WOOL, 1, (byte) 1));
        bimapColorItemStack.put("noir", new ItemStack(Material.WOOL, 1, (byte) 15));
        bimapColorItemStack.put("blanc", new ItemStack(Material.WOOL, 1, (byte) 0));
        
    }
    
    public static final HashBiMap<String, ChatColor> bimapColorChatColor;
    static{
        bimapColorChatColor = HashBiMap.create();
        bimapColorChatColor.put("bleu", ChatColor.BLUE);
        bimapColorChatColor.put("rouge", ChatColor.RED);
        bimapColorChatColor.put("jaune", ChatColor.YELLOW);
        bimapColorChatColor.put("vert", ChatColor.GREEN);
        bimapColorChatColor.put("rose", ChatColor.LIGHT_PURPLE);
        bimapColorChatColor.put("violet",ChatColor.DARK_PURPLE);
        bimapColorChatColor.put("orange", ChatColor.GOLD);
        bimapColorChatColor.put("noir", ChatColor.BLACK);
        bimapColorChatColor.put("blanc", ChatColor.WHITE);
    }
    
}
