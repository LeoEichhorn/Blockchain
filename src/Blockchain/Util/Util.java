package Blockchain.Util;

import java.util.Random;

public class Util {
    public static double nextGaussian(Random rnd, double mean, double stdDev){
        return Math.max(0, rnd.nextGaussian()*stdDev+mean);
    }
    
    public static double log(double base, double val) {
        return Math.log(val) / Math.log(base);
    }
}
