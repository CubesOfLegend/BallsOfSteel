package com.cubesoflegend.ballsofsteel.model;

import org.bukkit.Location;

import com.comze_instancelabs.minigamesapi.util.Cuboid;

public class Base {

    private String name;
    private Location location;
    private Cuboid bounds;

    public Base(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }

    public Cuboid getBounds() {
        return bounds;
    }

    public void setBounds(Cuboid bounds) {
        this.bounds = bounds;
    }
}
