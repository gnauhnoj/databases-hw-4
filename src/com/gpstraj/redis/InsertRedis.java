package com.gpstraj.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * Created by jhh11 on 11/25/14.
 */
public class InsertRedis {
    public static void insert(Pipeline p, String trajN, String gps) throws Exception {
        String[] gpsArr = gps.split(",");
        Double score = Double.parseDouble(gpsArr[4]);
        p.zadd(trajN, score, gps);
//        return p;
    }
}
