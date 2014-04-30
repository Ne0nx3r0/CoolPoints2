package com.gmail.ne0nx3r0.coolpoints.points;

import com.gmail.ne0nx3r0.coolpoints.CoolPointsPlugin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PointsManager {
    private final String TBL_ACCOUNTS;
    private final Logger logger;
    private Connection con;
    
    public PointsManager(CoolPointsPlugin plugin) {
        this.logger = plugin.getLogger();
        
        ConfigurationSection dbConfig = plugin.getConfig().getConfigurationSection("database");
        
        String prefix = dbConfig.getString("prefix","");
        String hostname = dbConfig.getString("hostname","localhost");
        String port = dbConfig.getString("port","3306");
        String database = dbConfig.getString("database");
        String username = dbConfig.getString("username");
        String password = dbConfig.getString("password");
        
        this.TBL_ACCOUNTS = prefix+"accounts";

	try {
            Class.forName("com.mysql.jdbc.Driver");
	} 
        catch (ClassNotFoundException ex) {
            this.logger.log(Level.SEVERE, null, ex);
            
            this.logger.log(Level.SEVERE,"No MySQL JDBC driver found (that's bad)");
            
            return;
	}
        
	try {
            this.con = DriverManager.getConnection("jdbc:mysql://"+hostname+":"+port+"/"+database,username,password);
	} 
        catch (SQLException ex) {
            this.logger.log(Level.SEVERE, null, ex);

            System.out.println("Database connection failed!");

            return;
	}
 
	if (this.con == null) {
            this.logger.log(Level.SEVERE,"Unable to connect to the database");
            
            return;
	}

        try(ResultSet tableExistsResultSet = this.con.getMetaData().getTables(null, null, this.TBL_ACCOUNTS, null)){
            if(!tableExistsResultSet.next()) {
                this.logger.log(Level.INFO, "Creating cool points table: {0}", this.TBL_ACCOUNTS);
                
                String createAccountsQuery = "CREATE TABLE IF NOT EXISTS ###TABLE_ACCOUNTS### (  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,  `uuid` VARCHAR(36) NOT NULL,  `username` VARCHAR(16) NOT NULL,  `first_joined` DATETIME NOT NULL,  `last_seen` DATETIME NOT NULL,  `last_gifted` DATETIME NOT NULL,  `balance` INT UNSIGNED NOT NULL,  PRIMARY KEY (`id`),  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC),  UNIQUE INDEX `username_UNIQUE` (`username` ASC))ENGINE = InnoDB";
                
                createAccountsQuery = createAccountsQuery.replace("###TABLE_ACCOUNTS###",this.TBL_ACCOUNTS);
                
                try(PreparedStatement createAccountsTable = this.con.prepareStatement(createAccountsQuery)){
                    createAccountsTable.execute();
                } 
                catch (SQLException ex) {
                    this.logger.log(Level.SEVERE, null, ex);

                    this.logger.severe("Unable to create cool points tables!!");

                    this.con = null;
            
                    return;
                }
            }
        } 
        catch (SQLException ex) {
            this.logger.log(Level.SEVERE, null, ex);
            
            this.logger.severe("Unable to check if the database exists!");
        }
    }
    
    public CoolPointsResponse getPlayerAccount(UUID uuid,boolean createIfNotExists){
        try(PreparedStatement getAccount = this.con.prepareStatement("SELECT id,username,first_joined,last_seen,last_gifted,balance FROM "+this.TBL_ACCOUNTS+" WHERE uuid = ?")){
            getAccount.setString(1, uuid.toString());
            
            try(ResultSet result = getAccount.executeQuery()){
                if(result.next()){
                    return new CoolPointsResponse(CoolPointsResponseType.SUCCESS,new CoolPointsAccount(
                            result.getInt("id"),
                            uuid,
                            result.getString("username"),
                            result.getInt("balance"),
                            (Date) result.getTimestamp("first_joined"),
                            (Date) result.getTimestamp("last_seen"),
                            (Date) result.getTimestamp("last_gifted")
                    ));
                }
                else if(createIfNotExists){
                    Player player = Bukkit.getPlayer(uuid);
                    
                    if(player != null){
                        return this.createAccountFor(player.getUniqueId(),player.getName());
                    }
                    
                    return new CoolPointsResponse(CoolPointsResponseType.FAILURE_NO_ACCOUNT,"Unable to create a CP account for an offline player!");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PointsManager.class.getName()).log(Level.SEVERE, null, ex);
            
            return new CoolPointsResponse(CoolPointsResponseType.FAILURE_DATABASE,"A database error occurred!");
        }
        
        return new CoolPointsResponse(CoolPointsResponseType.FAILURE_NO_ACCOUNT,"No account exists for that UUID!");
    }

    public CoolPointsResponse getPlayerAccount(String playerName,boolean createIfNotExists){
        playerName = playerName.toLowerCase();
        
        try(PreparedStatement getAccount = this.con.prepareStatement("SELECT id,uuid,username,first_joined,last_seen,last_gifted,balance FROM "+this.TBL_ACCOUNTS+" WHERE username = ?")){
            getAccount.setString(1, playerName);
            
            try(ResultSet result = getAccount.executeQuery()){
                if(result.next()){
                    return new CoolPointsResponse(CoolPointsResponseType.SUCCESS,new CoolPointsAccount(
                        result.getInt("id"),
                        UUID.fromString(result.getString("uuid")),
                        result.getString("username"),
                        result.getInt("balance"),
                        (Date) result.getTimestamp("first_joined"),
                        (Date) result.getTimestamp("last_seen"),
                        (Date) result.getTimestamp("last_gifted")
                    ));
                }
                else if(createIfNotExists){
                    Player player = Bukkit.getPlayer(playerName);
                    
                    if(player != null){
                        return this.createAccountFor(player.getUniqueId(),player.getName());
                    }
                    
                    return new CoolPointsResponse(CoolPointsResponseType.FAILURE_NO_ACCOUNT,"Unable to create a CP account for an offline player!");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PointsManager.class.getName()).log(Level.SEVERE, null, ex);
            
            return new CoolPointsResponse(CoolPointsResponseType.FAILURE_DATABASE,"A database error occurred!");
        }
        
        return new CoolPointsResponse(CoolPointsResponseType.FAILURE_NO_ACCOUNT,"No account exists for that UUID!");
    }
    
    // presumes there's already been a check to see if the account exists
    private CoolPointsResponse createAccountFor(UUID uuid, String username) {
        username = username.toLowerCase();
        
        try(PreparedStatement createAccount = this.con.prepareStatement("INSERT INTO "+this.TBL_ACCOUNTS+"(uuid,username,first_joined,last_seen,last_gifted,balance) VALUES(?,?,?,?,?,?)")){
            Timestamp now = new java.sql.Timestamp(new java.util.Date().getTime());
            
            createAccount.setString(1, uuid.toString());
            createAccount.setString(2, username);
            createAccount.setTimestamp(3, now);
            createAccount.setTimestamp(4, now);
            createAccount.setTimestamp(5, now);
            createAccount.setInt(6, 1);// accounts are created with 1CP
            
            createAccount.executeUpdate();
            
            return this.getPlayerAccount(username, false);
        } 
        catch (SQLException ex) {
            Logger.getLogger(PointsManager.class.getName()).log(Level.SEVERE, null, ex);
            
            return new CoolPointsResponse(CoolPointsResponseType.FAILURE_DATABASE,"A database error occurred!");
        }
    }

    public CoolPointsResponse giftPlayer(UUID gifter, String giftTo) {
        CoolPointsResponse gifterResponse = this.getPlayerAccount(gifter, false);
        
        if(gifterResponse.wasSuccessful()){
            if(!gifterResponse.getAccount().hasGiftedToday()){
                try(PreparedStatement updateLastGifted = this.con.prepareStatement("UPDATE "+this.TBL_ACCOUNTS+" SET last_gifted = ? WHERE uuid = ? LIMIT 1")){
                    updateLastGifted.setTimestamp(1, new java.sql.Timestamp(new java.util.Date().getTime()));
                    updateLastGifted.setString(2, gifter.toString());
                    
                    int rows = updateLastGifted.executeUpdate();
                    
                    if(rows > 0){ 
                        return this.givePlayer(giftTo, 1);
                    }
                    else {
                        return new CoolPointsResponse(CoolPointsResponseType.FAILURE,"Unable to verify if you have gifted today!");
                    }
                } 
                catch (SQLException ex) {
                    Logger.getLogger(PointsManager.class.getName()).log(Level.SEVERE, null, ex);
                    
                    return new CoolPointsResponse(CoolPointsResponseType.FAILURE_DATABASE,"Unable to update the database with your gift!");
                } 
            }
            else{
                return new CoolPointsResponse(CoolPointsResponseType.FAILURE_ALREADY_GIFTED,"You already gifted someone today!");
            }
        }
        
        return gifterResponse;
    }

    public CoolPointsResponse givePlayer(String giveToName, int amount) {
        CoolPointsResponse cpr = this.getPlayerAccount(giveToName, true);
        
        if(!cpr.wasSuccessful()){
            return cpr;
        }
        
        try(PreparedStatement givePointsToPlayer = this.con.prepareStatement("UPDATE "+this.TBL_ACCOUNTS+" SET balance = balance + ? WHERE id = ?")){
            givePointsToPlayer.setInt(1, amount);
            givePointsToPlayer.setInt(2, cpr.getAccount().getdbID());
            
            givePointsToPlayer.executeUpdate();
            
            CoolPointsAccount cpa = cpr.getAccount();
            
            return new CoolPointsResponse(CoolPointsResponseType.SUCCESS,new CoolPointsAccount(
                cpa.getdbID(),
                cpa.getUUID(),
                cpa.getUsername(),
                cpa.getBalance()+amount,
                cpa.getFirstJoined(),
                cpa.getLastSeen(),
                cpa.getLastGifted()
            ));
        } 
        catch (SQLException ex) {
            Logger.getLogger(PointsManager.class.getName()).log(Level.SEVERE, null, ex);
            
            return new CoolPointsResponse(CoolPointsResponseType.FAILURE_DATABASE,"A database error occurred!");
        }
    }

    public int getPlayerRank(String playerName) {
        CoolPointsResponse response = this.getPlayerAccount(playerName, false);
        
        if(!response.wasSuccessful()) {
            return -1;
        }

        try(PreparedStatement statement = this.con.prepareStatement("SELECT COUNT(*) as rank FROM "+this.TBL_ACCOUNTS+" WHERE balance > ?"))
        {
            statement.setInt(1, response.getAccount().getBalance());
            
            try(ResultSet result = statement.executeQuery())
            {
                if(result.next())
                {
                    int iRank = result.getInt("rank")+1;

                    return iRank;
                }
            }
        }
        catch (SQLException ex)
        {
            this.logger.log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    public Map<String, Integer> getTopPlayers(int topAmount) {
        try(PreparedStatement statement = this.con.prepareStatement("SELECT username,balance FROM "+this.TBL_ACCOUNTS+" ORDER BY balance DESC LIMIT ?"))
        {
            statement.setInt(1, topAmount);
            
            try(ResultSet result = statement.executeQuery()){
                LinkedHashMap <String,Integer> topPlayers = new LinkedHashMap <>();

                while(result.next())
                {
                    topPlayers.put(result.getString("username"),result.getInt("balance"));
                }

                return topPlayers;
            }
        }
        catch (Exception ex)
        {
            this.logger.log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public boolean giveDailyWage(UUID uniqueId) {
        CoolPointsResponse cpr = this.getPlayerAccount(uniqueId, true);
        
        if(!cpr.wasSuccessful() || cpr.getAccount().receivedDailyWageInLast24Hours()){
            return false;
        }
        
        try(PreparedStatement giveDailyWage = this.con.prepareStatement("UPDATE "+this.TBL_ACCOUNTS+" SET balance = balance + 1, last_seen = NOW() WHERE id = ?")){
            giveDailyWage.setInt(1, cpr.getAccount().getdbID());
            
            giveDailyWage.executeUpdate();

            return true;
        } 
        catch (SQLException ex) {
            Logger.getLogger(PointsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        return false;
    }

    public void migrateAccount(UUID uniqueId, String username, int balance, Date firstJoined) {
        try(PreparedStatement migrateAccount = this.con.prepareStatement("INSERT INTO "+this.TBL_ACCOUNTS+"(uuid,username,first_joined,last_seen,last_gifted,balance) VALUES(?,?,?,?,?,?)")){
            migrateAccount.setString(1, uniqueId.toString());
            migrateAccount.setString(2, username);
            migrateAccount.setTimestamp(3, new Timestamp(firstJoined.getTime()));
            migrateAccount.setTimestamp(4, new Timestamp(firstJoined.getTime()));
            migrateAccount.setTimestamp(5, new Timestamp(firstJoined.getTime()));
            migrateAccount.setInt(6, balance);
            
            migrateAccount.executeUpdate();
        } 
        catch (SQLException ex) {
            Logger.getLogger(PointsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
