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
    
    public static ArrayList<LinkedList<EdgeTo>> fromBoolMatrix(int[][] b, long mean, double stdDev, boolean symmetric) {
        Random rnd = new Random();
        int n = b.length;
        long[][] m = new long[n][n];
        for(int i = 0; i < n; i++) {
            for(int j = i+1; j < n; j++) {
                if(b[i][j]>0){
                    long latency = (long) Util.nextGaussian(rnd, mean, stdDev);
                    m[i][j] = latency;
                    if(symmetric){
                        m[j][i] = latency;
                        continue;
                    }
                }else{
                    m[i][j] = -1;
                }
                if(b[j][i]>0){
                    long latency = (long) Util.nextGaussian(rnd, mean, stdDev);
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
    
    public static ArrayList<LinkedList<EdgeTo>> rndGraph(int n, int edges, long mean, double stdDev) {
        edges = Math.min((n*(n-1))/2, Math.max(edges, n-1));
        Random rnd = new Random();
        ArrayList<LinkedList<EdgeTo>> adj = new ArrayList<>(n);
        adj.add(new LinkedList<>());
        for(int i = 1; i < n; i++){
            adj.add(new LinkedList<>());
            int index = rnd.nextInt(i);
            long latency = (long) Util.nextGaussian(rnd, mean, stdDev);
            adj.get(index).add(new EdgeTo(i, latency));
            adj.get(i).add(new EdgeTo(index, latency));
        }
        edges -= (n-1);
        while(edges --> 0){
            int x;
            do {
                x = rnd.nextInt(n);
            }while(adj.get(x).size() == n-1);
            int y;
            do {
                y = rnd.nextInt(n);
            }while(x == y || adj.get(y).size() == n-1 || adj.get(y).contains(new EdgeTo(x, 0)));
            long latency = (long) Util.nextGaussian(rnd, mean, stdDev);
            adj.get(x).add(new EdgeTo(y, latency));
            adj.get(y).add(new EdgeTo(x, latency));
        }
        return adj;
    }
    
    public static long[] dijkstra(ArrayList<LinkedList<EdgeTo>> adj, int start){
        int n = adj.size();
        long[] d = new long[n];
        Arrays.fill(d, -1);
        d[start] = 0;
        PriorityQueue<EdgeTo> pq = new PriorityQueue<>(n, (EdgeTo o1, EdgeTo o2) -> (int)(o1.weight - o2.weight));
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
     * @return The length of the shortest Paths
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
