package DoubleSpend;

import Blockchain.Blockchain;
import Blockchain.Network;
import Blockchain.Node;
import Blockchain.Parameters;
import java.util.logging.Level;

public class TrustedNode extends Node{
    private DSManager dsm;
    
    public TrustedNode(DSManager dsm, Network network, Parameters p, String name) {
        super(network, p, new DSBlockchain(false), name);
        this.dsm = dsm;
    }
    
    @Override
    protected void onBlockMined(){
        if(((DSBlockchain) blockchain).isInfested()) {
            dsm.registerAttackerChain(blockchain.getLength());
        } else {
            dsm.registerTrustedChain(blockchain.getLength());
        }
    }
    
    @Override
    protected boolean ignoreBlockchain(Blockchain newChain, Node sender) {
        return ((DSBlockchain) newChain).isInfested() 
            && ((DSBlockchain) blockchain).getLength() < p.getConfirmations();
    }

    @Override
    protected void onChoice(Blockchain oldChain, Blockchain newChain) {
        boolean infestedBefore = ((DSBlockchain) oldChain).isInfested();
        boolean infestedAfter = ((DSBlockchain) newChain).isInfested();
        if(infestedBefore && !infestedAfter){
            if(p.getLogLevel().intValue() <= Level.FINEST.intValue())
                System.out.println(name+": No longer infested!");
            dsm.removeInfested();
        }else if(!infestedBefore && infestedAfter){
            if(p.getLogLevel().intValue() <= Level.FINEST.intValue())
                System.out.println(name+": Is now infested!");
            dsm.addInfested();
        }
    }
}
