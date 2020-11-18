package com.example.yzn.activity.util;

public class ApkVersion {
    private boolean latest;
    private String latestVersion;

    public ApkVersion(boolean latest,String latestVersion){
        this.latest=latest;
        this.latestVersion=latestVersion;
    }


    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }
}
