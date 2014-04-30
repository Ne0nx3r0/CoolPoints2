package com.gmail.ne0nx3r0.coolpoints.api;

import com.gmail.ne0nx3r0.coolpoints.points.CoolPointsResponse;
import com.gmail.ne0nx3r0.coolpoints.points.PointsManager;

public class CoolPointsAPI {
    private PointsManager pm;

    public CoolPointsAPI(PointsManager pm) {
        this.pm = pm;
    }
    
    public int getCoolPoints(String playerName){
        CoolPointsResponse cpr = pm.getPlayerAccount(playerName, false);
        
        if(cpr.wasSuccessful()){
            return cpr.getAccount().getBalance();
        }
        
        return -1;
    }
}
