package com.gmail.ne0nx3r0.coolpoints.commands;

import com.gmail.ne0nx3r0.coolpoints.CoolPointsPlugin;
import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsAccount;
import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsResponse;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CommandTake extends CoolPointsCommand{
    private final PointsManager cp;
    private final CoolPointsPlugin plugin;

    public CommandTake(CoolPointsPlugin plugin) {
        super("take","<username> <amount>","Take CP from a player","coolpoints.take");
        
        this.plugin = plugin;
        this.cp = plugin.getCoolPointsManager();
    }
    
    @Override
    public boolean execute(CommandSender cs, String[] args){        
        if(args.length < 3){
            this.send(cs,this.getUsage());
        }
        else {//args.length > 2
            String takeFromName = args[1];
            
            String sAmount = args[2];
            
            int amount;
            
            try{
                amount = Integer.parseInt(sAmount);
            }
            catch(NumberFormatException ex){
                this.sendError(cs,sAmount+" is not a valid amount!");               
                
                return true;
            }

            CoolPointsResponse takenResponse = cp.givePlayer(takeFromName,0-amount);
            
            if(takenResponse.wasSuccessful()){
                CoolPointsAccount takenFromPlayerAccount = takenResponse.getAccount();
                
                Player pTakenBy = plugin.getServer().getPlayer(takeFromName);
                
                String takeFromDisplayName;
                
                if(pTakenBy != null){
                    takeFromDisplayName = pTakenBy.getDisplayName();
                }
                else{
                    takeFromDisplayName = takeFromName;
                }
                
                String takerDisplayName;
                
                if(cs instanceof Player){
                    takerDisplayName = ((Player) cs).getDisplayName();
                }
                else {
                    takerDisplayName = "The server ";
                }
                
                plugin.getServer().broadcastMessage(takerDisplayName+ChatColor.RESET+" took "+amount+"CP from "+takeFromDisplayName+ChatColor.RESET+"("+takenFromPlayerAccount.getBalance()+"CP)!");
            }
            else {
                this.sendError(cs, takenResponse.getMessage());
            }
        }
        
        return true;
        
    }
}
