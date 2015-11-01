package com.cubesoflegend.ballsofsteel.model;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.cubesoflegend.ballsofsteel.IPlayer;
import com.cubesoflegend.ballsofsteel.utils.ColorUtils;

public class Team {
    private ArrayList<IPlayer> players;
    private String name;
    private Spawn spawn;

    public Team(String name, Spawn spawn) {
        this.players = new ArrayList<IPlayer>();
        this.name = name;
        this.spawn = spawn;
    }

    public ArrayList<IPlayer> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }
    
    /**
     * Récupere le nom de la team, passe la premiére lettre en majuscule, entoure le tout des balises de ChatColor et renvoie le résultat
     * @return String
     */
    public String getChatColoredName(){
        Character c = ColorUtils.bimapColorChatColor.get(this.name).getChar();
        String str = "&"+ c + Character.toUpperCase(this.name.charAt(0))+this.name.substring(1) + "&"+ c;
        return ChatColor.translateAlternateColorCodes('&', str);
    }
    
    public ChatColor getChatColor(){
        return ColorUtils.bimapColorChatColor.get(this.name);
    }
    
    /**
     * Renvoie la liste des joueurs
     * @param players
     */
    public void setPlayers(ArrayList<IPlayer> players) {
        this.players = players;
    }

    public void addPlayer(IPlayer player) {
        this.players.add(player);
    }

    public void removePlayer(IPlayer player) {
        this.players.remove(player);
    }

    public Spawn getSpawn() {
        return spawn;
    }

    public void setSpawn(Spawn spawn) {
        this.spawn = spawn;
    }
    
    public void teleportTeam(Location location){
        for (IPlayer player : players) {
            player.getPlayer().teleport(location);
        }
    }

}
