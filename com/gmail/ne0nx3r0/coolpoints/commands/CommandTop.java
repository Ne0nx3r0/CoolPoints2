package com.gmail.ne0nx3r0.coolpoints.commands;

import com.gmail.ne0nx3r0.coolpoints.CoolPointsPlugin;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

class CommandTop extends CoolPointsCommand{
    private final PointsManager cp;

    public CommandTop(CoolPointsPlugin plugin) {
        super("top","[username]","Check how many CP a user has","coolpoints.balance");
        
        this.cp = plugin.getCoolPointsManager();
    }
    
    @Override
    public boolean execute(CommandSender cs, String[] args){
        int topAmount = 10;
        
        if(args.length > 1){
            String sAmount = args[1];
            
            try {
                topAmount = Integer.parseInt(sAmount);
            }
            catch(NumberFormatException ex){
                this.sendError(cs,sAmount+" is not a valid number!");
            }
        }
        
        if(topAmount > 50){
            topAmount = 50;
        }
        else if(topAmount < 1){
            topAmount = 1;
        }

        Map<String,Integer> topPlayers = cp.getTopPlayers(topAmount);
        
        String[] messages = new String[topPlayers.size()+1];
        
        Iterator<Map.Entry<String, Integer>> iterator = topPlayers.entrySet().iterator();
        
        
        messages[0] = ChatColor.GOLD+"    Top "+topAmount+" players";
        
        int i = 1;
        
        while(iterator.hasNext()){
            Entry<String, Integer> entry = iterator.next();

            messages[i+3] = "    #"+(i)+" "+ChatColor.BLUE+entry.getKey() + ChatColor.WHITE+" : " + ChatColor.GREEN+entry.getValue();
            
            i++;
        }
        
        this.send(cs,messages);
        
        return true;
        
    }
}
