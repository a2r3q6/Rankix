package com.shifz.rankix.database.tables;

import com.shifz.rankix.database.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by shifar on 24/12/15.
 */
public class History {

    private History() {
    }

    private static History instance;

    public static History getInstance() {
        if (instance == null) {
            instance = new History();
        }
        return instance;
    }

    public boolean add(final String data) {
        final String query = "INSERT INTO history (data) VALUES (?)";
        boolean isAdded = false;
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, data);
            isAdded = ps.executeUpdate() == 1;
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isAdded;

    }

    public boolean isExist(String treeString) {
        final String query = "SELECT id FROM history WHERE data = ? ORDER BY id DESC LIMIT 1";
        boolean isExist = false;
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, treeString);
            final ResultSet rs = ps.executeQuery();
            isExist = rs.first();
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isExist;
    }
}
