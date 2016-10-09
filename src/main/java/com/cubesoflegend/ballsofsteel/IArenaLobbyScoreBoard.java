package com.cubesoflegend.ballsofsteel;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.ArenaLobbyScoreboard;
import com.comze_instancelabs.minigamesapi.util.Validator;
import com.cubesoflegend.ballsofsteel.model.Team;

public class IArenaLobbyScoreBoard extends ArenaLobbyScoreboard {
    
    HashMap<String, Scoreboard> ascore = new HashMap<String, Scoreboard>();
    HashMap<String, Objective> aobjective = new HashMap<String, Objective>();
    
    int initialized = 0; // 0 = false; 1 = true;
    boolean custom = false;

    PluginInstance pli;

    ArrayList<String> loaded_custom_strings = new ArrayList<String>();

    public IArenaLobbyScoreBoard(PluginInstance pli, JavaPlugin plugin) {
        super(pli, plugin);
    }
    
    public void updateScoreboard(final JavaPlugin plugin, final IArena arena) {
        
        if (!arena.getShowScoreboard()) {
            return;
        }

        if (pli == null) {
            pli = MinigamesAPI.getAPI().getPluginInstance(plugin);
        }

        Bukkit.getScheduler().runTask(MinigamesAPI.getAPI(), new Runnable() {
            public void run() {
                
                for (String p__ : arena.getAllPlayers()) {
                    
                    if (!Validator.isPlayerValid(plugin, p__, arena)) {
                        return;
                    }
                    
                    Player p = Bukkit.getPlayer(p__);
                    if (!ascore.containsKey(p__)) {
                        ascore.put(p__, Bukkit.getScoreboardManager().getNewScoreboard());
                    }
                    
                    if (!aobjective.containsKey(p__)) {
                        aobjective.put(p__, ascore.get(p__).registerNewObjective(p__, "dummy"));
                        aobjective.get(p__).setDisplaySlot(DisplaySlot.SIDEBAR);
                        aobjective.get(p__).setDisplayName("Teams of : " + pli.getMessagesConfig().scoreboard_lobby_title.replaceAll("<arena>", arena.getInternalName()));
                    }
                    
                    for (Team team : arena.getTeams()) {
                        aobjective.get(p__).getScore(Bukkit.getOfflinePlayer(team.getChatColoredName())).setScore(team.getPlayers().size());                    
                    }
                    
                    p.setScoreboard(ascore.get(p__));
                }
            }
        });
    }
    
    @Override
    public void updateScoreboard(JavaPlugin plugin, final Arena arena) {
        IArena a = (IArena) MinigamesAPI.getAPI().getPluginInstance(plugin).getArenaByName(arena.getInternalName());
        this.updateScoreboard(plugin ,a);
    }
    
    @Override 
    public void removeScoreboard(String arena, Player p){
        super.removeScoreboard(arena, p);
    }
}
