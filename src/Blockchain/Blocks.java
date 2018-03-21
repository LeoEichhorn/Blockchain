package Blockchain;

public class Blocks {
    //length of trusted chain
    private int trusted;
    //length of attacker fork
    private int attacker;
    //number of (un-)successful double spends
    private int success;
    private int failure;
    //ignore the first couple measures to avoid scheduling bias
    private int ignore;
    
    public Blocks(){
        this.attacker = 0;
        this.trusted  = 0;
        this.success  = 0;
        this.failure  = 0;
        this.ignore   = 2;
    }
    
    /*
    * Adding to a chain consists of incrementing the length of the
    * corresponding chain and checking for a successful double spend.
    * Check for stopped() is needed since there might still be nodes 
    * mining at the time all runs have been completed.
    */
    public synchronized void addToTrustedChain(){
        if(stopped())
            return;        
        trusted++;
        checkSuccess();
    }
    public synchronized void addToAttackerChain(){
        if(stopped())
            return;
        attacker++;
        checkSuccess();
    }
    
    public synchronized boolean stopped(){
        return success + failure >= Params.RUNS;
    }
    
    /*
    * Checks if double spend is possible or if the current 
    * attempt should be aborted
    */
    private void checkSuccess(){
        //Double spend happens if the first block is confirmed 
        //and the attacking chain is longer than the trusted chain
        if(trusted >= Params.CONFIRMATIONS && attacker > trusted){
            if(ignore <= 0){
                System.out.println((success+failure)+": SUCCESS t:"+trusted+" a:"+attacker);
                success++;
            }else{
                System.out.println("skipping");
                ignore--;
            }
            attacker = trusted = 0;
            
        //current attempt will be aborted if attackers are falling too far behind
        }else if(Params.MAX_LEAD < trusted - attacker){  
            if(ignore <= 0){
                System.out.println((success+failure)+": FAILURE t:"+trusted+" a:"+attacker);
                failure++;
            }else{
                System.out.println("skipping");
                ignore--;
            }
            attacker = trusted = 0;
        }
    }

    public int getSuccess() {
        return success;
    }

    public int getFailure() {
        return failure;
    }
}
