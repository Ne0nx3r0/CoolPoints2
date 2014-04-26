package com.gmail.ne0nx3r0.coolpoints.points;

public class CoolPointsResponse {
    private final String errorMessage;
    private final CoolPointsResponseType responseType;
    private CoolPointsAccount account;

    CoolPointsResponse(CoolPointsResponseType responseType, String errorMessage) {
        this.responseType = responseType;
        this.errorMessage = errorMessage;
    }
    
    CoolPointsResponse(CoolPointsResponseType responseType, CoolPointsAccount account) {
        this.responseType = responseType;
        this.account = account;
        this.errorMessage = "";
    }

    public boolean wasSuccessful() {
        return this.responseType == CoolPointsResponseType.SUCCESS;
    }

    public CoolPointsAccount getAccount() {
        return this.account;
    }

    public String getMessage() {
        return this.errorMessage;
    }
}
