package com.gmail.ne0nx3r0.coolpoints.commands;

import com.gmail.ne0nx3r0.coolpoints.CoolPointsPlugin;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CommandRank extends CoolPointsCommand{
    private final PointsManager cp;

    public CommandRank(CoolPointsPlugin plugin) {
        super("rank","[username]","Check the rank of a user","coolpoints.rank");
        
        this.cp = plugin.getCoolPointsManager();
    }
    
    @Override
    public boolean execute(CommandSender cs, String[] args){
        if(args.length == 1){
            if(cs instanceof Player){
                Player player = (Player) cs;
                
                int playerRank = cp.getPlayerRank(player.getUniqueId());
                
                this.send(cs,"You are ranked "+playerRank);
            }
            else
            {
                this.sendError(cs,"Console has ALL the rank!");
            }
        }
        else {//args.length > 1
            String sPlayer = args[1];

            int playerRank = cp.getPlayerRank(sPlayer);

            if(playerRank != -1){
                this.send(cs,sPlayer+" is ranked #"+playerRank);
            }
            else {
                this.sendError(cs, "Unable to get rank for "+sPlayer);
            }
        }
        
        return true;
        
    }
}
