package com.HQHMA.rule34.Models;

import com.google.firebase.Timestamp;

public class MyUser {

    String userId;
    Timestamp joinTime,lastOnlineTime;
    int loginCount;
    int appVersion;
    int prime;

    public MyUser() {
    }

    public MyUser(String userId, Timestamp joinTime, Timestamp lastOnlineTime,int loginCount,int appVersion, int prime) {
        this.userId = userId;
        this.joinTime = joinTime;
        this.lastOnlineTime = lastOnlineTime;
        this.loginCount = loginCount;
        this.appVersion = appVersion;
        this.prime = prime;
    }

    public int getPrime() {
        return prime;
    }

    public void setPrime(int prime) {
        this.prime = prime;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Timestamp joinTime) {
        this.joinTime = joinTime;
    }

    public Timestamp getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(Timestamp lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loinCount) {
        this.loginCount = loinCount;
    }
}
