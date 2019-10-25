package com.ehotels.cache;

import java.sql.Timestamp;

import com.ehotels.enums.Permission;

public class SessionInfo {
    
    private Timestamp exp;
    private int loginCredID;
    private Permission permission;
    private int clientOrEmployeeID;

    
    public SessionInfo(Timestamp exp, int loginCredID, Permission permission, int clientOrEmployeeID) {
        super();
        this.exp = exp;
        this.loginCredID = loginCredID;
        this.permission = permission;
        this.clientOrEmployeeID = clientOrEmployeeID;
    }
    public Timestamp getExp() {
        return exp;
    }
    public int getLoginCredID() {
        return loginCredID;
    }
    public Permission getPermission() {
        return permission;
    }
    public int getClientOrEmployeeID() {
        return clientOrEmployeeID;
    }

}