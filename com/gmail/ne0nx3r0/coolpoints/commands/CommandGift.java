package com.gmail.ne0nx3r0.coolpoints.commands;

import com.gmail.ne0nx3r0.coolpoints.CoolPointsPlugin;
import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsAccount;
import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsResponse;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CommandGift extends CoolPointsCommand{
    private final PointsManager cp;
    private final CoolPointsPlugin plugin;

    public CommandGift(CoolPointsPlugin plugin) {
        super("gift","<username>","Gift a CP to another player","coolpoints.gift");
        
        this.plugin = plugin;
        this.cp = plugin.getCoolPointsManager();
    }
    
    @Override
    public boolean execute(CommandSender cs, String[] args){
        if(!(cs instanceof Player)){
            this.sendError(cs,"Not from console. (try /cp give)");
            
            return true;
        }
        
        if(args.length == 1){
            this.send(cs,this.getUsage());
        }
        else {//args.length > 1
            String giftToName = args[1];

            Player gifter = (Player) cs;

            if(gifter.getName().equalsIgnoreCase(giftToName)){
                this.sendError(cs,"You can't gift yourself! Imagine how rude that would look at a party.");
                
                return true;
            }
            
            CoolPointsResponse giftedResponse = cp.giftPlayer(gifter.getUniqueId(),giftToName);
            
            if(giftedResponse.wasSuccessful()){
                CoolPointsAccount giftedPlayer = giftedResponse.getAccount();
                
                Player pGiftedTo = plugin.getServer().getPlayer(giftToName);
                
                String giftToDisplayName;
                
                if(pGiftedTo != null){
                    giftToDisplayName = pGiftedTo.getDisplayName();
                }
                else{
                    giftToDisplayName = giftToName;
                }
                
                plugin.getServer().broadcastMessage(gifter.getDisplayName()+ChatColor.RESET+" gifted a CP to "+giftToDisplayName+ChatColor.RESET+"("+giftedPlayer.getBalance()+"CP)!");
            }
            else {
                this.sendError(cs, giftedResponse.getMessage());
            }
        }
        
        return true;
        
    }
}
