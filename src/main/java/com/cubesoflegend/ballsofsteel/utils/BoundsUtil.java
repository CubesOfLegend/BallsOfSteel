package com.cubesoflegend.ballsofsteel.utils;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.comze_instancelabs.minigamesapi.util.Cuboid;

public class BoundsUtil {
    public static void addBounds(char lowOrHigh,Location loc, FileConfiguration config, String base){
        
        if(lowOrHigh=='l'){
            base = base + ".bounds.low.";
        }
        else
        {
            base = base + ".bounds.high.";
        }
        
        config.set( base + "x", loc.getBlockX());
        config.set( base + "y", loc.getBlockY());
        config.set( base + "z", loc.getBlockZ());
    }
    
    public static void isInCuboid(Location loc, Cuboid cub){
    }
    
    public static boolean isInArea(Location loc, Cuboid cub){
        return loc.getX()>=cub.getLowLoc().getBlockX() && loc.getX()<=cub.getHighLoc().getBlockX() && loc.getZ()>=cub.getLowLoc().getBlockZ() && loc.getZ()<=cub.getHighLoc().getBlockZ();
    }

}
