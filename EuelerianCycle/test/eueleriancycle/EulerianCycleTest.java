/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eueleriancycle;

import eueleriancycle.EulerianCycle.Cycle;
import eueleriancycle.EulerianCycle.Edge;
import eueleriancycle.EulerianCycle.Graph;

import java.util.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
//        List<int[]> konigsburg = new ArrayList<>();
//        String kString =
//                "4 7" +
//                        "1 2" +
//                        "1 3" +
//                        "2 1" +
//                        "3 1" +
//                        "2 4" +
//                        "3 4" +
//                        "4 1";
//        List<int[]> simpleInput = new ArrayList<>();
//        String simpleInputs =
//                            "8 9\n" +
//                            "1 2\n" +
//                            "2 3\n" +
//                            "3 1\n" +
//                            "1 4\n" +
//                            "4 5\n" +
//                            "5 6\n" +
//                            "6 7\n" +
//                            "7 8\n" +
//                            "8 1";
//        String[] simpleInputLines = simpleInputs.split("\n");
//
//        int[] simpleOutputAnswer = {1,2,3,1,4,5,6,7,8};
//        for(String si:simpleInputLines){
//            String[] inputString = si.split(" ");
//            int[] inputNums = new int[2];
//            inputNums[0] = Integer.parseInt(inputString[0]);
//            inputNums[1] = Integer.parseInt(inputString[1]);
//            simpleInput.add(inputNums);
//        }
//        Graph simpleG = instance.buildGraph((ArrayList<int[]>)simpleInput);
//        Cycle simpleC = simpleG.makeEulerianCycle();
//        int[] correctAnswer = simpleC.outputAsArray(simpleG);
//        assertArrayEquals(simpleOutputAnswer, correctAnswer);

        //work with this after the simple answer works


        InputGraph input = makeBalancedInputGraph();
        //TODO: create the cycle, then check to see if the cycle is Eulerian
