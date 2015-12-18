package com.shifz.rankix.database.tables;

import com.shifz.rankix.database.Connection;
import com.shifz.rankix.models.ShareData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by shifar on 17/12/15.
 */
public class SharedData {

    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_DATA_KEY = "data_key";
    private static SharedData ourInstance = new SharedData();

    public static SharedData getInstance() {
        return ourInstance;
    }

    private SharedData() {
    }


    public boolean add(String shareDataKey, String shareData) {
        final String query = "INSERT INTO shared_data (data_key,data) VALUES (?,?);";
        try {
            final PreparedStatement ps = Connection.getConnection().prepareStatement(query);
            ps.setString(1, shareDataKey);
            ps.setString(2, shareData);
            final boolean isAdded = ps.executeUpdate() == 1;
            ps.close();
            return isAdded;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String get(final String returnColumn, final String whereColumn, final String whereColumnValue) {
        final String query = String.format("SELECT %s FROM shared_data WHERE %s = ? LIMIT 1", returnColumn, whereColumn);
        try {
            final PreparedStatement ps = Connection.getConnection().prepareStatement(query);
            ps.setString(1, whereColumnValue);
            final ResultSet rs = ps.executeQuery();
            String data = null;
            if (rs.first()) {
                data = rs.getString(returnColumn);
            }
            rs.close();
            ps.close();
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
