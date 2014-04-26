package com.gmail.ne0nx3r0.coolpoints.points;

import com.ne0nx3r0.util.DateTimeUtil;
import java.util.Date;
import java.util.UUID;

public class CoolPointsAccount {
    private final int dbID;
    private final UUID uuid;
    private final String username;
    private final int balance;
    private final Date first_joined;
    private final Date last_seen;
    private final Date last_gifted;

    CoolPointsAccount(int dbID,UUID uuid, String username, int balance, Date first_joined, Date last_seen, Date last_gifted) {
        this.dbID = dbID;
        this.uuid = uuid;
        this.username = username;
        this.balance = balance;
        this.first_joined = first_joined;
        this.last_seen = last_seen;
        this.last_gifted = last_gifted;
    }

    public int getBalance() {
        return this.balance;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean hasGiftedToday() {
        return DateTimeUtil.inLast24Hours(this.last_gifted);
    }
    
    public Date getFirstJoined(){
        return this.first_joined;
    }
    
    public Date getLastGifted(){
        return this.last_gifted;
    }
    
    public Date getLastSeen(){
        return this.last_seen;
    }

    public int getdbID() {
        return this.dbID;
    }

    public boolean receivedDailyWageInLast24Hours() {
        return DateTimeUtil.inLast24Hours(this.last_seen);
    }
    
    public UUID getUUID(){
        return this.uuid;
    }
}
