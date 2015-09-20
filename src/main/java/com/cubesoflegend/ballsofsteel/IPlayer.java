package com.cubesoflegend.ballsofsteel;

import org.bukkit.entity.Player;

public class IPlayer{
    Player player;
    Team team;
    
    public IPlayer(Player p){
        this.player = p;
    }
    
    public void setTeam(Team t){
        this.team = t;
    }
    
    public Team getTeam(){
        return this.team;
    }
    
    public Player getPlayer(){
        return this.player;
    }
}
