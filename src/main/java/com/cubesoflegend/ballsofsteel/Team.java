package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Team {
	private ArrayList<Player> players;
	private Color color;
	private Spawn spawn;
	private String name;
	
	public Team(Color color, Spawn spawn){
		this.color = color;
		this.name = Character.toUpperCase(color.getName().charAt(0)) + color.getName().substring(1);
		this.spawn = spawn;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public String getName(){
		return name;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public void addPlayer(Player player){
		this.players.add(player);
	}
	
	public void removePlayer(Player player){
		this.players.remove(player);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Spawn getSpawn() {
		return spawn;
	}

	public void setSpawn(Spawn spawn) {
		this.spawn = spawn;
	}
	
	

}
