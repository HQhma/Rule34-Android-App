package com.HQHMA.rule34.Models;

public class AppVersion {

    int version;
    String updateLink, updateText;
    boolean forceUpdate, appActive;

    public AppVersion() {
    }

    public AppVersion(int version, String updateLink, String updateText, boolean forceUpdate, boolean appActive) {
        this.version = version;
        this.updateLink = updateLink;
        this.updateText = updateText;
        this.forceUpdate = forceUpdate;
        this.appActive = appActive;

    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUpdateLink() {
        return updateLink;
    }

    public void setUpdateLink(String updateLink) {
        this.updateLink = updateLink;
    }

    public String getUpdateText() {
        return updateText;
    }

    public void setUpdateText(String updateText) {
        this.updateText = updateText;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean isAppActive() {
        return appActive;
    }

    public void setAppActive(boolean appActive) {
        this.appActive = appActive;
    }
}
