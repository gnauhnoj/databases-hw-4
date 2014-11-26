package com.gpstraj.redis;

import com.gpstraj.helpers;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import static java.lang.System.err;


/**
 * Created by jhh11 on 11/25/14.
 */
public class QueryRedis {
    public static long countGPS (Jedis jedis, String trajN) throws Exception {
        long count = jedis.zcard(trajN);
        return count;
    }

    public static void filterDate (Jedis jedis) throws Exception {
        ArrayList<Integer> dates = helpers.getDates();

        // this is super inefficient...
        for (Integer date : dates) {
            Set<String> names = jedis.keys("*");
            Iterator<String> it = names.iterator();
            int count = 0;
            while (it.hasNext()) {
                String s = it.next();
                Set<String> sose = jedis.zrangeByScore(s, date, date + 1);
                count += sose.size();
            }
            System.out.println(date + ", " + count);
        }
    }

    public static void main (String[] args) {
        if (args.length < 1) {
            err.format("Usage: java QueryRedis <classname> <input>%n");
            return;
        }
        String func = args[0];
        String input = null;
        if (args.length > 1) {
            input = args[1];
        }
        try {
            Jedis jedis = new Jedis("localhost", 6379);

            if (func.equals("CountGPS")) {
                long result = countGPS(jedis, input);
                System.out.println(result);
            } else if (func.equals("CountDates")) {
                filterDate(jedis);
            } else {
                err.format("Usage: NetworkAnalysis <classname> not found");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("DONE");
        }
    }
}
