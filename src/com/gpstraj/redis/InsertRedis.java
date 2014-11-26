package com.gpstraj.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * Created by jhh11 on 11/25/14.
 */
public class InsertRedis {
    public static Pipeline insert(Pipeline p, Jedis jedis, String trajN, String gps) throws Exception {
        Float score = Float.parseFloat(gps.split(",")[4]);
        p.zadd(trajN, score, gps);
        return p;
    }
}
