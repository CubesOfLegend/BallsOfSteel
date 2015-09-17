package com.cubesoflegend.ballsofsteel;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Constants {
    public static final Map<String, Color> colors;

    static {
        colors = new HashMap<>();
        colors.put("bleu", new Color("bleu", new ItemStack(Material.WOOL, 1, (byte) 11), ChatColor.BLUE));
        colors.put("rouge", new Color("rouge", new ItemStack(Material.WOOL, 1, (byte) 14), ChatColor.RED));
        colors.put("jaune", new Color("jaune", new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.YELLOW));
        colors.put("vert", new Color("vert", new ItemStack(Material.WOOL, 1, (byte) 5), ChatColor.GREEN));
        colors.put("rose", new Color("rose", new ItemStack(Material.WOOL, 1, (byte) 6), ChatColor.LIGHT_PURPLE));
        colors.put("violet", new Color("violet", new ItemStack(Material.WOOL, 1, (byte) 10), ChatColor.DARK_PURPLE));
        colors.put("orange", new Color("orange", new ItemStack(Material.WOOL, 1, (byte) 1), ChatColor.GOLD));
        colors.put("noir", new Color("noir", new ItemStack(Material.WOOL, 1, (byte) 15), ChatColor.BLACK));
        colors.put("blanc", new Color("blanc", new ItemStack(Material.WOOL, 1, (byte) 0), ChatColor.WHITE));
    }
}
