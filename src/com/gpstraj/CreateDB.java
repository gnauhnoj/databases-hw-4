package com.gpstraj;

/**
 * Created by jhh11 on 11/23/14.
 */

import com.gpstraj.sql.JDBCutils;
import com.mongodb.MongoClient;
import redis.clients.jedis.Jedis;

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

    // Drops existing SQL, Mongo, and Redis databases
    // Creates the SQL Schema
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
                "`traj` int NOT NULL, `lat` double NOT NULL, `long` double NOT NULL, `zero` int NOT NULL, " +
                "`altitude` int NOT NULL, `dateNum` double NOT NULL, `date` date NOT NULL, `time` time NOT NULL, " +
                "primary key (`id`), foreign key (`traj`) references Traj(`id`))";

        Jedis jedis = null;
        MongoClient mongoClient = null;

        try {
            con = JDBCutils.getConnection();
            stmt = con.createStatement();
            stmt = createSQLDatabase(stmt, db);

            stmt.executeUpdate("USE " + db);
            stmt = createTable(stmt, table1, schemaQuery1);
            stmt = createTable(stmt, table2, schemaQuery2);

            // drop current jedis database
            jedis = new Jedis("localhost", 6379);
            jedis.flushDB();

            // Drop current mongo database
            mongoClient = new MongoClient("localhost",27017);
            mongoClient.dropDatabase("db");
        }
        catch (Exception e) {e.printStackTrace();}
        finally {
            JDBCutils.closeConnection(rs, stmt, con);
            jedis.quit();
            mongoClient.close();
        }

        System.out.println("DONE");
    }
}
