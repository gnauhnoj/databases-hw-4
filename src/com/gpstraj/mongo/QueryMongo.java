package com.gpstraj.mongo;

import com.mongodb.*;

import java.util.ArrayList;

import static java.lang.System.err;

/**
 * Created by Alap on 11/26/14.
 */
public class QueryMongo {

    public static void main (String[] args){
        try{
            String trajname = "20081027115449";
            MongoClient mongoClient = new MongoClient("localhost");
            DB mongodb = mongoClient.getDB("db");
            DBCollection coll = mongodb.getCollection("Traj");
            int number = getNumberOfTrajPoints(coll, trajname);
            int number2 = getNumberOfDatePoints(coll, 39747);
            System.out.println("Number of GPS points: " + number + " Number of Date points: " + number2);
        } catch (Exception e){
            e.printStackTrace();
        }

        /*if (args.length < 1) {
            err.format("Usage: java QueryRedis <classname> <input>%n");
            return;
        }
        String func = args[0];
        String input = null;
        if (args.length > 1) {
            input = args[1];
        }
        try {
            MongoClient mongoClient = new MongoClient("localhost");
            DB mongodb = mongoClient.getDB("db");
            DBCollection coll = mongodb.getCollection("Traj");

            if (func.equals("getNumberOfTrajPoints")) {
                getNumberOfTrajPoints(coll, input);
            } else if (func.equals("getNumberOfDatePoints")) {
                getNumberOfDatePoints(coll,1);
            } else {
                err.format("Usage: QueryMongo <classname> not found");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("DONE");
        }*/
    }

    public static int getNumberOfTrajPoints (DBCollection coll, String traj){
        long start = System.nanoTime();
        int number = 0;
        BasicDBObject query = new BasicDBObject("name", traj);
        DBCursor cursor = coll.find(query);
        try {
            while(cursor.hasNext()) {
                BasicDBObject obj = (BasicDBObject) cursor.next();
                BasicDBList list = (BasicDBList) obj.get("measures");
                number = list.size();
            }
        } finally {
            cursor.close();
        }
        long elapsedTime = System.nanoTime() - start;
        System.out.println("Elapsed time to get number of gps points in trajectory " + traj + " is " + elapsedTime);
        return number;
    }

    public static int getNumberOfDatePoints (DBCollection coll, int dateNum) {
        long start = System.nanoTime();
        int datePoints = 0;
        DBCursor cursor = coll.find();
        try {
            while(cursor.hasNext()) {
                BasicDBObject obj = (BasicDBObject) cursor.next();
                BasicDBList list = (BasicDBList) obj.get("measures");
                for (Object point : list) {
                    BasicDBObject gps = (BasicDBObject) point;
                    if (Math.floor((Double)gps.get("dateNum")) == dateNum) {
                        datePoints ++;
                    }
                }

            }
        } finally {
            cursor.close();
        }
        long elapsedTime = System.nanoTime() - start;
        System.out.println("Time to get number of gps points for dateNum " + dateNum + " is " + elapsedTime);
        return datePoints;
    }
}
