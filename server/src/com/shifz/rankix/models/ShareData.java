package com.shifz.rankix.models;

/**
 * Created by shifar on 18/12/15.
 */
public class ShareData {
    private final String key,data;

    public ShareData(String key, String data) {
        this.key = key;
        this.data = data;
    }

    public String getKey() {
        return this.key;
    }
}
