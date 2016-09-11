package com.cubesoflegend.ballsofsteel.utils;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.comze_instancelabs.minigamesapi.util.Cuboid;
import com.cubesoflegend.ballsofsteel.IArena;
import com.cubesoflegend.ballsofsteel.IPlayer;
import com.cubesoflegend.ballsofsteel.model.Team;

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
    
    public static boolean isInEnnemyBase(IArena ia, IPlayer player){
        for (Team team : ia.getTeams()) {
            if(isInCuboid(player.getPlayer().getLocation(), team.getBase().getBounds()) && player.getTeam() != team){
                return true;
            }
        }
        return false;
    }
    
    public static boolean isInCuboid(Location loc, Cuboid cub){
        //Incrementation de 1 pour eviter le decalage de 1 de hauteur.
        return isInArea(loc, cub) && loc.getY() + 1 >= cub.getLowLoc().getBlockY() && loc.getY() + 1 <= cub.getHighLoc().getBlockY();
    }
    
    public static boolean isInArea(Location loc, Cuboid cub){
        
        int lowX = cub.getLowLoc().getBlockX();
        int highX = cub.getHighLoc().getBlockX();
        
        int maxX = Math.max(lowX, highX);
        int minX = Math.min(lowX, highX);
        
        int lowZ = cub.getLowLoc().getBlockZ();
        int highZ = cub.getHighLoc().getBlockZ();
        
        int maxZ = Math.max(lowZ, highZ);
        int minZ = Math.min(lowZ, highZ);
        
        return loc.getX()>=minX && loc.getX() <= maxX && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

}
