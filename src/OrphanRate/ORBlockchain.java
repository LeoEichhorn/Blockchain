package OrphanRate;

import Blockchain.*;

public class ORBlockchain extends Blockchain{
    protected int length;

    public ORBlockchain(double difficulty) {
        this(0, difficulty);
    }
    
    public ORBlockchain(int length, double difficulty) {
        super(difficulty);
        this.length = length;
    }
    
    @Override
    public ORBlockchain copy() {
        return new ORBlockchain(length, difficulty);
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
