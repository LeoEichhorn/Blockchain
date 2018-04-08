package Blockchain;

import Blockchain.DoubleSpend.DSSimulation;
import Blockchain.Parameters.ParametersBuilder;
import Blockchain.Peers.*;
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
        
        System.out.printf("Attempting %d double spends on a Blockchain with\n"
                + "mining difficulty %s and %d confirmations.\n"
                + "Network: %d trusted and %d attacking nodes.\n"
                + "MaxLead: %d, MaxLength: %d\n", 
                p.getRuns(), ""+p.getDifficulty(), p.getConfirmations(), 
                p.getTrustedNodes(), p.getAttackerNodes(), p.getMaxLead(), p.getMaxLength());
                
        PeerStrategy trustedPeerStrategy =  new GraphPeerStrategy(p.getTrustedNodes(), 12, 0, 0);
        PeerStrategy attackerPeerStrategy = new GraphPeerStrategy(p.getAttackerNodes(), 4, 0, 0);
        AttackerStrategy attackerStrategy = new ConstantAttackerStrategy(0, 0);
        
        DSSimulation sim = new DSSimulation(p, trustedPeerStrategy, attackerPeerStrategy, attackerStrategy);
        sim.start();

    }
}
