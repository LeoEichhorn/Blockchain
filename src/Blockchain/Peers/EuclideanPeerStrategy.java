package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Util.Parameter;
import Blockchain.Util.Util;
import java.util.ArrayList;
import java.util.Random;

public class EuclideanPeerStrategy extends PeerStrategy{
    private Parameter<Integer> side;
    private Random rnd;

    /**
     * All Nodes are assigned random coordinates in a 2D square. The latency
     * between two Nodes is sampled from a normal distribution whose mean is
     * proportional to the nodes' euclidean distance with constant standard deviation.
     * @param side The width and height of the square.
     */
    public EuclideanPeerStrategy(Parameter<Integer> side) {
        this.side = side;
        this.rnd = new Random();
    }

    @Override
    public long connectPeers(ArrayList<Node> nodes) {
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
        double mean = Math.sqrt(Math.pow(a.x-b.x, 2)+Math.pow(a.y-b.y, 2));
        return (long) Util.nextGaussian(rnd, mean);
    }
    
    private Point rndPoint() {
        return new Point(
            rnd.nextInt(side.getValue() + 1),
            rnd.nextInt(side.getValue() + 1)
        );
    }

    private class Point {
        public final int x,y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString() {
            return String.format("(%d,%d)", x, y);
        }
    }
}
