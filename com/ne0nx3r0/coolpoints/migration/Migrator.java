package com.ne0nx3r0.coolpoints.migration;

import com.gmail.ne0nx3r0.coolpoints.CoolPointsPlugin;
import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsAccount;
import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsResponse;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;
import com.ne0nx3r0.coolpoints.migration.com.evilmidget38.UUIDFetcher;
import com.ne0nx3r0.coolpoints.migration.lib.PatPeter.SQLibrary.SQLite;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Migrator {
    private final CoolPointsPlugin plugin;
    private SQLite sqlite;
    
    public Migrator(CoolPointsPlugin plugin) {
        this.plugin = plugin;
    }

    public void migrate() {
        System.out.println("Migrating balances from CoolPoints v1 database");

        this.loadDB();

        List<AccountToMigrate> accountsToMigrate = this.getAllUserAccounts();

        System.out.println("Found "+accountsToMigrate.size()+" accounts to migrate");
        
        PointsManager cp = plugin.getCoolPointsManager();
        
        ArrayList<String> list = new ArrayList<>();
        
        for(AccountToMigrate account : accountsToMigrate){
            list.add(account.username);
        }
        
        UUIDFetcher fetcher = new UUIDFetcher(list);

        Map<String, UUID> playerUUIDs = null;
        
        try {
            playerUUIDs = fetcher.call();
        } 
        catch (Exception e) {
            plugin.getLogger().warning("Exception while running UUIDFetcher");
            
            e.printStackTrace();
            
            return;
        }
        
        Map<String, UUID> playerUUIDsLower = new HashMap<>();
        
        // convert names to lower case...
        for(String username : playerUUIDs.keySet()){
            playerUUIDsLower.put(username.toLowerCase(),playerUUIDs.get(username));
        }
        
        for(AccountToMigrate account : accountsToMigrate){
            UUID uniqueId = playerUUIDsLower.get(account.username);

            if(uniqueId != null){
                cp.migrateAccount(uniqueId,account.username,account.balance,account.firstJoined);
            }
            else{
                plugin.getLogger().log(Level.WARNING, "UCAN for {0}({1}) with {2}! - null UUID!", 
                        new Object[]{account.username, uniqueId, account.balance});
            }
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Migrator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void loadDB() {
        this.sqlite = new SQLite(
            plugin.getLogger(),            
            "CoolPoints",
            "points",
            plugin.getDataFolder().getAbsolutePath()
        );

        try {
            sqlite.open();
        } 
        catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);

            return;
        }
    }

    public List<AccountToMigrate> getAllUserAccounts(){
        List<AccountToMigrate> userBalances = new ArrayList<>();

        try (PreparedStatement statement = sqlite.prepare("SELECT username,points,giftedToday,receivedAllowanceToday,firstJoined FROM player LIMIT 100000;")){
            try(ResultSet result = statement.executeQuery()){
                while(result.next())
                {
                    int balance = result.getInt("points");
                    userBalances.add(new AccountToMigrate(
                        result.getString("username"),
                        balance >= 0 ? balance : 0,
                        new java.util.Date(Long.parseLong(result.getString("firstJoined")) * 1000)
                    ));
                }
            }
        } 
        catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        }

        return userBalances;
    }
}
