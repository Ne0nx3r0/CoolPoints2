package com.ne0nx3r0.coolpoints.migration;

import java.util.Date;

public class AccountToMigrate {
    public final String username;
    public final int balance;
    public final Date firstJoined;

    AccountToMigrate(String username, int balance, Date firstJoined) {
        this.username = username;
        this.balance = balance;
        this.firstJoined = firstJoined;
    }
    
}
