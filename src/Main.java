

import Blockchain.Parameters;
import DoubleSpend.DSSimulation;
import Blockchain.Parameters.ParametersBuilder;
import Blockchain.Peers.*;
import OrphanRate.ORSimulation;
import java.io.IOException;

public class Main{
    
    public static void main(String[] args) {  
        Parameters p = null;
        try {
            if(args != null && args.length > 0 && args[0] != null){
                p = (new ParametersBuilder())
                        .loadFromProperty(args[0])
                        .build();
            }else{                                             
                p = (new ParametersBuilder())
                        .loadFromProperty("parameters.txt")
                        .build();
            }
        } catch (IOException ex) {
            System.err.println("Error loading parameters.");
            System.exit(1);
        }
        
        orphanRate(p);

    }
    
    public static void doubleSpend(Parameters p){
        System.out.printf("Attempting %d double spends on a Blockchain with\n"
                + "mining difficulty %s and %d confirmations.\n"
                + "Network: %d trusted and %d attacking nodes.\n"
                + "MaxLead: %d, MaxLength: %d\n", 
                p.getRuns(), ""+p.getDifficulty(), p.getConfirmations(), 
                p.getTrustedNodes(), p.getAttackerNodes(), p.getMaxLead(), p.getMaxLength());
        
        PeerStrategy trustedPeerStrategy =  new RndGraphPeerStrategy(p.getTrustedNodes(), 12, 500, 100);
        PeerStrategy attackerPeerStrategy = new RndGraphPeerStrategy(p.getAttackerNodes(), 4, 500, 100);
        AttackerStrategy attackerStrategy = new ConstantAttackerStrategy(0, 0);
        
        DSSimulation sim = new DSSimulation(p, trustedPeerStrategy, attackerPeerStrategy, attackerStrategy, true);
        sim.start();
    }

    private static int[][] star1 = new int[][]
    {{0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0},
     {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
    };
    
    private int[][] star2 = new int[][]
    {{0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0},
     {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
    };
    
    private int[][] line = new int[][]
    {{0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
    };
    
    private int[][] ring = new int[][]
    {{0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
     {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
    };
    
    public static void orphanRate(Parameters p) {
        System.out.printf("Measuring Orphan Rate in %d mined Blocks\n"
                + "by a Network with mining difficulty %s and %d Nodes.\n",
                p.getMaxLength(),""+p.getDifficulty(),p.getNodes());
        
        PeerStrategy ORPeerStrategy = new BoolMatrixPeerStrategy(star1, true, 0, 0);
        ORSimulation sim = new ORSimulation(p, ORPeerStrategy);
        sim.start();
    }
}
