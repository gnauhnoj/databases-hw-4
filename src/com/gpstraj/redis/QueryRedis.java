package com.gpstraj.redis;

import com.gpstraj.helpers;
import redis.clients.jedis.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import static java.lang.System.err;


/**
 * Created by jhh11 on 11/25/14.
 */
public class QueryRedis {
    public static void countGPS (Jedis jedis, String trajN) throws Exception {
        Date date1 = new Date();
        System.out.println("Start Milliseconds: " + date1.getTime());
        System.out.println(jedis.zcard(trajN));
        Date date2 = new Date();
        System.out.println("End Milliseconds: " + date2.getTime());
    }

    public static void filterDate (Jedis jedis) throws Exception {
        Date date1 = new Date();
        System.out.println("Start Milliseconds: " + date1.getTime());
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
        Date date2 = new Date();
        System.out.println("End Milliseconds: " + date2.getTime());
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
                countGPS(jedis, input);
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
