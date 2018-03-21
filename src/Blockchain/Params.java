package Blockchain;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Params {
    //Properties to load parameters
    private static final Properties p = new Properties();
    
    //Number of trusted and attaking nodes (trusted > attacking)
    public static int NUM_TRUSTED;
    public static int NUM_ATTACKER;
    
    //Difficulty to mine one block [0,1]
    public static double DIFFICULTY;
    
    //Number of additional blocks needed to validate one block
    public static int CONFIRMATIONS;
    
    //Number of double spend attemps
    public static int RUNS;
    
    //Minimum probability for attackers to catch up
    public static double EPSILON;
    
    
    //Probability for attackers to reduce the gap by one block
    public static double PROB_CATCHUP;
    
    //Maximum lead by trusted nodes for attackers to stay within epsilon
    public static int MAX_LEAD;
    
    private static double log(double base, double val){
        return Math.log(val)/Math.log(base);
    }
    
    public static void loadParameters(String filename) throws IOException{
        p.load(new FileInputStream(filename));
        NUM_TRUSTED   = getInteger("NUM_TRUSTED", 10);
        NUM_ATTACKER  = getInteger("NUM_ATTACKER", 3);
        DIFFICULTY    = getDouble("DIFFICULTY", 0.0000001);
        CONFIRMATIONS = getInteger("CONFIRMATIONS", 3);
        RUNS          = getInteger("RUNS", 100);
        EPSILON       = getDouble("EPSILON", 0.0000001);
        
        PROB_CATCHUP  = ((double)NUM_ATTACKER)/NUM_TRUSTED;
        MAX_LEAD      = (int) Math.ceil(log(PROB_CATCHUP, EPSILON));
    }
    
    private static double getDouble(String key, double defaultValue){
        String value = p.getProperty(key);
        if(value == null) return defaultValue;
        try{
            return Double.parseDouble(value);
        }catch(NumberFormatException e){
            return defaultValue;
        }
    }
    
    private static int getInteger(String key, int defaultValue){
        String value = p.getProperty(key);
        if(value == null) return defaultValue;
        try{
            return Integer.parseInt(value);
        }catch(NumberFormatException e){
            return defaultValue;
        }
    }
}
