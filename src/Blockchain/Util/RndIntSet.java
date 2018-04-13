package Blockchain.Util;

import java.util.Arrays;
import java.util.Random;

/**
 * Implementation of a Multiset for Integers in a specific range.
 * Allows removal of random elements in constant time.
 */
public class RndIntSet {
    private final int maxSize;
    private final int[] data;
    private int size;
    
    public RndIntSet(int maxSize){
        this.maxSize = maxSize;
        this.data = new int[maxSize];
        this.size = 0;
    }
    
    public boolean add(int item){
        if (item > maxSize) {
            return false;
        }
        data[size++] = item;
        return true;
    }
    
    public int removeRandom(Random rnd){
        if (size == 0) {
            return -1;
        }
        int id = rnd.nextInt(size);
        int el = data[id];
        remove(id);
        return el;
    }
    
    private void remove(int id){
        int last = data[--size];
        if (id < size) {
            data[id] = last;
        }
    }
    
    @Override
    public String toString(){
        return Arrays.toString(data);
    }
}
