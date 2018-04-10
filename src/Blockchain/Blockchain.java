package Blockchain;

/**
 * Basic representation of a Blockchain, internally represented by its length.
 */
public class Blockchain implements Comparable<Blockchain>{
    protected int length;
    
    /**
     * Creates a new Blockchain of length zero.
     */
    public Blockchain() {
        this(0);
    }
    
    /**
     * Creates a new Blockchain.
     * @param length The length og the Blockchain
     */
    public Blockchain(int length) {
        this.length = length;
    }
    
    /**
     * @return A copy of this Blockchain
     */
    public Blockchain copy() {
        return new Blockchain(length);
    }
    
    /**
     * Called to add another Block to this Blockchain.
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
    
    @Override
    public String toString() {
        return String.format("[%d]",length);
    }
}
