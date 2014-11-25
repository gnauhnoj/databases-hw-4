package com.gpstraj.sql;

/**
 * Created by jhh11 on 11/23/14.
 */

import java.sql.*;

public class CreateDB {

    private static Statement createSQLDatabase(Statement stmt, String db) throws Exception {
        try {
            stmt.executeUpdate("DROP DATABASE " + db);
        } catch(Exception e){
        }
        stmt.executeUpdate("CREATE DATABASE " + db);
        return stmt;
    }

    private static Statement createTable(Statement stmt, String table, String schemaQuery) throws Exception {
        stmt.executeUpdate(schemaQuery);
        return stmt;
    }

    public static void main (String[] args) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        String db = "GPSTraj";
        String table1 = "Traj";
        String schemaQuery1 = "CREATE TABLE " + table1 +
                " (`id` int NOT NULL AUTO_INCREMENT, " +
                " `set` varchar(3) NOT NULL, `name` varchar(14) NOT NULL, primary key (`id`))";

        String table2 = "GPS";
        String schemaQuery2 = "CREATE TABLE " + table2 +
                " (`id` int NOT NULL AUTO_INCREMENT, " +
                "`traj` int NOT NULL, `lat` float NOT NULL, `long` float NOT NULL, `zero` int NOT NULL, " +
                "`altitude` int NOT NULL, `dateNum` float NOT NULL, `date` date NOT NULL, `time` time NOT NULL, " +
                "primary key (`id`), foreign key (`traj`) references Traj(`id`))";

        try {
            con = JDBCutils.getConnection();
            stmt = con.createStatement();
            stmt = createSQLDatabase(stmt, db);

            stmt.executeUpdate("USE " + db);
            stmt = createTable(stmt, table1, schemaQuery1);
            stmt = createTable(stmt, table2, schemaQuery2);
        }
        catch (SQLException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
        finally {
            JDBCutils.closeConnection(rs, stmt, con);
        }

        System.out.println("DONE");
    }
}
