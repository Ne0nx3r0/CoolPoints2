package com.gmail.ne0nx3r0.coolpoints.commands;

import com.gmail.ne0nx3r0.coolpoints.CoolPointsPlugin;
import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsResponse;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CoolPointsCommandExecutor implements CommandExecutor {
    private final Map<String,CoolPointsCommand> subCommands;
    private final PointsManager cp;

    public CoolPointsCommandExecutor(CoolPointsPlugin plugin) {
        this.subCommands = new HashMap<>();
        
        this.registerSubcommand(new CommandBalance(plugin));
        this.registerSubcommand(new CommandGift(plugin));
        this.registerSubcommand(new CommandGive(plugin));
        this.registerSubcommand(new CommandTake(plugin));
        this.registerSubcommand(new CommandTop(plugin));
        this.registerSubcommand(new CommandRank(plugin));
        //this.registerSubcommand(new CommandWho(plugin));
        //this.registerSubcommand(new CommandReset(plugin));
        //this.registerSubcommand(new CommandCreateProfileWith(plugin));
        
        this.cp = plugin.getCoolPointsManager();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        if(args.length == 0 || args[0].equals("?")) {
            this.sendUsage(cs);
            
            return true;
        }

        CoolPointsCommand cpCommand = this.subCommands.get(args[0]);
        
        if(cpCommand != null) {
            if(cs.hasPermission(cpCommand.getPermissionNode())) {
                return cpCommand.execute(cs,args);
            }
            else {
                cpCommand.send(cs, 
                    ChatColor.RED+"You do not have permission to "+cpCommand.getAction(),
                    ChatColor.RED+"Required node: "+ChatColor.WHITE+cpCommand.getPermissionNode()
                );
            }
        }
        
        return false;
    }
    
    private void sendUsage(CommandSender cs) {
        cs.sendMessage(ChatColor.GRAY+"---"+ChatColor.GREEN+" CoolPoints "+ChatColor.GRAY+"---");
        cs.sendMessage("Here are the commands you have access to:");
        
        for(CoolPointsCommand lc : this.subCommands.values()) {
            if(cs.hasPermission(lc.getPermissionNode())) {
                cs.sendMessage(lc.getUsage());
            }
        }

        if(cs instanceof Player){
            Player player = (Player) cs;
            
            CoolPointsResponse response = this.cp.getPlayerAccount(player.getUniqueId(), false);

            if(response.wasSuccessful()){
                cs.sendMessage("You have "+response.getAccount().getBalance()+"CP");
            }
            else {
                cs.sendMessage("You have "+0+"CP :(... BOO!");
            }
        }
    }
    
    public final void registerSubcommand(CoolPointsCommand lc) {
        this.subCommands.put(lc.getName(), lc);
    }
}
