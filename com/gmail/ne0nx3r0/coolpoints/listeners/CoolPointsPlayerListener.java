package com.gmail.ne0nx3r0.coolpoints.listeners;

import com.gmail.ne0nx3r0.coolpoints.CoolPointsPlugin;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CoolPointsPlayerListener implements Listener {
    private final PointsManager cp;

    public CoolPointsPlayerListener(CoolPointsPlugin plugin) {
        this.cp = plugin.getCoolPointsManager();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(cp.giveDailyWage(e.getPlayer().getUniqueId()) || !e.getPlayer().hasPlayedBefore()){
            e.getPlayer().sendMessage("You earned a cool point for logging in today!");
        }
    }
}
