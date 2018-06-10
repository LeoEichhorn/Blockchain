package DoubleSpend;

import Blockchain.Blockchain;
import Blockchain.Network;
import Blockchain.Node;
import Blockchain.Util.Logger;
import java.util.logging.Level;

public class TrustedNode extends Node{
    private DSManager dsm;
    private Parameters p;
    
    public TrustedNode(DSManager dsm, Network network, Parameters p, String name) {
        super(network, new DSBlockchain(p.getDifficulty(), false), name);
        this.p = p;
        this.dsm = dsm;
    }
    
    @Override
    protected void onBlockMined(){
        //Check wether the new block was mined on an infested chain
        if(((DSBlockchain) blockchain).isDoubleSpending()) {
            dsm.registerAttackerChain(blockchain.getLength());
        } else {
            dsm.registerTrustedChain(blockchain.getLength());
        }
    }
    
    @Override
    protected boolean ignoreBlockchain(Blockchain newChain, Node sender) {
        //Infested blockchains are ignored until the legitimate blockchain has been confirmed
        return ((DSBlockchain) newChain).isDoubleSpending() 
            && ((DSBlockchain) blockchain).getLength() < p.getConfirmations();
    }

    @Override
    protected void onChoice(Blockchain oldChain, Blockchain newChain) {
        boolean infestedBefore = ((DSBlockchain) oldChain).isDoubleSpending();
        boolean infestedAfter = ((DSBlockchain) newChain).isDoubleSpending();
        if(infestedBefore && !infestedAfter){
            Logger.log(Level.FINEST, String.format("%s: No longer infested!",name));
            dsm.removeConvinced();
        }else if(!infestedBefore && infestedAfter){
            Logger.log(Level.FINEST, String.format("%s: Is now infested!",name));
            dsm.addConvinced();
        }
    }
}
