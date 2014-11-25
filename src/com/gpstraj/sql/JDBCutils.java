package com.gpstraj.sql;

/**
 * Created by jhh11 on 11/23/14.
 */

import java.sql.*;

public class JDBCutils {
    public static Connection getConnection() throws Exception {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "mysql";
        String userName = "root";
        String password = "";

        Class.forName(driver);
        Connection con = DriverManager.getConnection(url + dbName, userName, password);
        return con;
    }

    public static void closeConnection(ResultSet rs, Statement stmt, Connection con) {
        if (rs != null) {
            try { rs.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
        if (stmt != null) {
            try { stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
        if (con != null) {
            try { con.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }
}
