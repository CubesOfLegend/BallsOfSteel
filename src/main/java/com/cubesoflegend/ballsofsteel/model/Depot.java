package com.cubesoflegend.ballsofsteel.model;

import com.comze_instancelabs.minigamesapi.util.Cuboid;

public class Depot {
    
    private Cuboid bounds;
    
    public Depot(Cuboid bounds) {
        this.bounds = bounds;
    }
    
    public Cuboid getBounds(){
        return this.bounds;
    }
    
    public void setBounds(Cuboid bounds){
        this.bounds = bounds;
    }
}
