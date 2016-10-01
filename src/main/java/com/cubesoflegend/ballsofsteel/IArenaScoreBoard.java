package com.cubesoflegend.ballsofsteel;

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
import com.comze_instancelabs.minigamesapi.util.ArenaScoreboard;
import com.comze_instancelabs.minigamesapi.util.Validator;
import com.cubesoflegend.ballsofsteel.model.Team;

public class IArenaScoreBoard extends ArenaScoreboard {
    HashMap<String, Scoreboard> ascore = new HashMap<String, Scoreboard>();
    HashMap<String, Objective> aobjective = new HashMap<String, Objective>();

    PluginInstance pli;

    public IArenaScoreBoard(PluginInstance pli, JavaPlugin plugin) {
        super(pli, plugin);
    }

    public void updateScoreboard(final JavaPlugin plugin, final IArena arena) {
        
        System.out.println("Update the scoreboard");
        
        /*
        ArrayList<Team> teams = arena.getTeams();
        
        if (!ascore.containsKey(arena.getName())) {
            ascore.put(arena.getName(), Bukkit.getScoreboardManager().getNewScoreboard());
        }
        if (!aobjective.containsKey(arena.getName())) {
            aobjective.put(arena.getName(), ascore.get(arena.getName()).registerNewObjective(arena.getName(), "dummy"));
        }
        
        
        aobjective.get(arena.getName()).setDisplaySlot(DisplaySlot.SIDEBAR);
        aobjective.get(arena.getName()).setDisplayName("Teams");
        
        //On parcours les teams de l'arene
        for(Team team : teams){
            
            aobjective.get(arena.getName()).getScore(Bukkit.getOfflinePlayer(team.getChatColoredName())).setScore(team.getPlayers().size());
            
            //On parcours les players de la team
            for(String playername : arena.getAllPlayers()){
                Player player = Bukkit.getPlayer(playername);
                
                //ascore.get(arena.getName()).resetScores(Bukkit.getOfflinePlayer(player.getName()));
                player.setScoreboard(ascore.get(arena.getName()));
            }
        }
        */
        
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
                        aobjective.get(p__).getScore(Bukkit.getOfflinePlayer(team.getChatColoredName())).setScore(team.getScore());
                    }
                    
                    p.setScoreboard(ascore.get(p__));
                }
            }
        });
        
        /*
        for (String p_ : arena.getAllPlayers()) {
            Player p = Bukkit.getPlayer(p_);
            if (!ascore.containsKey(arena.getName())) {
                ascore.put(arena.getName(), Bukkit.getScoreboardManager().getNewScoreboard());
            }
            if (!aobjective.containsKey(arena.getName())) {
                aobjective.put(arena.getName(), ascore.get(arena.getName()).registerNewObjective(arena.getName(), "dummy"));
            }

            aobjective.get(arena.getName()).setDisplaySlot(DisplaySlot.SIDEBAR);

            aobjective.get(arena.getName()).setDisplayName(MinigamesAPI.getAPI().pinstances.get(plugin).getMessagesConfig().scoreboard_title.replaceAll("<arena>", arena.getName()));

            aobjective.get(arena.getName()).getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "BLUE")).setScore(arena.blue);
            aobjective.get(arena.getName()).getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "RED")).setScore(arena.red);
            aobjective.get(arena.getName()).getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "GREEN")).setScore(arena.green);
            aobjective.get(arena.getName()).getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "YELLOW")).setScore(arena.yellow);

            p.setScoreboard(ascore.get(arena.getName()));
        }
        */
        
    }

    @Override
    public void updateScoreboard(JavaPlugin plugin, final Arena arena) {
        IArena a = (IArena) MinigamesAPI.getAPI().pinstances.get(plugin).getArenaByName(arena.getName());
        this.updateScoreboard(plugin ,a);
    }

    @Override
    public void removeScoreboard(String arena, Player p) {
        super.removeScoreboard(arena, p);
    }
}
