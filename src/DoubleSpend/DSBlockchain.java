package DoubleSpend;

import Blockchain.Blockchain;
import Blockchain.Node;

/**
 * Implementation of a Blockchain used to simulate Double Spend attacks.
 */
public class DSBlockchain extends Blockchain{
    private boolean infested;
    
    public DSBlockchain(double difficulty, boolean infested) {
        this(0, difficulty, infested);
    }
    
    public DSBlockchain(int length, double difficulty, boolean infested) {
        super(length, difficulty);
        this.infested = infested;
    }
    
    @Override
    public DSBlockchain copy() {
        return new DSBlockchain(length, difficulty, infested);
    }
    
    @Override
    public void reset(Node owner) {
        super.reset(owner);
        infested = owner instanceof AttackerNode;
    }
    
    /**
     * A Blockchain is infested if it contains a malicious transaction indroduced
     * by an Attacker to achieve a Double Spend.
     * @return Wether this Blockchain is infested.
     */
    public boolean isInfested() {
        return infested;
    }
    
    @Override
    public String toString() {
        return String.format("[%d; %s]", length, infested?"infested":"safe");
    }
}
