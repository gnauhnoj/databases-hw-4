package com.gpstraj.neo4j;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alap on 11/26/14.
 */
public class Neo4j {
    // Name of file to be read in
    //private static final String textFile = "p2p-Gnutella04.txt";

    // Need to change this to a directory within the project
    private static final String DB_PATH = "/Users/Alap/Documents/Cornell Tech/Database Systems/Assignment4/data/neo4j";
    private static final String textFile = "/Users/Alap/Downloads/roadNet-CA.txt";

    Set<Integer> Nodes;
    GraphDatabaseService graphDB;

    private static enum RelTypes implements RelationshipType
    {
        CONNECTS
    }

    public static void main (String[] args) throws IOException {
        Neo4j neo = new Neo4j();
        neo.createDB();
        neo.readInFile(textFile);
        neo.getNeighbors();
        //neo.getReachabilityCount();
        neo.shutdown();
    }

    void createDB() {
        graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
        registerShutdownHook( graphDB );
        /*try ( Transaction tx = graphDB.beginTx() )
        {
            Schema schema = graphDB.schema();
            schema.indexFor(DynamicLabel.label("ID"))
                    .on( "id" )
                    .create();
            tx.success();
        }*/
    }

    void shutdown() {
        graphDB.shutdown();
    }

    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }

    public void readInFile (String file){
        Nodes = new HashSet<Integer>();
        int currSize = 0;

        try {

            FileInputStream fs= new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));

            for(int i = 0; i < 4; ++i) {
                br.readLine();
            }

            String line;
            String [] array = new String[2];
            int[] numarray = new int[2];
            Node firstNode;
            Node secondNode;
            Relationship relationship;
            IndexDefinition indexDefinition;
            Label label = DynamicLabel.label( "ID" );
            int count = 0;

            while ((line = br.readLine()) != null) {


                //line = br.readLine();
                array = line.split("\\s");
                numarray[0] = Integer.parseInt(array[0]);
                numarray[1] = Integer.parseInt(array[1]);

                // Add first node to HashSet and check whether it is a new node (size of HashSet)
                Nodes.add(numarray[0]);
                if (Nodes.size() > currSize){
                    try ( Transaction tx = graphDB.beginTx() ) {
                        firstNode = graphDB.createNode(label);
                        firstNode.setProperty("id",numarray[0]);
                        tx.success();
                    }
                    currSize += 1;
                } else {
                    try (Transaction tx = graphDB.beginTx()){
                        try ( ResourceIterator<Node> node =
                                      graphDB.findNodesByLabelAndProperty( label, "id", numarray[0] ).iterator() )
                        {
                            firstNode = node.next();
                        }
                        tx.success();
                    }
                }

                // Add second node to HashSet and check whether it is a new node (size of HashSet)
                Nodes.add(numarray[1]);
                if (Nodes.size() > currSize){
                    try ( Transaction tx = graphDB.beginTx() ) {
                        secondNode = graphDB.createNode(label);
                        secondNode.setProperty("id",numarray[1]);
                        tx.success();
                    }
                    currSize += 1;
                } else {
                    try (Transaction tx = graphDB.beginTx()){
                        try ( ResourceIterator<Node> node =
                                      graphDB.findNodesByLabelAndProperty( label, "id", numarray[1] ).iterator() )
                        {
                            secondNode = node.next();
                        }
                        tx.success();
                    }
                }
                try (Transaction tx = graphDB.beginTx()){
                    firstNode.createRelationshipTo(secondNode, RelTypes.CONNECTS);
                    tx.success();
                }
                count ++;
            }
            System.out.println(Nodes.size());
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getNeighbors () {
        long start = System.nanoTime();
        int neighbors = 0;
        Node node;
        //String nodeResult = "";

        try (Transaction tx = graphDB.beginTx()) {
            node = graphDB.getNodeById(0);
            System.out.println(node.getProperty("id"));
            tx.success();
        }

        ExecutionEngine engine = new ExecutionEngine(graphDB);

        ExecutionResult result;
        try ( Transaction ignored = graphDB.beginTx() ) {
            result = engine.execute("START n=node(0) MATCH n-[:CONNECTS]->m RETURN distinct m.id");
            //result = engine.execute("MATCH (n) RETURN n");
            //result = engine.execute("START n=node(1) MATCH n<-[r]-() return r");
            System.out.println(result.dumpToString());
            ignored.success();
        }

        /*try (Transaction tx = graphDB.beginTx()){
            TraversalDescription td = graphDB.traversalDescription()
                    .breadthFirst()
                    .relationships( RelTypes.CONNECTS, Direction.OUTGOING )
                    .evaluator( Evaluators.excludeStartPosition())
                    .evaluator(Evaluators.toDepth(1));
            for ( Path friendPath : td.traverse(node) )
            {
                System.out.println(friendPath.endNode());
                neighbors++;
            }
            System.out.println("Number of neighbors: "+ neighbors);
            tx.success();
        }*/
        long elapsedTime = System.nanoTime() - start;
        System.out.println("Elapsed time for neighbors is: " + elapsedTime);

    }

    public void getReachabilityCount () {
        Node node;
        int reachableNeighbors = 0;
        long start = System.nanoTime();
        try (Transaction tx = graphDB.beginTx()) {
            node = graphDB.getNodeById(0);
            TraversalDescription td = graphDB.traversalDescription()
                    .breadthFirst()
                    .relationships(RelTypes.CONNECTS, Direction.OUTGOING)
                    .evaluator(Evaluators.excludeStartPosition());
            //.evaluator(Evaluators.toDepth(10));
            for (Path friendPath : td.traverse(node)) {
                System.out.println(friendPath.endNode());
                reachableNeighbors++;
            }
            System.out.println("Number of reachable nodes: " + reachableNeighbors);
            long elapsedTime = System.nanoTime() - start;
            System.out.println("Elapsed time for reachability: " + elapsedTime);
            tx.success();
        }
    }
}
