package Blockchain;

/**
 * Basic representation of a Blockchain, internally represented by its length.
 */
public class Blockchain implements Comparable<Blockchain>{
    protected int length;
    protected final double difficulty;
    /**
     * Creates a new Blockchain of length zero.
     * @param difficulty The difficulty to add a Block to this Blockchain
     */
    public Blockchain(double difficulty) {
        this(0, difficulty);
    }
    
    /**
     * Creates a new Blockchain.
     * @param length The length of the new Blockchain
     * @param difficulty The difficulty to add a Block to this Blockchain
     */
    public Blockchain(int length, double difficulty) {
        this.length = length;
        this.difficulty = difficulty;
    }
    
    /**
     * @return A copy of this Blockchain
     */
    public Blockchain copy() {
        return new Blockchain(length, difficulty);
    }
    
    /**
     * Adds another block to this Blockchain.
     */
    public void addBlock() {
        length++;
    }
    
    /**
     * Resets this Blockchain to its initial state.
     * @param owner This Blockchain's owner
     */
    public void reset(Node owner) {
        length = 0;
    }
    
    @Override
    public int compareTo(Blockchain other) {
        return length - other.getLength();
    }
    
    /**
     * @return The length of this Blockchain
     */
    public int getLength() {
        return length;
    }
    
    /**
     * @return The difficulty of this Blockchain
     */
    public double getDifficulty() {
        return difficulty;
    }

    
    @Override
    public String toString() {
        return String.format("[%d]",length);
    }
}
