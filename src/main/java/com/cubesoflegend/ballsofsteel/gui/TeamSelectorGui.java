package com.cubesoflegend.ballsofsteel.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.IconMenu;
import com.cubesoflegend.ballsofsteel.Main;
import com.cubesoflegend.ballsofsteel.Team;
import com.cubesoflegend.ballsofsteel.config.IMessagesConfig;
import com.cubesoflegend.ballsofsteel.utils.ColorUtils;

public class TeamSelectorGui {
    PluginInstance pli;
    Main m;
    HashMap<String, Team> teams;
    HashMap<Integer, Team> mapOptionTeam;
    public HashMap<String, IconMenu> lasticonm = new HashMap<String, IconMenu>();

    public TeamSelectorGui(PluginInstance pli, Main plugin, HashMap<String ,Team> teams) {
        this.pli = pli;
        this.m = plugin;
        this.teams = teams;
        
        
    }

    public void openGUI(String playername) {
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
                    if(pli.global_players.containsKey(playername)){
                        Player p = Bukkit.getPlayer(playername);
                        Team team = mapOptionTeam.get(event.getPosition());
                        team.addPlayer(p);
                        IMessagesConfig config = (IMessagesConfig) pli.getMessagesConfig();
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.you_joined_team.replaceAll("<team>", team.getColoredName())));
                    }
                    event.setWillClose(true);
                }
            }, m);
        }
        int cnt = 0;
        mapOptionTeam = new HashMap<Integer, Team>();
        for (Map.Entry<String, Team> entry : teams.entrySet()) {
            Team team = entry.getValue();
            iconm.setOption(cnt, ColorUtils.bimapColorItemStack.get(team.getName()), ColorUtils.bimapColorChatColor.get(team.getName()) + team.getName(),
                    "Choisir l'équipe " + team.getName());
            mapOptionTeam.put(cnt, team);
            cnt++;
        }
        iconm.open(Bukkit.getPlayerExact(playername));
        lasticonm.put(playername, iconm);
    }
}
