package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cubesoflegend.ballsofsteel.utils.ColorUtils;

public class Team {
    private ArrayList<Player> players;
    private String name;
    private Spawn spawn;

    public Team(String name, Spawn spawn) {
        this.players = new ArrayList<Player>();
        this.name = name;
        this.spawn = spawn;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }
    
    /**
     * Récupere le nom de la team, passe la premiére lettre en majuscule, entoure le tout des balises de ChatColor et renvoie le résultat
     * @return String
     */
    public String getCharColoredName(){
        Character c = ColorUtils.bimapColorChatColor.get(this.name).getChar();
        return  "&"+ c + Character.toUpperCase(this.name.charAt(0))+this.name.substring(1) + "&"+ c;
    }
    
    public String getChatColoredName(){
        String s = Character.toUpperCase(this.name.charAt(0))+this.name.substring(1);
        return ColorUtils.bimapColorChatColor.get(this.name) + s;
    }
    
    /**
     * Renvoie la liste des joueurs
     * @param players
     */
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public Spawn getSpawn() {
        return spawn;
    }

    public void setSpawn(Spawn spawn) {
        this.spawn = spawn;
    }

}
