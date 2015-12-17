package com.shifz.rankix.database.tables;

/**
 * Created by shifar on 17/12/15.
 */
public class SharedData {

    private static SharedData ourInstance = new SharedData();
    public static SharedData getInstance() {
        return ourInstance;
    }
    private SharedData() {
    }


    public boolean add(String shareDataKey, String shareData) {
        final String query = "INSERT INTO shared_data ("
        return false;
    }
}
