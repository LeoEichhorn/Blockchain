package DoubleSpend;

import Blockchain.Blockchain;
import Blockchain.Node;

/**
 * Implementation of a Blockchain used to simulate Double Spend attacks.
 */
public class DSBlockchain extends Blockchain{
    private int length;
    private boolean doubleSpending;
    
    public DSBlockchain(double difficulty, boolean doubleSpending) {
        this(0, difficulty, doubleSpending);
    }
    
    public DSBlockchain(int length, double difficulty, boolean doubleSpending) {
        super(difficulty);
        this.length = length;
        this.doubleSpending = doubleSpending;
    }
    
    @Override
    public DSBlockchain copy() {
        return new DSBlockchain(length, difficulty, doubleSpending);
    }
    
    @Override
    public void reset(Node owner) {
        length = 0;
        doubleSpending = owner instanceof AttackerNode;
    }
    
    @Override
    public void addBlock()  {
        length++;
    }
    
    @Override
    public int getLength() {
        return length;
    }
    
    /**
     * A Blockchain is double-spending if it contains a malicious transaction indroduced
     * by an Attacker to himself.
     * @return Wether this Blockchain is infested.
     */
    public boolean isDoubleSpending() {
        return doubleSpending;
    }
    
    @Override
    public String toString() {
        return String.format("[%d; %s]", length, doubleSpending?"double-spending":"safe");
    } 
}
