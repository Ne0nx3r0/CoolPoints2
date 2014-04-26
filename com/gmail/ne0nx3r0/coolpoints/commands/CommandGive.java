package com.gmail.ne0nx3r0.coolpoints.commands;

import com.gmail.ne0nx3r0.coolpoints.CoolPointsPlugin;
import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsAccount;
import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsResponse;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CommandGive extends CoolPointsCommand{
    private final PointsManager cp;
    private final CoolPointsPlugin plugin;

    public CommandGive(CoolPointsPlugin plugin) {
        super("give","<username> <amount>","Give CP to a player","coolpoints.give");
        
        this.plugin = plugin;
        this.cp = plugin.getCoolPointsManager();
    }
    
    @Override
    public boolean execute(CommandSender cs, String[] args){        
        if(args.length < 3){
            this.send(cs,this.getUsage());
        }
        else {//args.length > 2
            String giveToName = args[1];
            
            String sAmount = args[2];
            
            int amount;
            
            try{
                amount = Integer.parseInt(sAmount);
            }
            catch(NumberFormatException ex){
                this.sendError(cs,sAmount+" is not a valid amount!");               
                
                return true;
            }

            CoolPointsResponse giveResponse = cp.givePlayer(giveToName,amount);
            
            if(giveResponse.wasSuccessful()){
                CoolPointsAccount givenToAccount = giveResponse.getAccount();
                
                Player pGivenTo = plugin.getServer().getPlayer(giveToName);
                
                String giveToDisplayName;
                
                if(pGivenTo != null){
                    giveToDisplayName = pGivenTo.getDisplayName();
                }
                else{
                    giveToDisplayName = giveToName;
                }
                
                String giverDisplayName;
                
                if(cs instanceof Player){
                    giverDisplayName = ((Player) cs).getDisplayName();
                }
                else {
                    giverDisplayName = "The server ";
                }
                
                plugin.getServer().broadcastMessage(giverDisplayName+ChatColor.RESET+" gave "+amount+"CP to "+giveToDisplayName+ChatColor.RESET+"("+givenToAccount.getBalance()+"CP)!");
            }
            else {
                this.sendError(cs, giveResponse.getMessage());
            }
        }
        
        return true;
        
    }
}
