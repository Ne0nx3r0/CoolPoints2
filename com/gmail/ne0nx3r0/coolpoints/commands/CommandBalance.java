package com.gmail.ne0nx3r0.coolpoints.commands;

import com.gmail.ne0nx3r0.coolpoints.CoolPointsPlugin;
import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsAccount;
import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsResponse;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;
import com.ne0nx3r0.util.DateTimeUtil;
import java.text.SimpleDateFormat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CommandBalance extends CoolPointsCommand{
    private final PointsManager cp;

    public CommandBalance(CoolPointsPlugin plugin) {
        super("balance","[username]","Check how many CP a user has","coolpoints.balance");
        
        this.cp = plugin.getCoolPointsManager();
    }
    
    @Override
    public boolean execute(CommandSender cs, String[] args){
        if(args.length == 1){
            if(cs instanceof Player){
                Player player = (Player) cs;
                
                CoolPointsResponse cpr = cp.getPlayerAccount(player.getUniqueId(), false);
                
                if(cpr.wasSuccessful()){
                    this.send(cs,"You have "+cpr.getAccount().getBalance());
                }
                else {
                    this.send(cs, "You have 0CP, boo!");
                }
            }
            else
            {
                this.sendError(cs,"Console doesn't need CP, he too cool as it is.");
            }
        }
        else {//args.length > 1
            String sPlayer = args[1];

            CoolPointsResponse cpr = cp.getPlayerAccount(sPlayer, false);

            if(cpr.wasSuccessful()){
                CoolPointsAccount cpa = cpr.getAccount();

                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
                String date = DATE_FORMAT.format(cpa.getFirstJoined());
                
                this.send(cs,new String[]{
                    cpa.getUsername()+" has "+cpa.getBalance()+"CP",
                    "Member since "+date+" ("+DateTimeUtil.getTimeSinceString(cpa.getFirstJoined())+")"
                });
            }
            else {
                this.sendError(cs, cpr.getMessage());
            }
        }
        
        return true;
        
    }
}
