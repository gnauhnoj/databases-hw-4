package com.gpstraj;

import com.gpstraj.helpers;
import com.gpstraj.mongo.InsertMongo;
import com.gpstraj.redis.InsertRedis;
import com.gpstraj.sql.InsertSQL;
import com.gpstraj.sql.JDBCutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mongodb.*;
import com.mongodb.MongoClient;
import redis.clients.jedis.*;


/**
 * Created by jhh11 on 11/23/14.
 */

public class LoadData {

    public static void main(String[] args) {
        // TODO: modify to use provided path variable
//        String path = args[0];

        //String path = "/Users/jhh11/Downloads/Geolife Trajectories 1.3/";
        String path = "/Users/Alap/Documents/Cornell Tech/Database Systems/Assignment4/data/Geolife Trajectories 1.3/";
        File file = new File(path + "/Data");
        // Reading directory contents
        String[] files = file.list();

        // number of files to use
        int folders = 2;

        // INITIALIZE SQL
        Connection con = null;
        Statement stmt = null;
        String db = "GPSTraj";

        // TODO: Initialize Mongo
        MongoClient mongoClient = null;
        DB mongodb = null;

        Jedis jedis = null;

        try {
            // Start SQL connection
            con = JDBCutils.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE " + db);

            // Start Mongo Connection
            mongoClient = new MongoClient( "localhost", 27017 );
            mongodb = mongoClient.getDB("db");
            DBCollection coll = mongodb.getCollection("Traj");
            //DBCollection collG = mongodb.getCollection("GPS");

            // Start Redis Connection - assumes an empty db - need to run flushdb on redis terminal (see CreateDB)
//            jedis = new Jedis("localhost", 6379);

            for (int i = 0; i < folders; i++) {
                String setName = files[i];
                File inner = new File(path + "/Data/" + setName + "/Trajectory");
                File[] inners = inner.listFiles();

                for (int j = 0; j < inners.length; j++) {
                    String trajName = inners[j].getName();
                    String trajN = trajName.substring(0, trajName.indexOf("."));
                    BufferedReader reader = null;
                    ArrayList<BasicDBObject> GPSList = new ArrayList<BasicDBObject>();

                    // Insert SQL trajectories
//                    int trajID = InsertSQL.insertTraj(con, setName, trajN);

                    // Declare redis pipeline of statements for a trajectory
//                    Pipeline p = jedis.pipelined();

                    try {
                        reader = new BufferedReader(new FileReader(path + "/Data/"
                                + setName + "/Trajectory/" + trajName));
                        String line = null;
                        int count = 0;
                        while(true) {
                            line = reader.readLine();
                            if(line == null) break;
                            if (count > 5) {
                                // Insert SQL GPS points
//                                InsertSQL.insertGPS(con, trajID, line);

                                // Add new GPS point to GPS list
                                GPSList.add(helpers.getGPSPoint(line));

                                // Insert Redis GPS points
//                                InsertRedis.insert(p, trajN, line);
                            }
                            count++;
                        }

                        // run redis pipeline sync

                        //Insert entry in MongoDB
                        InsertMongo.insertEntry(mongoClient,setName,trajN,GPSList);

                    } catch(Exception e) {
                        e.printStackTrace();
                    } finally {
//                        p.sync();
                        if(reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    trajName = null;
                    trajN = null;
                }
                setName = null;
                inner = null;
                inners = null;
                System.gc();
            }
        }
        catch (SQLException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
        finally {
            JDBCutils.closeConnection(null, stmt, con);
            //jedis.quit();
            mongoClient.close();
            System.out.println("DONE");
        }
    }
}
