package com.gpstraj.mongo;

import com.mongodb.*;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by jhh11 on 11/25/14.
 */
public class InsertMongo {
    public static void insertEntry(MongoClient mongoClient, String setName, String trajName, ArrayList<BasicDBObject> GPSList) throws Exception {

        BasicDBObject entry = new BasicDBObject("name",trajName)
                .append("set",setName)
                .append("measures",GPSList);
        DB db = mongoClient.getDB("db");
        DBCollection coll = db.getCollection("Traj");
        coll.insert(entry);
    }
}
