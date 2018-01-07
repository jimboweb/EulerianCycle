/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eueleriancycle;

import eueleriancycle.EulerianCycle.Cycle;
import eueleriancycle.EulerianCycle.Edge;
import eueleriancycle.EulerianCycle.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jimstewart
 */
public class EulerianCycleTest {
    
    public EulerianCycleTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class EulerianCycle.
     */
//    @Test
//    public void testMain() {
//        System.out.println("main");
//        String[] args = null;
//        EulerianCycle.main(args);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of run method, of class EulerianCycle.
     */
//    @Test
//    public void testRun() throws Exception {
//        System.out.println("run");
//        EulerianCycle instance = new EulerianCycle();
//        instance.run();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    @Test
    public void testMakeEulerianCycle(){
        EulerianCycle instance = new EulerianCycle();
        List<int[]> konigsburg = new ArrayList<>();
        String kString =
                "4 7" +
                        "1 2" +
                        "1 3" +
                        "2 1" +
                        "3 1" +
                        "2 4" +
                        "3 4" +
                        "4 1";
        List<int[]> simpleInput = new ArrayList<>();
        String simpleInputs =
                            "8 9\n" +
                            "1 2\n" +
                            "2 3\n" +
                            "3 1\n" +
                            "1 4\n" +
                            "4 5\n" +
                            "5 6\n" +
                            "6 7\n" +
                            "7 8\n" +
                            "8 1";
        String[] simpleInputLines = simpleInputs.split("\n");

        int[] simpleOutputAnswer = {1,2,3,1,4,5,6,7,8,1};
        for(String si:simpleInputLines){
            String[] inputString = si.split(" ");
            int[] inputNums = new int[2];
            inputNums[0] = Integer.parseInt(inputString[0]);
            inputNums[1] = Integer.parseInt(inputString[1]);
            simpleInput.add(inputNums);
        }
        Graph simpleG = instance.buildGraph((ArrayList<int[]>)simpleInput);
        Cycle simpleC = simpleG.makeEulerianCycle();
        assertArrayEquals(simpleOutputAnswer, simpleC.outputAsArray(simpleG));

        //work with this after the simple answer works

//
//        ArrayList<int []> input = makeGraphInput();
//        Graph g = instance.buildGraph(input);
//        Cycle c = g.makeEulerianCycle();
//        assert(testEulerianCycle(c, g));
    }
    private ArrayList<int []> makeGraphInput(){
        Random rnd = new Random();
        int n = rnd.nextInt(10) + 2;
        int m = 0;
//        int m = (rnd.nextInt(triangular(n) - n) + n)/2 * 2  ; // use my triangular function
        ArrayList<int []> input = new ArrayList<>();
        int[] firstLine = {n, m};
        input.add(firstLine);
        int nextNode = 0;
        ArrayList <Integer> usedNodes = new ArrayList<>();
        int firstNode = rnd.nextInt(n);
        int prevNode = firstNode;
        for(int i=1;i<n;i++){
            usedNodes.add(prevNode);
            do{
                nextNode = rnd.nextInt(n);
            } while(usedNodes.contains((Integer)nextNode));
            int[] nextLine = {prevNode+1, nextNode+1};
            input.add(nextLine);
            m++;
            prevNode = nextNode;
        }
        usedNodes.add(nextNode);
        int[] nextLine = {prevNode+1, firstNode+1};
        input.add(nextLine);
        // now add a few extra cycles
        int moreCycles = rnd.nextInt(triangular(n))- (m + 3);
        moreCycles = moreCycles < 2 ? 2 : moreCycles;
        int maxEdges = triangular(n-1);
        for(int i=0;i<moreCycles;i++){
            firstNode = rnd.nextInt(n);
            prevNode = firstNode;
            usedNodes = new ArrayList<>();
            usedNodes.add(prevNode);
            do{
                do{
                    //[tk] think this works now try it
                    nextNode = rnd.nextInt(n);
                } while (usedNodes.contains((Integer)nextNode) && input.size()<maxEdges-2 && usedNodes.size()<(n-2));
                if(nextNode == prevNode){
                    nextNode = firstNode;
                }
                int[] newLine = {prevNode+1, nextNode+1};
                int[] newLineRev = {nextNode+1, prevNode+1};
                //this if statement isn't working, have to use something else
                boolean edgeUsed = false;
                for(int[] arr:input){
                    if(Arrays.equals(arr, newLine)||Arrays.equals(arr, newLineRev)){
                        edgeUsed = true;
                    }
                }
                if(!edgeUsed){
                    input.add(newLine);
                    m++;
                }
                prevNode = nextNode;
                usedNodes.add(nextNode);
            } while(nextNode!=firstNode);
        }
        input.get(0)[1] = m + 1;
        return input;
    }
    
    private boolean testEulerianCycle(Cycle c, Graph g){
        //make sure that the cycle has all the nodes
        Edge nextEdge = g.getEdge(c.getFirstEdge());
        Edge thisEdge;
        if(c.size()<g.size())
            return false;
        //make sure each node's "to" == the next node's "from"
        for(int i=0;i<c.size()-1;i++){
            nextEdge = g.getEdge(c.getEdge(i+1));

            thisEdge = g.getEdge(c.getEdge(i));
            if(nextEdge.getFromNode()!=thisEdge.getToNode())
                return false;
        }
        if(g.getEdge(c.getFirstEdge()).getFromNode()!= nextEdge.getToNode())
            return false;
        //[tk]make sure the start equals the end
        if(c.getFirstEdge()!=c.getEdge(c.size()-1))
            return false;
        return true;
    }
    
    private static int triangular(int n){
        int tri = 0;
        for(int i=1; i<n; i++){
            tri = tri + i;
        }
        return tri;
    }


//    /**
//     * Test of buildGraph method, of class EulerianCycle.
//     */
//    @Test
//    public void testBuildGraph() {
//        System.out.println("buildGraph");
//        ArrayList inputs = null;
//        EulerianCycle instance = new EulerianCycle();
//        EulerianCycle.Graph expResult = null;
//        EulerianCycle.Graph result = instance.buildGraph(inputs);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
