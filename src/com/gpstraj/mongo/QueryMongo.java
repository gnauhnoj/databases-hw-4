package com.gpstraj.mongo;

import com.gpstraj.helpers;
import com.mongodb.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import static java.lang.System.err;

/**
 * Created by Alap on 11/26/14.
 */
public class QueryMongo {

    public static void main (String[] args) {
        if (args.length < 1) {
            err.format("Usage: java QueryMongo <classname> <input>%n");
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

            if (func.equals("CountGPS")) {
                getNumberOfTrajPoints(coll, input);
            } else if (func.equals("CountDates")) {
                getNumberOfDatePoints(coll);
            } else {
                err.format("Usage: QueryMongo <classname> not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("DONE");
        }
    }

    public static void getNumberOfTrajPoints (DBCollection coll, String traj){
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
            System.out.println(number);
            cursor.close();
        }
        long elapsedTime = System.nanoTime() - start;
        System.out.println("Elapsed time to get number of gps points in trajectory " + traj + " is " + elapsedTime);
    }

    public static void getNumberOfDatePoints (DBCollection coll) {
        long start = System.nanoTime();
        ArrayList<Integer> dates = helpers.getDates();

        for (Integer date : dates) {
            int datePoints = 0;
            DBCursor cursor = coll.find();
            try {
                while(cursor.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cursor.next();
                    BasicDBList list = (BasicDBList) obj.get("measures");
                    for (Object point : list) {
                        BasicDBObject gps = (BasicDBObject) point;
                        if (Math.floor((Double)gps.get("dateNum")) == date) {
                            datePoints++;
                        }
                    }
                }
            } finally {
                System.out.println("Date: " + date + ", Count: " + datePoints);
                cursor.close();
            }
        }
        long elapsedTime = System.nanoTime() - start;
        System.out.println("Time to get number of gps points for 10 dateNum is " + elapsedTime);
    }
}
