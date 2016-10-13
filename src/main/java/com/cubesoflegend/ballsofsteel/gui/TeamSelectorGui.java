package com.cubesoflegend.ballsofsteel.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.IconMenu;
import com.cubesoflegend.ballsofsteel.IArena;
import com.cubesoflegend.ballsofsteel.Main;
import com.cubesoflegend.ballsofsteel.config.IMessagesConfig;
import com.cubesoflegend.ballsofsteel.model.Team;
import com.cubesoflegend.ballsofsteel.utils.ColorUtils;
import com.cubesoflegend.ballsofsteel.utils.Debug;

public class TeamSelectorGui {
    PluginInstance pli;
    Main plugin;
    HashMap<String, Team> teams;
    HashMap<Integer, Team> mapOptionTeam;
    public HashMap<String, IconMenu> lasticonm = new HashMap<String, IconMenu>();
    HashMap<Player, Team> mapPlayerTeam = new HashMap<Player, Team>();
    Logger logger;

    public TeamSelectorGui(PluginInstance pli, Main plugin, ArrayList<Team> teams) {
        
        logger = plugin.logger;
        StopWatch timer = new StopWatch();
        timer.start();
        
        this.pli = pli;
        this.plugin = plugin;
        this.teams = new HashMap<String, Team>();
        for (Team team : teams) {
            this.teams.put(team.getName(), team);
        }
        
        timer.stop();
        logger.log(Level.INFO, Debug.formatPerf("TeamSelectorGui::contruct()", timer.getTime()));
    }

    public void openGUI(String playername) {
        
        StopWatch timer = new StopWatch();
        timer.start();
        
        IconMenu iconm;
        //Si le joueur a déjà instancié un inconmenu, alors on le récupére si non on en crée un nouveau (Evite doublons)
        if(lasticonm.containsKey(playername)){
            iconm = lasticonm.get(playername);
        }
        else
        {
            iconm = new IconMenu("Team", 9, new IconMenu.OptionClickEventHandler() {
                @Override
                public void onOptionClick(IconMenu.OptionClickEvent event) {
                    
                    StopWatch timer = new StopWatch();
                    timer.start();
                    
                    //Permet de prendre le joueur qui a cliqué sur le menu et non tout les autres
                    if(event.getPlayer().getName().equalsIgnoreCase(playername)){
                        if(pli.containsGlobalPlayer(playername)){
                            Player p = Bukkit.getPlayer(playername);
                            //global_players contient en fait un hashmap <playername,arena> :o
                            IArena a = (IArena) pli.getArenaByGlobalPlayer(playername);
                            Team team = mapOptionTeam.get(event.getPosition());
                            a.changePlayerToTeam(p, team);
                            IMessagesConfig config = (IMessagesConfig) pli.getMessagesConfig();
                            p.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.you_joined_team.replaceAll("<team>", team.getChatColoredName())));
                        }
                    }
                    event.setWillClose(true);
                    
                    timer.stop();
                    logger.log(Level.INFO, Debug.formatPerf("TeamSelectorGui:OpenGui():onOptionClick()", timer.getTime()));
                    
                }
            }, plugin);
        }
        int cnt = 0;
        mapOptionTeam = new HashMap<Integer, Team>();
        
        for (Map.Entry<String, Team> entry : teams.entrySet()) {
            Team team = entry.getValue();
            iconm.setOption(cnt, ColorUtils.bimapColorItemStack.get(team.getName()), team.getChatColoredName(), "Choisir l'équipe " + team.getChatColoredName());
            mapOptionTeam.put(cnt, team);
            cnt++;
        }
        
        iconm.open(Bukkit.getPlayerExact(playername));
        lasticonm.put(playername, iconm);
        
        timer.stop();
        logger.log(Level.INFO, Debug.formatPerf("TeamSelectorGui:OpenGui()", timer.getTime()));
        
    }
}
