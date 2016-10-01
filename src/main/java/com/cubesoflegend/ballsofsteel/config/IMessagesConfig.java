package com.cubesoflegend.ballsofsteel.config;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.config.MessagesConfig;

public class IMessagesConfig extends MessagesConfig {
    
    public String you_joined_team = "&aYou joined team <team>&a !";
    public String team_not_exists = "&cThe team <team> does not exists";
    public String possible_teams_are = "&ePossible teams are <teams>";
    public String successfully_set_team_component = "&a Successfully set <component> for team <team>";
    public String not_allowed_enter_in_base= "&c You're not allowed to enter in <team>&c base";
    
    public IMessagesConfig(JavaPlugin plugin){
        super(plugin);
    }
    
    @Override
    public void init() {
        
        //Override kills messages
        this.player_died = "&c<player> died. (Lost <itemcount> <item>)";
        this.you_got_a_kill = "&aYou killed &2<player>! (Lost <itemcount> <item>)";
        this.player_was_killed_by = "&4<player> &cwas killed by &4<killer>&c! (Lost <itemcount> <item>)";
        
        this.getConfig().addDefault("messages.player_died", this.player_died);
        this.getConfig().addDefault("messages.you_got_a_kill", this.you_got_a_kill);
        this.getConfig().addDefault("messages.player_was_killed_by", this.player_was_killed_by);
        
        
        this.getConfig().addDefault("messages.you_joined_team", you_joined_team);
        this.getConfig().addDefault("messages.team_not_exists", team_not_exists);
        this.getConfig().addDefault("messages.possible_teams_are", possible_teams_are);
        this.getConfig().addDefault("messages.successfully_set_team_component", successfully_set_team_component);
        this.getConfig().addDefault("messages.not_allowed_enter_in_base", not_allowed_enter_in_base);
        
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        
        this.player_died = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.player_died"));
        this.you_got_a_kill = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.you_got_a_kill"));
        this.player_was_killed_by = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.player_was_killed_by"));
        
        this.you_joined_team = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_joined_team"));
        this.team_not_exists = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.team_not_exists"));
        this.possible_teams_are = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.possible_teams_are"));
        this.successfully_set_team_component = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successfully_set_team_component"));
        this.not_allowed_enter_in_base = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.not_allowed_enter_in_base"));
        
        super.init();
    }
}
