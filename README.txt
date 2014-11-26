Required Drivers:
- mySQL JDBC
- MongoDB Java Driver (2.13.0 - http://central.maven.org/maven2/org/mongodb/mongo-java-driver/2.13.0-rc0/)
- jedis Java Redis Driver (2.6.1 - http://search.maven.org/remotecontent?filepath=redis/clients/jedis/2.6.1/jedis-2.6
.1.jar)


Redis info:
- flushdb - clears db
- flushall - clears everything

Direction to call redis commands:
java com.gpstraj.redis.QueryRedis CountGPS <trajectory name>
java com.gpstraj.redis.QueryRedis CountDates (assumes that sql query selecting 10 random dates has been ran)
