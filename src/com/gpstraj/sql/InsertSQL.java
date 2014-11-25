package com.gpstraj.sql;

import java.sql.*;

/**
 * Created by jhh11 on 11/24/14.
 */
public class InsertSQL {
    public static int insertTraj(Connection con, String setName, String trajName) throws Exception {
        ResultSet rs = null;
        String table = "Traj";
        int output = -1;

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO "+ table + " (`set`, `name`) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS );
        ps.setString(1, setName);
        ps.setString(2, trajName);
        ps.executeUpdate();
        rs = ps.getGeneratedKeys();
        rs.next(); // Assume just one auto-generated key; otherwise, use a while loop here
        output = rs.getInt(1); // there should only be 1 column in your results: the value of the

        if (rs != null) {
            try { rs.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }

        return output;
    }

    public static void insertGPS(Connection con, int trajID, String gps) throws Exception {
        String table = "GPS";
        String[] gpsArr = gps.split(",");

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO "+ table + " (`traj`, `lat`, `long`, `zero`, `altitude`, `dateNum`, `date`, `time`) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        ps.setInt(1, trajID);
        ps.setFloat(2, Float.parseFloat(gpsArr[0]));
        ps.setFloat(3, Float.parseFloat(gpsArr[1]));
        ps.setInt(4, Integer.parseInt(gpsArr[2]));
        ps.setInt(5, Integer.parseInt(gpsArr[3]));
        ps.setFloat(6, Float.parseFloat(gpsArr[4]));
        ps.setDate(7, java.sql.Date.valueOf(gpsArr[5]));
        ps.setTime(8, java.sql.Time.valueOf(gpsArr[6]));
        ps.executeUpdate();

        if (ps != null) {
            try { ps.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }
}
