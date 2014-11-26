package com.gpstraj;

import com.gpstraj.sql.JDBCutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by jhh11 on 11/25/14.
 */
public class helpers {
    public static ArrayList<Integer> getDates() {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<Integer> list = new ArrayList<Integer>();

        try {
            con = JDBCutils.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE GPSTraj");

            rs = stmt.executeQuery("select * from random");
            while (rs.next()) {
                list.add(rs.getInt("trun"));
            }
        }
        catch (SQLException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
        finally {
            JDBCutils.closeConnection(rs, stmt, con);
        }
        return list;
    }
}
