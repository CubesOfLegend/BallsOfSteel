package com.cubesoflegend.ballsofsteel.model;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.cubesoflegend.ballsofsteel.IPlayer;
import com.cubesoflegend.ballsofsteel.utils.ColorUtils;

public class Team {
    
    private ArrayList<IPlayer> players;
    private ArrayList<Block> blocks;
    private String name;
    private Base base;
    private Depot depot;
    private ItemStack itemCollect;
    private Integer score;

    public Team(String name, Base spawn, Depot depot, ItemStack itemCollect) {
        this.players = new ArrayList<IPlayer>();
        this.blocks = new ArrayList<Block>();
        this.name = name;
        this.base = spawn;
        this.depot = depot;
        this.itemCollect = itemCollect;
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

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }
    
    public Depot getDepot(){
        return this.depot;
    }
    
    public void setDepot(Depot depot){
        this.depot = depot;
    }
    
    public void teleportTeam(Location location){
        for (IPlayer player : players) {
            player.getPlayer().teleport(location);
        }
    }
    
    public void setItemCollect(ItemStack itemStack){
        this.itemCollect = itemStack;
    }
    
    public ItemStack getItemCollect(){
        return this.itemCollect;
    }
    
    public Integer getScore(){
        return this.score;
    }
    
    public void setScore(Integer score){
        this.score = score;
    }
    
    public ArrayList<Block> getBlocks(){
        return this.blocks;
    }
    
    public void setBlocks(ArrayList<Block> blocks){
        this.blocks = blocks;
    }

}
