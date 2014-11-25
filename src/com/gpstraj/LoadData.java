import com.gpstraj.sql.InsertSQL;
import com.gpstraj.sql.JDBCutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.mongodb.*;

/**
 * Created by jhh11 on 11/23/14.
 */

public class LoadData {

    public static void main(String[] args) {
        // TODO: modify to use path variable
        String path = args[0];

        path = "/Users/jhh11/Downloads/Geolife Trajectories 1.3/";
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

        // TODO: Initialize Redis

        try {
            // Start SQL connection
            con = JDBCutils.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE " + db);

            // TODO: Start Mongo Connection
            mongoClient = new MongoClient( "localhost" );
            mongodb = mongoClient.getDB( db );
            DBCollection TColl = mongodb.getCollection("Traj");


            // TODO: Start Redis Connection

            for (int i = 0; i < folders; i++) {
                String setName = files[i];
                File inner = new File(path + "/Data/" + setName + "/Trajectory");
                File[] inners = inner.listFiles();
                for (int j = 0; j < inners.length; j++) {
                    String trajName = inners[j].getName();
                    String trajN = trajName.substring(0, trajName.indexOf("."));
                    BufferedReader reader = null;

                    // Insert SQL trajectories
                    int trajID = InsertSQL.insertTraj(con, setName, trajN);

                    // TODO: Insert Mongo Trajectories

                    // TODO: Insert Redis Trajectories

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
                                InsertSQL.insertGPS(con, trajID, line);

                                // TODO: Insert Mongo GPS

                                // TODO: Insert Redis GPS
                            }
                            count++;
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    } finally {
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
            System.out.println("DONE");
        }
    }
}
