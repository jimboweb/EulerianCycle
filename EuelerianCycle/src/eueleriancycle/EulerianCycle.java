/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eueleriancycle;





import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jimstewart
 */
public class EulerianCycle {

    private int addEdgeOps;
    private int addNodeOps;
    public int buildCycleOps;

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

    // TODO: 1/21/18 make a single node edge object in node. add single node edges to that and not main nodes. add each sne to cycle without looping through them
    public void run() throws IOException {
        Scanner scanner = new Scanner(System.in);
        ArrayList<int[]> inputs = new ArrayList<>();
        int[] in = new int[2];
        in[0] = scanner.nextInt();
        in[1] = scanner.nextInt();
        if(in[0]==0||in[1]==0){
            System.out.println("0");
            System.exit(0);
        }
        inputs.add(in);
        int n = inputs.get(0)[1];
        for (int i = 0; i < n; i++) {
            in = new int[2];
            in[0] = scanner.nextInt();
            in[1] = scanner.nextInt();
            inputs.add(in);
        }
        Graph g = buildGraph(inputs);
        Cycle c;
        if(!g.isGraphEven()){
            c = new Cycle(0);
        } else {
            c = g.makeEulerianCycle();
        }
        if(c.edges.size()==0){
            //either graph is even or couldn't find edge some other reason
            System.out.println("0");
        } else {
            System.out.println("1");
            for (int i : c.outputAsArray(g)) {
                System.out.print(i + " ");
            }
            System.out.println();
        }

    }

    class Graph{
        private Edge[] edges;
        private Node[] nodes;
        int nodeNum;
        int edgeNum;
        public Graph(int n, int m){
            edges = new Edge[m];
            nodes = new Node[n];
            this.nodeNum = n;
            this.edgeNum = m;
        }


        public Edge addEdge(int ind, int from, int to){
            Edge e = new Edge(from, to);
            e.nodeNum = ind;
            edges[ind] = e;
            return e;
        }
        public ArrayList<Integer> edgesFromEdge(int e){
            return nodes[edges[e].to].edgesOut;
        }
        public ArrayList<Integer> edgesToEdge(int e){
            return nodes[edges[e].from].edgesIn;

        }

        public ArrayList<Integer> singleNodeEdgesFromEdge(int e){
            return nodes[edges[e].to].singleNodeEdges;
        }

        public int size(){
            return edges.length;
        }
        public Edge getEdge(int n){
            return edges[n];
        }

        public boolean isGraphEven(){
            for(Node n:nodes){
                if (n.edgesIn.size()!=n.edgesOut.size()){
                    return false;
                }
            }
            return true;
        }


        public Cycle makeEulerianCycle(){
            Cycle newCycle;
            Cycle oldCycle = new Cycle(edges.length);
                //find new node to start from
            do{    
                newCycle= new Cycle(edges.length);

                //mostRecentOpenEdgeIndex will be changed but not returned
                newCycle = oldCycle.startNewCycle(newCycle,this);
                if(newCycle.edges.size()==0){
                    //return empty cycle because couldn't find available edge
                    return new Cycle(0);
                }
                //do old cycle
                newCycle = growCycle(newCycle);
                newCycle.appendCycle(oldCycle);
                 //make new cycle
                oldCycle = newCycle.copy();
            } while (newCycle.edges.size() < edges.length);
           return newCycle;
        }

        /**
         * add new edges to the cycle from before
         * @param newCycle
         * @return
         */
        Cycle growCycle(Cycle newCycle){
            int nextEdge = newCycle.getFirstEdge();
            while (edges[nextEdge].to!=edges[newCycle.getFirstEdge()].from){
                for(Integer e:singleNodeEdgesFromEdge(nextEdge)){
                    if(!newCycle.visited[e]) {
                        newCycle.addEdge(e);
                    }
                    buildCycleOps++;//debug
                }
                for(Integer e:edgesFromEdge(nextEdge)){
                    if(!newCycle.visited[e]){
                        nextEdge = e;
                        newCycle.addEdge(nextEdge);
                        break;
                    }
                    buildCycleOps++;//debug
                }
            }
             
            return newCycle;
        }

    }
    
    
    class Edge{
        private int from;
        private int to;
        private int nodeNum;
        public Edge(int from, int to){
            this.from = from;
            this.to = to;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

        public int getNodeNum() {
            return nodeNum;
        }
    }
    
    class Node{
        private ArrayList<Integer> edgesIn;
        private ArrayList<Integer> edgesOut;
        private ArrayList<Integer> singleNodeEdges;
        private final int nodeNum;
        public Node(int nodeNum){
            edgesIn = new ArrayList<>();
            edgesOut = new ArrayList<>();
            singleNodeEdges = new ArrayList<>();
            this.nodeNum = nodeNum;
        }

        public void addSingleNodeEdge(int n){
            singleNodeEdges.add(n);
        }

