package com.cubesoflegend.ballsofsteel.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
    
    public static String formatPerf(String actionName, long milliseconds){
        return "[PERF] " + milliseconds + " ms for " + actionName;
    }
    
    public static Logger getLogger(){
        
        Logger logger = Logger.getLogger("PerfLog");
        FileHandler fh;
        
        try {  

            String path = new File(".").getCanonicalPath();
                        
            DateFormat dateFormat = new SimpleDateFormat("d_m_Y_HH_mm_ss");
            String logPath = path + "\\plugins\\BallsOfSteel\\logs\\" + dateFormat.format(new Date()) + ".log";
            File file = new File(logPath);
            file.getParentFile().mkdirs();
            
            fh = new FileHandler(path + "\\plugins\\BallsOfSteel\\logs\\" + dateFormat.format(new Date()) + ".log");  
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);  

        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  

        return logger;
        
    }
}
