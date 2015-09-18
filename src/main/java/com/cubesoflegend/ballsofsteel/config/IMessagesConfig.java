package com.cubesoflegend.ballsofsteel.config;

import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.config.MessagesConfig;

public class IMessagesConfig extends MessagesConfig {
    
    public String you_joined_team = "&bYou joined team <team> &b!";
    
    public IMessagesConfig(JavaPlugin plugin){
        super(plugin);
        this.getConfig().addDefault("messages.you_joined_team", you_joined_team);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }
}
