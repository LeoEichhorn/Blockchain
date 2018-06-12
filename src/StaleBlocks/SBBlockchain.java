package StaleBlocks;

import Blockchain.*;

public class SBBlockchain extends Blockchain{
    protected int length;

    public SBBlockchain(double difficulty) {
        this(0, difficulty);
    }
    
    public SBBlockchain(int length, double difficulty) {
        super(difficulty);
        this.length = length;
    }
    
    @Override
    public SBBlockchain copy() {
        return new SBBlockchain(length, difficulty);
    }

    @Override
    public void addBlock() {
        length++;
    }
    
    @Override
    public void reset(Node owner) {
        length = 0;
    }

    @Override
    public int getLength() {
        return length;
    }
    
    @Override
    public double getDifficulty() {
        return difficulty;
    }

    @Override
    public String toString() {
        return String.format("[%d]",length);
    }
}
