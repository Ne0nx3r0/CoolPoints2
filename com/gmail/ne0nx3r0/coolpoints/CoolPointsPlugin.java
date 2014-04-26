package com.gmail.ne0nx3r0.coolpoints;

import com.gmail.ne0nx3r0.coolpoints.listeners.CoolPointsPlayerListener;
import com.gmail.ne0nx3r0.coolpoints.commands.CoolPointsCommandExecutor;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CoolPointsPlugin extends JavaPlugin{
    private PointsManager pointsManager;
    
    @Override
    public void onEnable() {
        this.pointsManager = new PointsManager(this);
        
        this.getCommand("cp").setExecutor(new CoolPointsCommandExecutor(this));
        
        this.getServer().getPluginManager().registerEvents(new CoolPointsPlayerListener(this), this);
    }
    
    public PointsManager getCoolPointsManager(){
        return this.pointsManager;
    }
}
