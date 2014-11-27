Team Members:
Jonathan Huang (jhh283)
Alap Parikh (akp76)

Project Dependencies (these drivers should be downloaded and added to your project if you run into issues running the
 code):
- mySQL JDBC
- MongoDB Java Driver (2.13.0 - http://central.maven.org/maven2/org/mongodb/mongo-java-driver/2.13.0-rc0/)
- jedis Java Redis Driver (2.6.1 - http://search.maven.org/remotecontent?filepath=redis/clients/jedis/2.6.1/jedis-2.6.1.jar)
- Neo4j java files from the /lib folder of the distribution (http://neo4j.com/download/)

Testing Instructions:
Problem 1-

Problem 2-
0. Navigate to the built project "<submission directory>/out/production/CS5320-HW4"
1. Run the CreateDB method which drops currently existing SQL, Redis, Mongo and initializes the required SQL schema
    - java com.gpstraj.CreateDB
2. Run the LoadData method which parses the provided dataset inserting data into the respective databases
    - java com.gpstraj.LoadData <path to the "Geolife Trajectories 1.3" folder>
3. Run SQL Tests (manual process done through the mysql command prompt) [queries found in com.gpstraj.sql.sql_queries]
    - Query 1: replace the value found for variable "@trajectoryName" with the desired Trajectory string (example: "20081023025304". Execute the query to retrieve the GPS point count for the trajectory
    - Query 2:
        - IMPORTANT: In order for Query 2 to work across platforms (redis, mongo, sql) the query marked 2a must be first ran. This query only needs to be ran once for all platforms
        - Run Query 2a (only need to run once)
        - Run Query 2b to retrieve gps point counts for 10 random days. In this case, the days appear in the format of being the number of days since 12/30/1899
4. Run Mongo Tests
    - Query 1: java com.gpstraj.mongo.QueryMongo CountGPS <trajectory name>
    - Query 2: [ASSUMES SQL QUERY 2a HAS BEEN RUN] java com.gpstraj.Mongo.QueryMongo CountDates
4. Run Redis Tests
    - Query 1: java com.gpstraj.redis.QueryRedis CountGPS <trajectory name>
    - Query 2: [ASSUMES SQL QUERY 2a HAS BEEN RUN] java com.gpstraj.redis.QueryRedis CountDates

Structure of Data Storage:
Problem 1-

Problem 2-
SQL:
- 2 Tables: Traj, GPS
- Traj Table contains: id (SQL generated), set (folder name where trajectory is found), name (name of the trajectory
filename)
- GPS Table contains: id, traj (foreign key to id in Traj table), lat, long, zero, altitude, dateNum (days from
12/30/1899) , date, time

Redis:
- Key: Trajectory Name
- Value: Sorted Set containing strings which are taken from each "line" of the parsed data files (each line is a GPS
point)
    - the set is scored by the "dateNum" field which is the days since 12/30/1899

Mongo:

Time Comparisons:
Problem 1-

Problem 2-
Query 1:
SQL-
Max: 0.002 s
Min: 0.001 s
Average: 0.001375 s
Mongo-
Max: 146528287 ns
Min: 61470242 ns
Average: 93071230 ns
Redis-
Max: 42994727 ns
Min: 21826689 ns
Average: 26,438,519.2 ns

Query 2:
SQL-
Max: 0.55 s
Min: 0.525 s
Average: 0.5322 s
Redis-
Max: 825442736 ns
Min: 690397627 ns
Average: 768,944,737.8 ns

Mongo-
Max: 12039619633 ns
Min: 11182223975 ns
Average: 11584815850 ns
