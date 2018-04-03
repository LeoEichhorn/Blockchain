package Blockchain;

import java.util.ArrayList;
import java.util.Random;

public class EuclideanPeerFactory extends AbstractPeerFactory{
    private int width;
    private int height;
    private Random rnd;

    public EuclideanPeerFactory(int width, int height) {
        this.width = width;
        this.height = height;
        this.rnd = new Random();
    }

    @Override
    public long createPeers(ArrayList<Node> nodes) {
        ArrayList<Point> points = new ArrayList<>(nodes.size());
        for(int i = 0; i < nodes.size(); i++){
            points.add(rndPoint());
        }
        long max = 0;
        for(int i = 0; i < nodes.size(); i++) {
            for(int j = i+1; j < nodes.size(); j++) {
                long latency = latency(points.get(i), points.get(j));
                max = Math.max(max, latency);
                nodes.get(i).addPeer(new Peer(nodes.get(j), latency));
                nodes.get(j).addPeer(new Peer(nodes.get(i), latency));
            }
        }
        return max;
    }
    
    private long latency(Point a, Point b) {
        return (long) Math.sqrt(Math.pow(a.x-b.x, 2)+Math.pow(a.y-b.y, 2));
    }
    
    private Point rndPoint() {
        return new Point(
            rnd.nextInt(width + 1),
            rnd.nextInt(height + 1)
        );
    }

    private class Point {
        public final int x,y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
    }
}
