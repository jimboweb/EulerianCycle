/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eueleriancycle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jimstewart
 */
public class EulerianCycle {

    /**
     * @param args the command line arguments
     */
    class Graph{
        private Edge[] edges;
        public Graph(int n, int m){
            edges = new Edge[m];
        }
        public Edge addEdge(int ind, int from, int to){
            Edge e = new Edge(from, to);
            e.nodeNum = ind;
            try{
            edges[ind] = e;
            }catch(ArrayIndexOutOfBoundsException err){
                System.out.println();
            }
            return e;
        }
        public ArrayList<Integer> edgesFromEdge(int e){
            return edges[e].edgesOut;
        }
        public ArrayList<Integer> edgesToEdge(int e){
            return edges[e].edgesIn;
            
        }
        public int size(){
            return edges.length;
        }
        public Edge getEdge(int n){
            return edges[n];
        }

        public Cycle makeEulerianCycle(){
            Cycle newCycle;
            Cycle oldCycle = new Cycle(edges.length);
            oldCycle.firstEdge = 0;
                //find new node to start from
            do{    
                newCycle= new Cycle(edges.length);
                newCycle.firstEdge = findFirstEdge(oldCycle);
                newCycle.addEdge(newCycle.firstEdge);
                //do old cycle
                newCycle = doPrevCycle(newCycle, oldCycle);
                //[tk] may need a "lastEdge" variable
                //make new cycle
                newCycle = growCycle(newCycle);
                oldCycle = newCycle.copy();
            } while (newCycle.edges.size() < edges.length);
           return newCycle;
        }
        Cycle growCycle(Cycle newCycle){
            int nextEdge = newCycle.firstEdge;
            do{
                for(Integer e:edgesFromEdge(nextEdge)){
                    if(!newCycle.visited[e]){
                        nextEdge = e;
                        newCycle.addEdge(nextEdge);
                        break;
                    }
                }
            } while (edges[nextEdge].to!=edges[newCycle.firstEdge].from);
             
            return newCycle;
        }
        private Cycle doPrevCycle(Cycle newCycle, Cycle oldCycle){
            if(oldCycle.size()==0)
                    return newCycle;
            int nextEdge = newCycle.firstEdge;
            for(int i=0;i<oldCycle.size();i++){
                newCycle.addEdge(nextEdge);
                try{
                    nextEdge = oldCycle.edges.get(nextEdge);
                }catch (IndexOutOfBoundsException err){
                    System.out.println(); 
                }
            } 

            return newCycle;
        }
        /**
         * Finds a new first edge with unvisited edges out
         * from previous cycle
         * @param oldCycle
         * @return the edge number of the new first edge
         */
        private int findFirstEdge(Cycle oldCycle){
            if(oldCycle.size()==0)
                return 0;
            int edge = oldCycle.firstEdge;
            int firstEdge = -1;
            while(firstEdge==-1){
                for(int e:edgesFromEdge(edge)){
                    if(!oldCycle.visited[e]){
                        firstEdge = e;
                        break;
                    }
                }
            }
            return firstEdge;
        }
    }
    
    
    class Edge{
        private int from;
        private int to;
        private int nodeNum;
        private ArrayList<Integer> edgesIn;
        private ArrayList<Integer> edgesOut;
        public Edge(int from, int to){
            this.from = from;
            this.to = to;
            this.edgesOut = new ArrayList<>();
            this.edgesIn = new ArrayList<>();
        }
        private int from(){
            return from;
        }
        private int to(){
            return to;
        }
//        For when I try to get rid of the nodes
        public void addEdgeIn(int n){
            edgesIn.add(n);
        }
        //[tk] move this to the edge instead
        public ArrayList<Integer> getEdgesIn(){
            return edgesIn;
        }
        public void addEdgeOut(int n){
            edgesOut.add(n);
        }
        public ArrayList<Integer> getEdgesOut(){
            return edgesOut;
        }
        public int getFromNode(){
            return from;
        }
        public int getToNode(){
            return to;
        }
    }
    
