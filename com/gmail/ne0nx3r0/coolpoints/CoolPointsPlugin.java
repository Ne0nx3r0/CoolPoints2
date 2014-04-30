package com.gmail.ne0nx3r0.coolpoints;

import com.gmail.ne0nx3r0.coolpoints.api.CoolPointsAPI;
import com.gmail.ne0nx3r0.coolpoints.listeners.CoolPointsPlayerListener;
import com.gmail.ne0nx3r0.coolpoints.commands.CoolPointsCommandExecutor;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;
import com.ne0nx3r0.coolpoints.migration.Migrator;
import org.bukkit.plugin.java.JavaPlugin;

public class CoolPointsPlugin extends JavaPlugin{
    private PointsManager pointsManager;
    private CoolPointsAPI api;
    
    @Override
    public void onEnable() {
        this.pointsManager = new PointsManager(this);
        
        this.getCommand("cp").setExecutor(new CoolPointsCommandExecutor(this));
        
        this.getServer().getPluginManager().registerEvents(new CoolPointsPlayerListener(this), this);
        
        this.api = new CoolPointsAPI(this.pointsManager);
        
        if(this.getConfig().getBoolean("migrate_accounts")){
            Migrator m = new Migrator(this);
            
            m.migrate();
        }
    }
    
    public PointsManager getCoolPointsManager(){
        return this.pointsManager;
    }
    
    public CoolPointsAPI getCoolPointsAPI(){
        return this.api;
    }
}
