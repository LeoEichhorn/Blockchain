package Blockchain.Util;

public interface Randomizable<T> {

    /**
     * Generates and returns the next random value of this object.
     * @return The next random value.
     */
    public T next();
    /**
     * @return The current value.
     */
    public T getValue();
    
    /**
     * @return Wether this object is currently randomized
     */
    public boolean isRandomized();
    
    /**
     * @return An array of length two containing lower and upper bounds of this objects uniform distribution.
     */
    public T[] getBounds();
}