//        Graph g = instance.buildGraph(input);
//        Cycle c = g.makeEulerianCycle();
//        assert(testEulerianCycle(c, g));
    }

    class InputNode{
        private ArrayList<Integer> edgesOut;
        private ArrayList<Integer> edgesIn;
        private final int index;
        //positive balance = more out than in
        //neg. balance = more in than out
        //0 balance = even
        private int balance;

        public InputNode(int index) {
            this.edgesOut = new ArrayList<>();
            this.edgesIn = new ArrayList<>();
            this.index = index;
            this.balance = 0;
        }

        public void addEdgeOut(int e){
            edgesOut.add(e);
            balance--;
        }
        public void addEdgeIn(int e){
            edgesIn.add(e);
            balance++;
        }

        public int getBalance() {
            return balance;
        }

        public boolean balanceIsPositive(){
            return balance>0;
        }

        public boolean balanceIsNegative(){
            return balance<0;
        }
        public boolean isBalanced(){
            return balance==0;
        }

        public int getIndex() {
            return index;
        }
    }

    class InputEdge{
        private final int index;
        private final int fromNode;
        private final int toNode;

        public InputEdge(int index, int fromNode, int toNode) {
            this.index = index;
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        public int getIndex() {
            return index;
        }

        public int getFromNode() {
            return fromNode;
        }

        public int getToNode() {
            return toNode;
        }

        @Override
        public String toString() {
            return fromNode + " " + toNode;
        }
    }

    class InputGraph {
        Random rnd = new Random();
        private final ArrayList<InputNode> nodes;
        ArrayList<InputEdge> edges;
        BitSet balancedNodes;
        boolean graphIsBalanced;
        public InputGraph(int size) {
            ArrayList<InputNode> nodes = new ArrayList<>();
            for(int i=0;i<size;i++){
                nodes.add(new InputNode(i));
            }
            this.nodes=nodes;
            this.edges = new ArrayList<>();
            balancedNodes = new BitSet(nodes.size());
        }

        public ArrayList<InputNode> getNodes() {
            return nodes;
        }

        /**
         * <ol>
         *     <li>adds an edge from node to node</li>
         *     <li>adds edge to edgeOut of from and edgeIn of to</li>
         *     <li>sets the balance of node which sets graphIsBalanced</li>
         * </ol>
         *
         * @param from from node
         * @param to to node
         */
        public void addEdge(int from, int to){
            if(!(nodes.size()>=(from))){
                throw new IllegalArgumentException("node " + from + " out of bounds");
            }
            if(!(nodes.size()>=to)){
                throw new IllegalArgumentException("node " + to + "out of bounds");
            }
            int edgeInd = edges.size();
            edges.add(new InputEdge(edgeInd,from,to));
            InputNode fromNode = nodes.get(from);
            InputNode toNode = nodes.get(to);
            fromNode.addEdgeOut(edgeInd);
            setOrClearBalancedNode(fromNode);
            toNode.addEdgeIn(edgeInd);
            setOrClearBalancedNode(toNode);
        }

        /**
         * checks if node is balanced and sets or clears the boolean in balancedNodes
         * @param n node to check
         */
        private void setOrClearBalancedNode(InputNode n){
            int ind = n.index;
            if(n.isBalanced()){
                setBalancedNode(ind);
            } else {
                clearBalancedNode(ind);
            }
        }

        /**
         * sets node in balancedNodes and sets graphIsBalanced
         * @param ind index of node
         */
        private void setBalancedNode(int ind){
            balancedNodes.set(ind);
            setGraphBalance();
        }

        /**
         * clears node in balancedNodes and sets graphIsBalanced
         * @param ind index of node
         */
        private void clearBalancedNode(int ind){
            balancedNodes.clear(ind);
            setGraphBalance();
        }

        /**
         * sets the balance of the graph if it's balanced
         */
        private void setGraphBalance(){
            graphIsBalanced = balancedNodes.cardinality()==1>>nodes.size();
        }

        public InputNode getNode(int ind){
            return nodes.get(ind);
        }
        public InputEdge getEdge(int ind){
            return edges.get(ind);
        }
        public String getInputAsString(){
            String rtrn = nodes.size() + " " + edges.size();
            for(InputEdge e:edges){
                rtrn+=e.toString() + "\n";
            }
            return rtrn;
        }
        public ArrayList<int[]> getInputAsArray(){
            ArrayList<int[]> input = new ArrayList<>();
            int[] firstLine = {nodes.size(),edges.size()};
            input.add(firstLine);
            for(InputEdge e:edges){
                int[] nextLine = {e.fromNode,e.toNode};
                input.add(nextLine);
            }
            return input;
        }

        /**
         * return a random node with optional sign; 0 = sign doesn't matter
         * @param sign 0 for any node, positive for positive node, negative for negative node
         * @return any node if sign 0, otherwise node of balanced sign
         */
        public InputNode getRandomNode(int sign){
            if(sign==0){
                return new InputNode(rnd.nextInt(nodes.size()));
            }
            boolean positive = sign>0;
            BitSet nodeWasTried = new BitSet(nodes.size());
            while(nodeWasTried.cardinality()<1>>nodes.size()){
                int rtrnInd = rnd.nextInt(nodes.size());
                if(nodeWasTried.get(rtrnInd)){
                    continue;
                }
                InputNode possibleRtrn = nodes.get(rtrnInd);
                if(positive) {
                    if (possibleRtrn.balanceIsPositive()) {
                        return possibleRtrn;
                    }
                } else {
                    if (possibleRtrn.balanceIsNegative()){
                        return possibleRtrn;
                    }
                }
                nodeWasTried.set(rtrnInd);
            }
            return new InputNode(-1);
        }

        public boolean graphIsBalanced() {
            return graphIsBalanced;
        }
    }

    /**
     * @return a balanced input graph
     */
    private InputGraph makeBalancedInputGraph(){
        Random rnd = new Random();
        int n = rnd.nextInt(10) + 2;
        boolean balanced = rnd.nextInt(10)<1;
        int m = 0;
        InputGraph gr = new InputGraph(n);
        for(InputNode fromNode:gr.getNodes()){
            int fromNodeInd = fromNode.getIndex();
            InputNode toNode = gr.getRandomNode(0);
            int toNodeInd = toNode.getIndex();
            gr.addEdge(fromNodeInd,toNodeInd);
        }

        while(!gr.graphIsBalanced()){
            InputNode nodeToBalance;
            do{
                nodeToBalance = gr.getRandomNode(0);
            } while(nodeToBalance.isBalanced());
            InputNode toNode;
            if(nodeToBalance.balanceIsPositive()){
                toNode = gr.getRandomNode(-1);
            } else {
                toNode = gr.getRandomNode(1);
            }
            gr.addEdge(nodeToBalance.getIndex(),toNode.getIndex());
        }
        return gr;
    }

    //TODO: make sure this checks if a cycle is Eurlerian
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
