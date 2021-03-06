package Blockchain.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

public class GraphUtil {
    public static class EdgeTo {
        public final int index;
        public final long weight;
        public EdgeTo(int index, long weight) {
            this.index = index;
            this.weight = weight;
        }
        @Override
        public String toString(){
            return String.format("(%d: %d)", index, weight);
        }
        @Override
        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) 
                return false;
            return this.index == ((EdgeTo) obj).index;
        }
    }
    
    /**
     * Creates a graph defined by an adjacency list as specified by the defined adjacency matrix.
     * Edge weights are sampled from a gaussian distributian with constant mean.
     * @param b The adjacency matrix.
     * @param mean The mean of the edge weights.
     * @param symmetric Wether the specified adjacency matrix should be treated as symmetric.
     * @return The generated graph represented by an adjacency list.
     */
    public static ArrayList<LinkedList<EdgeTo>> fromBoolMatrix(int[][] b, long mean, boolean symmetric) {
        Random rnd = new Random();
        int n = b.length;
        long[][] m = new long[n][n];
        for(int i = 0; i < n; i++) {
            for(int j = i+1; j < n; j++) {
                if(b[i][j]>0){
                    long latency = (long) Util.nextGaussian(rnd, mean);
                    m[i][j] = latency;
                    if(symmetric){
                        m[j][i] = latency;
                        continue;
                    }
                }else{
                    m[i][j] = -1;
                }
                if(b[j][i]>0){
                    long latency = (long) Util.nextGaussian(rnd, mean);
                    m[j][i] = latency;
                    if(symmetric)
                        m[i][j] = latency;
                }else{
                    m[j][i] = -1;
                }
            }
        }
        return fromAdjMatrix(m);
    }
    
    /**
     * Transforms a graph's matrix representation into an adjacency list.
     * @param m The adjacency matrix.
     * @return The adjacency list.
     */
    public static ArrayList<LinkedList<EdgeTo>> fromAdjMatrix(long[][] m) {
        int n = m.length;
        ArrayList<LinkedList<EdgeTo>> adj = new ArrayList<>(n);
        for(int i = 0; i < n; i++) {
            adj.add(new LinkedList<>());
        }
        for(int i = 0; i < n; i++) {
            for(int j = i+1; j < n; j++) {
                if(m[i][j] >= 0){
                    adj.get(i).add(new EdgeTo(j, m[i][j]));
                }
                if(m[j][i] >= 0){
                    adj.get(j).add(new EdgeTo(i, m[j][i]));
                }
            }
        }
        return adj;
    }
    
    /**
     * Creates a pseudo-random, connected, symmetric graph with the given number of nodes and edges.
     * Edge weights are sampled from a gaussian distributian with constant mean.
     * @param n The number of nodes contained in the graph.
     * @param edges The number of edges contained in the graph.
     * @param mean The mean of the edge weights.
     * @return The generated graph represented by an adjacency list.
     */
    public static ArrayList<LinkedList<EdgeTo>> rndGraph(int n, int edges, long mean) {
        edges = Math.min((n*(n-1))/2, Math.max(edges, n-1));
        Random rnd = new Random();
        ArrayList<LinkedList<EdgeTo>> adj = new ArrayList<>(n);
        boolean[][] con = new boolean[n][n];

        /**
         * Generate the spanning tree.
         */
        adj.add(new LinkedList<>());
        for(int i = 1; i < n; i++){
            adj.add(new LinkedList<>());
            int index = rnd.nextInt(i);
            long latency = (long) Util.nextGaussian(rnd, mean);
            adj.get(index).add(new EdgeTo(i, latency));
            adj.get(i).add(new EdgeTo(index, latency));
            con[i][index] = con[index][i] = true;
        }
        edges -= (n-1);
        
        RndIntSet set = new RndIntSet(n);
        for (int i = 0; i < n; i++) {
            if(adj.get(i).size() < n-1)
                set.add(i);
        }
        
        /**
         * Distribute the remaining edges.
         */
        while(edges-- > 0){
            int x = set.removeRandom(rnd);
            int y = getFreeNeighbor(x, rnd, con, set);
            
            long latency = (long) Util.nextGaussian(rnd, mean);
            adj.get(x).add(new EdgeTo(y, latency));
            adj.get(y).add(new EdgeTo(x, latency));
            con[x][y] = con[x][y] = true;
            if(adj.get(x).size() < n-1)
                set.add(x);
            if(adj.get(y).size() < n-1)
                set.add(y);
        }
        return adj;
    }
    
    private static int getFreeNeighbor(int x, Random rnd, boolean[][] con, RndIntSet set){
        int y = set.removeRandom(rnd);
        if(!con[x][y]&&!con[y][x])
            return y;
        int yn = getFreeNeighbor(x, rnd, con, set);
        set.add(y);
        return yn;
    }
    
    /**
     * Implementation of Dijkstra's shortest path algorithm.
     * @param adj The graph represented by its adjcency list.
     * @param start The starting node.
     * @return A distance array containing the starting node's distance to each other node.
     */
    public static long[] dijkstra(ArrayList<LinkedList<EdgeTo>> adj, int start){
        int n = adj.size();
        long[] d = new long[n];
        Arrays.fill(d, -1);
        d[start] = 0;
        PriorityQueue<EdgeTo> pq = new PriorityQueue<>(n, (EdgeTo o1, EdgeTo o2) 
                -> (int)(o1.weight - o2.weight));
        pq.offer(new EdgeTo(start, 0));
        while(!pq.isEmpty()){
            EdgeTo v = pq.poll();
            for(EdgeTo w : adj.get(v.index)){
                long newDist = d[v.index] + w.weight;
                if(d[w.index] == -1 || newDist < d[w.index]){
                    if(d[w.index] != -1){
                        pq.removeIf(x -> x.index == w.index);
                    }
                    pq.offer(new EdgeTo(w.index, newDist));
                    d[w.index] = newDist;
                }
            }
        }
        return d;
    }
    
    /**
     * Solving All Pairs Shortest Paths for a graph represented by its adjacency list
     * by calling dijkstra for each node.
     * @param adj This graph's adjacency List
     * @return A distance matrix containing the lengths of all shortest Paths
     */
    public static long[][] apsp(ArrayList<LinkedList<EdgeTo>> adj){
        int n = adj.size();
        long[][] d = new long[n][];
        for(int i = 0; i < n; i++){
            d[i] = dijkstra(adj, i);
        }
        return d;
    }
}