        public ArrayList<Integer> getSingleNodeEdges() {
            return singleNodeEdges;
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
        private boolean[] visited;
        private int graphSize;
        public Cycle(int graphSize){
            edges = new ArrayList<>();
            visited = new boolean[graphSize];
            this.graphSize=graphSize;
        }
        //newCyclePreviousEdge is the LOCAL INDEX IN THIS CYCLE of the edge the next cycle starts from
        public Integer newCyclePreviousEdge;


        /**
         * this starts a new cycle, setting its firstEdge and newCyclePreviousEdge
         * @param newCycle the new cycle, should be empty
         * @param gr the graph
         * @return a new cycle with correct firstEdge and newCyclePreviousEdge
         */
        private Cycle startNewCycle(Cycle newCycle, Graph gr){
            if(size()==0){
                newCycle.addEdge(0);
                newCycle.setNewCyclePreviousEdge(-1);
                return newCycle;
            }
            //nextEdgeLocalIndex is the index in this cycle of the edge we're looking at
            int nextEdgeLocalIndex = edges.size()-1;
            int firstEdge = -1;
            while(firstEdge==-1){
//                if(edges.get(nextEdgeLocalIndex))
                for(int e:gr.edgesFromEdge(edges.get(nextEdgeLocalIndex))){
                    if(!visited[e]){
                        firstEdge = e;
                        newCycle.setNewCyclePreviousEdge(nextEdgeLocalIndex);
                        break;
                    }
                    buildCycleOps++;//debug
                }
                nextEdgeLocalIndex--;
            }
            newCycle.addEdge(firstEdge);
            newCycle.setPrevIsVisited(this);
            return newCycle;
        }


        /**
         * this appends the other cycle starting from newCyclePreviousEdge and then wrapping around
         * @param otherCycle
         */
        public void appendCycle(Cycle otherCycle){
            int prevEdge = getNewCyclePreviousEdge();
            if (prevEdge >= 0 && otherCycle.size()>prevEdge) {
                List<Integer> appendCycle = otherCycle.edges.subList(prevEdge+1,otherCycle.edges.size());
                appendCycle.addAll(otherCycle.edges.subList(0,prevEdge+1));
                edges.addAll(appendCycle);
            }
        }

        public Integer getNewCyclePreviousEdge() {
            return newCyclePreviousEdge;
        }

        public void setNewCyclePreviousEdge(Integer newCyclePreviousEdge) {
            this.newCyclePreviousEdge = newCyclePreviousEdge;
        }

        public void addEdge(int e){
            edges.add(e);
            visited[e] = true;
        }
        public int size(){
            return edges.size();
        }
        public int getEdge(int n){
            return edges.get(n);
        }
        public int getFirstEdge(){
            if (edges.size()>0) {
                return edges.get(0);
            }
            return -1;
        }
        public int getLastEdge(){return edges.get(edges.size()-1);}
        public Cycle copy(){
            Cycle c = new Cycle(graphSize);
            c.edges = new ArrayList<>(edges);
            c.visited = Arrays.copyOf(visited, visited.length);
            return c;
        }
        public int[] outputAsArray(Graph graph){
            int[] rtrn = new int[edges.size()];
            for(int i=0;i<edges.size();i++){
                rtrn[i] = graph.edges[edges.get(i)].from+1;
            }
            return rtrn;
        }

        /**
         * sets all the true visited in the old cycle to true in the new cycle
         * so growCycle won't go over them
         * @param oldCycle
         */
        private void setPrevIsVisited(Cycle oldCycle){
            if(this.visited.length!=oldCycle.visited.length){
                throw new IllegalArgumentException("new cycle's visited not the same length as old cycle visited");
            }
            for(int i=0;i<visited.length;i++){
                if(!visited[i] && oldCycle.visited[i]){
                    visited[i] = true;
                }
                buildCycleOps++;
            }
        }

        @Override
        public String toString() {
            String rtrnString = "Cycle: ";
            for(Integer e:edges){
                rtrnString += e + " ";
            }
            return rtrnString;
        }
    }


    public Graph buildGraph(ArrayList<int[]> inputs){
        int n = inputs.get(0)[0];
        int m = inputs.get(0)[1];
        Graph g = new Graph(n,m);
        for(int i=1;i<inputs.size();i++){
            int x=inputs.get(i)[0];
            int y=inputs.get(i)[1];
            int from = x-1;
            int to = y-1;
            int edgeInd = i-1;
            Edge e = g.addEdge(edgeInd, from, to);
            addEdgeOps++; //debug
            g.nodes = addOrModifyNodes(g.nodes,edgeInd,from,to);
        }
       return g;
    }

    private Node[] addOrModifyNodes(Node[] nodes, int edgeNum, int from, int to){
        Node nextNode;
        addNodeOps++; //debug
        if(nodes[from]!=null){
            nextNode = nodes[from];
        } else {
            nextNode = new Node(from);
            nodes[from] = nextNode;
        }

        //this is for single node edges so they won't clutter the to and from edges
        if(from == to){
            if(!nextNode.singleNodeEdges.contains((edgeNum))){
                nextNode.singleNodeEdges.add(edgeNum);
            }
            return nodes;
        }

        if(!nextNode.edgesOut.contains(edgeNum)){
            nextNode.edgesOut.add(edgeNum);
        }

        if(nodes[to]!=null){
            nextNode = nodes[to];
        } else {
            nextNode = new Node(to);
            nodes[to]=nextNode;
        }
        if(!nextNode.edgesIn.contains(edgeNum)){
            nextNode.edgesIn.add(edgeNum);
        }
        return nodes;
    }

}
