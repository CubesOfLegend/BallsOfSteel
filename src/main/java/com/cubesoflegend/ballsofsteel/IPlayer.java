package com.cubesoflegend.ballsofsteel;

import org.bukkit.entity.Player;

import com.cubesoflegend.ballsofsteel.model.Team;

public class IPlayer{
    
    private Player player;
    private Team team;
    
    private Integer diamondsMined;

    public IPlayer(Player p){
        this.player = p;
        this.diamondsMined = 0;
    }
    
    public Integer getDiamondsMined() {
        return diamondsMined;
    }

    public void setDiamondsMined(Integer diamondsMined) {
        this.diamondsMined = diamondsMined;
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
