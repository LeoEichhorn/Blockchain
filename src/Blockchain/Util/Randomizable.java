package Blockchain.Util;

public interface Randomizable<T> {
    public T next();
    public T getValue();
    public boolean isRandomized();
    public T[] getBounds();
}
