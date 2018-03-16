package Blockchain;

public class Params {
    //Number of trusted and attaking nodes (trusted > attacking)
    public static final int NUM_TRUSTED     = 10;
    public static final int NUM_ATTACKER    = 3;
    
    //Difficulty to mine one block [0,1]
    public static final double DIFFICULTY   = 0.0000001;
    
    //Number of additional blocks needed to validate one block
    public static final int CONFIRMATIONS   = 3;
    
    //Number of double spend attemps
    public static final int RUNS            = 100;
    
    //Minimum probability for attackers to catch up
    public static final double EPSILON      = 0.0000001;
    
    
    //Probability for attackers to reduce the gap by one block
    public static final double PROB_CATCHUP = ((double)NUM_ATTACKER)/NUM_TRUSTED;
    
    //Maximum lead by trusted nodes for attackers to stay within epsilon
    public static final int MAX_LEAD        = (int) Math.ceil(log(PROB_CATCHUP, EPSILON));
    
    private static double log(double base, double val){
        return Math.log(val)/Math.log(base);
    }
}
