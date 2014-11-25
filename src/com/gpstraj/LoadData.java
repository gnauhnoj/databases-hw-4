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
        String path = args[0];
//        String type = args[1];

        path = "/Users/jhh11/Downloads/Geolife Trajectories 1.3/";
        File file = new File(path + "/Data");
        // Reading directory contents
        String[] files = file.list();
        int folders = 2;

        // sql stuff
        Connection con = null;
        Statement stmt = null;
        String db = "GPSTraj";


        MongoClient mongoClient = new MongoClient( "localhost" );
        DB mongodb = mongoClient.getDB( "CS5320" );

        try {
            con = JDBCutils.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE " + db);


            for (int i = 0; i < folders; i++) {
                String setName = files[i];
                File inner = new File(path + "/Data/" + setName + "/Trajectory");
                File[] inners = inner.listFiles();
                for (int j = 0; j < inners.length; j++) {
                    String trajName = inners[j].getName();
                    String trajN = trajName.substring(0, trajName.indexOf("."));
                    BufferedReader reader = null;

                    // sql stuff
                    int trajID = InsertSQL.insertTraj(con, setName, trajN);
                    //

                    try {
                        reader = new BufferedReader(new FileReader(path + "/Data/"
                                + setName + "/Trajectory/" + trajName));
                        String line = null;
                        int count = 0;
                        while(true) {
                            line = reader.readLine();
                            if(line == null) break;
                            if (count > 5) {

                                // sql stuff
                                InsertSQL.insertGPS(con, trajID, line);
                                //

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
