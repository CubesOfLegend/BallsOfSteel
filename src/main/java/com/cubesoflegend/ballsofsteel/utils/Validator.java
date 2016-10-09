package com.cubesoflegend.ballsofsteel.utils;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.PluginInstance;

public class Validator {
    PluginInstance pli;
    
    public Validator(PluginInstance pli) {
        this.pli = pli;
    }
    
    public boolean isValidTeamArgument(String arg){
        return ColorUtils.bimapColorChatColor.containsKey(arg);
    }
    
    public boolean isValidArenaArgument(String arg){
        return this.pli.getArenaByName(arg) instanceof Arena;
    }
    
    public boolean spawnExist(String arenaname, String team){
        return pli.getArenasConfig().getConfig().contains("arenas."+arenaname+".spawns.spawn"+team);
    }
    
    public boolean isNumeric(String arg){
        try {
            Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
