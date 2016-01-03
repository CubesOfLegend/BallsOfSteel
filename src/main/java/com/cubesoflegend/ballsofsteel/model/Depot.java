package com.cubesoflegend.ballsofsteel.model;

import com.comze_instancelabs.minigamesapi.util.Cuboid;

public class Depot {
    private Integer diamondsCount;
    private Cuboid bounds;
    
    public Depot(Cuboid bounds) {
        this.bounds = bounds;
    }
    
    public Integer getDiamondsCount(){
        
        return this.diamondsCount;
    }
    
    public Cuboid getBounds(){
        return this.bounds;
    }
    
    public void setBounds(Cuboid bounds){
        this.bounds = bounds;
    }
}
