package com.cubesoflegend.ballsofsteel.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Debug {
    
    public static CommandSender commandSender = Bukkit.getServer().getConsoleSender();
    final static String BOS_TAG = ChatColor.GREEN + "oo" +" [BOS] ";
    
    public static void sendMessage(String message){
        message = BOS_TAG + message;
        message = message
                .replace("<A>", ChatColor.AQUA.toString())
                .replace("<RES>", ChatColor.RESET.toString())
                .replace("<G>", ChatColor.GREEN.toString())
                .replace("<B>", ChatColor.BLUE.toString())
                .replace("<R>", ChatColor.RED.toString());
        
        commandSender.sendMessage(message);
    }
    
    public static void sendPerf(String actionName, long milliseconds){
        sendMessage("<A>[PERF] " + milliseconds + " <RES> ms for " + actionName);
    }
}
