package Blockchain;

public abstract class Blockchain implements Comparable<Blockchain>{
    
    protected final double difficulty;
    /**
     * Creates a new Blockchain with the given mining difficulty
     * @param difficulty The difficulty to add a Block to this Blockchain
     */
    public Blockchain(double difficulty) {
        this.difficulty = difficulty;
    }
    
    /**
     * @return A copy of this Blockchain
     */
    public abstract Blockchain copy();
    
    /**
     * Adds another block to this Blockchain.
     */
    public abstract void addBlock();
    
    /**
     * Resets this Blockchain to its initial state.
     * @param owner This Blockchain's owner
     */
    public abstract void reset(Node owner);
    
    /**
     * @return The length of this Blockchain
     */
    public abstract int getLength();
    
    /**
     * @return The difficulty of this Blockchain
     */
    public double getDifficulty() {
        return difficulty;
    }
    
    @Override
    public int compareTo(Blockchain other) {
        return getLength() - other.getLength();
    }
}