    class Node{
        private ArrayList<Integer> edgesIn;
        private ArrayList<Integer> edgesOut;
        private int nodeNum = -1;
        public Node(int nodeNum){
            edgesIn = new ArrayList<>();
            this.nodeNum = nodeNum;
        }
        public void setNodeNum(int n){
            nodeNum = n;
        }
        public int getNodeNum(){
            return nodeNum;
        }
        public void addEdgeIn(int n){
            edgesIn.add(n);
        }
        public int getEdgeIn(int n){
            return edgesIn.get(n);
        }
        public ArrayList<Integer> getEdgesIn(){
            return edgesIn;
        }
        public void addEdgeOut(int n){
            edgesOut.add(n);
        }
        public int getEdgeOut(int n){
            return edgesOut.get(n);
        }
        public ArrayList<Integer> getEdgesOut(){
            return edgesOut;
        }
    }
    
    class Cycle{
        private ArrayList<Integer> edges;
        private int firstEdge;
        private boolean[] visited;
        private int graphSize;
        public Cycle(int graphSize){
            edges = new ArrayList<>();
            visited = new boolean[graphSize];
            this.graphSize=graphSize;
            firstEdge = -1;
        }
        public void addEdge(int e){
            edges.add(e);
            if(firstEdge == -1)
                firstEdge = e;
            visited[e] = true;
        }
        public int size(){
            return edges.size();
        }
        public int getEdge(int n){
            return edges.get(n);
        }
        public int getFirstEdge(){
            return firstEdge;
        }
        public Cycle copy(){
            Cycle c = new Cycle(graphSize);
            c.edges = new ArrayList<>(edges);
            c.firstEdge = this.firstEdge;
            c.visited = Arrays.copyOf(visited, visited.length);
            return c;
        }
    }
    
    public static void main(String[] args) {
      new Thread(null, new Runnable() {
                    public void run() {
                        try {
                            new EulerianCycle().run();
                        } catch (IOException ex) {
                            Logger.getLogger(EulerianCycle.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }, "1", 1 << 26).start();
     }
    public void run() throws IOException {
        Scanner scanner = new Scanner(System.in);
        ArrayList<int[]> inputs = new ArrayList<>();
        int[] in = new int[2];
        in[0] = scanner.nextInt();
        in[1] = scanner.nextInt();
        inputs.add(in);
        int n = inputs.get(0)[1];
        for (int i = 0; i < n; i++) {
            in = new int[2];
            in[0] = scanner.nextInt();
            in[1] = scanner.nextInt();
            inputs.add(in);
        }
        Graph g = buildGraph(inputs);
        Cycle c = g.makeEulerianCycle();
        System.out.print("");
    }
    
    public Graph buildGraph(ArrayList<int[]> inputs){
        int n = inputs.get(0)[0];
        int m = inputs.get(0)[1];
        ArrayList<ArrayList<Integer>> edgesFromNode = new ArrayList<>();
        for(int i=0;i<n;i++){
            edgesFromNode.add(new ArrayList<>());
        }
        ArrayList<ArrayList<Integer>> edgesToNode = new ArrayList<>();
        for(int i=0;i<n;i++){
            edgesToNode.add(new ArrayList<>());
        }        
        Graph g = new Graph(n,m);
        for(int i=1;i<inputs.size();i++){
            int x=inputs.get(i)[0];
            int y=inputs.get(i)[1];
            Edge e = g.addEdge(i-1, x-1, y-1);
            edgesFromNode.get(x-1).add(i-1);
            edgesToNode.get(y-1).add(i-1);
        }
        for(Edge e:g.edges){
            for(Integer edgeOut:edgesFromNode.get(e.to)){
                e.edgesOut.add(edgeOut);
            }
            for(Integer edgeIn:edgesToNode.get(e.from)){
                e.edgesIn.add((edgeIn));
            }
        }
        return g;
    }

}
