package DoubleSpend;

import Blockchain.Peers.ConstantPeerStrategy;
import Blockchain.Peers.EuclideanPeerStrategy;
import Blockchain.Peers.PeerStrategy;
import Blockchain.Peers.RndGraphPeerStrategy;
import Blockchain.Util.Randomizable;
import Blockchain.Util.Util;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;

public class Parameters {
    //Number of trusted and attaking nodes
    private final int trustedNodes;
    private final int attackerNodes;
    private final int nodes;

    //Difficulty to mine one block [0,1]
    private final double difficulty;

    //Number of additional blocks needed to validate one block
    private final IntParameter confirmations;

    //Latency in milliseconds between two adjacent Nodes in trusted/attacking network
    private final IntParameter trustedLatency;
    private final IntParameter attackerLatency;
    
    //Latency in milliseconds between a trusted and an attacking Node
    private final IntParameter connectionLatency;
    
    //Graph density [2/n,1] which results to the number of edges in the trusted/attacking network
    private final DoubleParameter trustedGraphDensity;
    private final DoubleParameter attackerGraphDensity;
    
    //Number of Simulation runs
    private final int runs;

    //Minimum probability for attackers to catch up
    private final double epsilon;

    //Probability for attackers to reduce the gap by one block
    private final double probCatchUpByOne;

    //Maximum lead by trusted nodes for attackers to stay within epsilon
    private final int maxLead;
    //Maximum length of Blockchain before declaring attempt as failed
    private final int maxLength;
    
    //Controls amount of console output (INFO < FINE < FINER < FINEST)
    private final Level logLevel;
    
    //The strategies used to create trusted and attacking networks
    private final PeerStrategy tPeerStrat;
    private final PeerStrategy aPeerStrat;
    
    //The strategy used to connect the trusted network with the attacking network
    private final ConnectionStrategy connStrat;

    public Parameters(ParametersBuilder b) {
        this.trustedNodes = b.trustedNodes;
        this.attackerNodes = b.attackerNodes;
        this.difficulty = b.difficulty;
        this.confirmations = b.confirmations;
        this.trustedLatency = b.trustedLatency;
        this.attackerLatency = b.attackerLatency;
        this.connectionLatency = b.connectionLatency;
        this.trustedGraphDensity = b.trustedGraphDensity;
        this.attackerGraphDensity = b.attackerGraphDensity;
        this.runs = b.runs;
        this.epsilon = b.epsilon;
        this.logLevel = b.logLevel;
        this.tPeerStrat = b.tPeerStrat;
        this.aPeerStrat = b.aPeerStrat;
        this.connStrat = b.connStrat;
        
        nodes = trustedNodes + attackerNodes;
        
        probCatchUpByOne = ((double)attackerNodes) / trustedNodes;
        if (probCatchUpByOne >= 1) {
            maxLead = Integer.MAX_VALUE;
        } else {
            maxLead = (int) Math.ceil(Util.log(probCatchUpByOne, epsilon));
        }
        maxLength = (int) Math.ceil(1 / (epsilon * 100));
    }

    /**
     * @return The number of Nodes in the trusted network, mining on the longest chain they are aware of.
     */
    public int getTrustedNodes() {
        return trustedNodes;
    }
    
    /**
     * @return The number of Nodes in the attacking network, mining on a secret branch containing a double-spending transaction.
     */
    public int getAttackerNodes() {
        return attackerNodes;
    }

    /**
     * @return The difficulty target of mining a single block.
     */
    public double getDifficulty() {
        return difficulty;
    }

    /**
     * @return The number of confirmations required by the target merchant to consider the payment as valid.
     */
    public int getConfirmations() {
        return confirmations.getValue();
    }
        
    /**
     * @return The randomizable parameter corresponding to the number of confirmations required by the target merchant.
     */
    public IntParameter getConfirmationsIntParameter(){
        return confirmations;
    }
    
    /**
     * @return The mean latency in the trusted network.
     */
    public int getTrustedLatency() {
        return trustedLatency.getValue();
    }
        
    /**
     * @return The randomizable parameter corresponding to the mean latency in the trusted network.
     */
    public IntParameter getTrustedLatencyParameter() {
        return trustedLatency;
    }
        
    /**
     * @return The mean latency in the attacking network.
     */
    public int getAttackerLatency() {
        return attackerLatency.getValue();
    }
            
    /**
     * @return The randomizable parameter corresponding to the mean latency in the attacking network.
     */
    public IntParameter getAttackerLatencyParameter() {
        return attackerLatency;
    }
        
    /**
     * @return The mean latency between the trusted and the attacking network.
     */
    public int getConnectionLatency() {
        return connectionLatency.getValue();
    }
            
    /**
     * @return The randomizable parameter corresponding to the mean latency between the trusted and the attacking network.
     */
    public IntParameter getConnectionLatencyParameter() {
        return connectionLatency;
    }
                
    /**
     * @return The graph density of the trusted network.
     */
    public double getTrustedGraphDensity() {
        return trustedGraphDensity.getValue();
    }
                
    /**
     * @return The randomizable parameter corresponding to the graph density of the trusted network.
     */
    public DoubleParameter getTrustedGraphDensityParameter() {
        return trustedGraphDensity;
    }
                    
    /**
     * @return The graph density of the attacking network.
     */
    public double getAttackerGraphDensity() {
        return attackerGraphDensity.getValue();
    }
                    
    /**
     * @return The randomizable parameter corresponding to the graph density of the attacking network.
     */
    public DoubleParameter getAttackerGraphDensityParameter() {
        return attackerGraphDensity;
    }
                        
    /**
     * @return The number of double-spend attempts during the simulation.
     */
    public int getRuns() {
        return runs;
    }
                    
    /**
     * @return The fault-tolerance defining the maximum trusted lead and blockchain length resulting in a failed double-spend attempt.
     */
    public double getEpsilon() {
        return epsilon;
    }
                    
    /**
     * @return The maximum lead by the trusted nodes' blockchain over the attackers, before the attempt is considered to be unsuccessful
     */
    public int getMaxLead() {
        return maxLead;
    }
                    
    /**
     * @return The maximum length leading to a failed attempt when reached by any blockchain.
     */
    public int getMaxLength() {
        return maxLength;
    }
                    
    /**
     * @return The total number of nodes in both networks.
     */ 
    public int getNodes() {
        return nodes;
    }
                    
    /**
     * @return The current logging level defining the amount of console output.
     */
    public Level getLogLevel() {
        return logLevel;
    }
                    
    /**
     * @return The PeerStrategy used by the trusted network.
     */
    public PeerStrategy getTrustedPeerStrategy() {
        return tPeerStrat;
    }
                        
    /**
     * @return The PeerStrategy used by the attacking network.
     */
    public PeerStrategy getAttackerPeerStrategy() {
        return aPeerStrat;
    }
                    
    /**
     * @return The ConnectionStrategy used by the attacking network to connect to the trusted network.
     */
    public ConnectionStrategy getConnectionStrategy() {
        return connStrat;
    }
    
    @Override
    public String toString() {
        String conf;
        if(confirmations.isRandomized()){
            Integer[] b = confirmations.getBounds();
            conf = "random["+b[0]+";"+b[1]+"]";
        }else{
            conf = ""+confirmations.getValue();
        }
        return String.format("Trusted nodes: %d, Attacking nodes: %d\n"
                + "Difficulty: %s, Confirmation length: %s\n"
                + "Trusted Strategy: %s\n"
                + "Attacker Strategy: %s\n"
                + "Connection Strategy: %s\n"
                + "Runs: %d, Epsilon: %s"
                , trustedNodes, attackerNodes, ""+difficulty, conf
                , tPeerStrat, aPeerStrat, connStrat, runs, ""+epsilon);
    }
    
    public static class IntParameter implements Randomizable<Integer> {
        private int value;
        private int upper;
        private int lower;
        private Random rng;
        private boolean randomized;
        
        /**
         * Creates a new randomizable integer parameter
         * @param value The initial value of this parameter
         */
        public IntParameter(int value){
            this.value = value;
            randomized = false;
        }
        
        private void setValue(int value){
            randomized = false;
            this.value = value;
        }
        
        @Override
        public Integer getValue() {
            return value;
        }
        
        @Override
        public Integer[] getBounds(){
            return new Integer[]{lower, upper};
        }
        
        @Override
        public boolean isRandomized(){
            return randomized;
        }
        
        private void randomize(int lower, int upper){
            if(lower < 0 || lower > upper)
                throw new IllegalArgumentException("Negative or invalid randomization bounds");
            randomized = true;
            this.rng = new Random();
            this.upper = upper;
            this.lower = lower;
        }
        
        @Override
        public Integer next(){
            if(!randomized || upper <= lower)
                return value;
            return value = rng.nextInt((upper - lower) + 1) + lower;
        }  
    }
    
    public static class DoubleParameter implements Randomizable<Double> {
        private double value;
        private double upper;
        private double lower;
        private Random rng;
        private boolean randomized;
        
        /**
         * Creates a new randomizable double Parameter
         * @param value The initial value of this Parameter
         */
        public DoubleParameter(double value){
            this.value = value;
            randomized = false;
        }
        
        private void setValue(double value){
            randomized = false;
            this.value = value;
        }
        
        @Override
        public Double getValue() {
            return value;
        }
        
        @Override
        public Double[] getBounds(){
            return new Double[]{lower, upper};
        }
        
        @Override
        public boolean isRandomized(){
            return randomized;
        }
        
        private void randomize(double lower, double upper){
            if(lower < 0 || lower > upper)
                throw new IllegalArgumentException("Negative or invalid randomization bounds");
            randomized = true;
            this.rng = new Random();
            this.upper = upper;
            this.lower = lower;
        }
        
        @Override
        public Double next(){
            if(!randomized || upper <= lower)
                return value;
            return value = lower + (upper - lower) * rng.nextDouble();
        }  
    }
    
    public enum PeerStrategyEnum {
        CONSTANT, EUCLIDEAN, RANDOM
    }
    
    public enum ConnectionStrategyEnum {
        CONSTANT
    }

    public static class ParametersBuilder {

        private final Properties p;
        private int trustedNodes;
        private int attackerNodes;
        private double difficulty;
        private IntParameter confirmations;
        private IntParameter trustedLatency;
        private IntParameter attackerLatency;
        private IntParameter connectionLatency;
        private DoubleParameter trustedGraphDensity;
        private DoubleParameter attackerGraphDensity;
        private int runs;
        private double epsilon;
        private Level logLevel;
        private PeerStrategy tPeerStrat;
        private PeerStrategy aPeerStrat;
        private ConnectionStrategy connStrat;
        
        private PeerStrategyEnum tps,aps;
        private ConnectionStrategyEnum cs;

        public ParametersBuilder() {
            this.p = new Properties();
            this.trustedNodes  = 32;
            this.attackerNodes = 16;
            this.difficulty    = 0.00001;
            this.confirmations = new IntParameter(6);
            this.trustedLatency = new IntParameter(10);
            this.attackerLatency = new IntParameter(10);
            this.connectionLatency = new IntParameter(10);
            this.trustedGraphDensity = new DoubleParameter(0.8);
            this.attackerGraphDensity = new DoubleParameter(0.8);
            this.runs          = 100;
            this.epsilon       = 0.00001;
            this.logLevel      = Level.FINE;
            this.tps = PeerStrategyEnum.RANDOM;
            this.aps = PeerStrategyEnum.RANDOM;
            this.cs = ConnectionStrategyEnum.CONSTANT;
        }

        /**
         * @param trustedNodes The number of Nodes in the trusted network, mining on the longest chain they are aware of. Default value: 32
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setTrustedNodes(int trustedNodes) {
            if(trustedNodes <= 0)
                throw new IllegalArgumentException("Non positive number of trusted Nodes");
            this.trustedNodes = trustedNodes;
            return this;
        }

        /**
         * @param attackerNodes The number of Nodes in the attacking network, mining on a secret branch containing a double-spending transaction. Default value: 16
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setAttackerNodes(int attackerNodes) {
            if(attackerNodes <= 0)
                throw new IllegalArgumentException("Non positive number of attacker Nodes");
            this.attackerNodes = attackerNodes;
            return this;
        }

        /**
         * @param difficulty The difficulty target of mining a single block. Default value: 0.00001
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setDifficulty(double difficulty) {
            if(difficulty <= 0 || difficulty >= 1)
                throw new IllegalArgumentException("Difficulty not in (0, 1)");
            this.difficulty = difficulty;
            return this;
        }

        /**
         * Sets the Parameter to the specified value. The parameter is no longer randomized. 
         * @param confirmations The number of confirmations required by the target merchant to consider the payment as valid. Default value: 6
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setConfirmations(int confirmations) {
            if(confirmations < 0)
                throw new IllegalArgumentException("Negative confirmation length");
            this.confirmations.setValue(confirmations);
            return this;
        }
        
        /**
         * Randomizes this parameter by setting its lower and upper bounds (inclusive).
         * @param lower The lower bound
         * @param upper The upper bound
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder randomizeConfirmations(int lower, int upper) {
            this.confirmations.randomize(lower, upper);
            return this;
        }

        /**
         * Sets the Parameter to the specified value. The parameter is no longer randomized.
         * @param trustedLatency The mean latency in the trusted network. Default value: 10
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setTrustedLatency(int trustedLatency) {
            if(trustedLatency < 0)
                throw new IllegalArgumentException("Negative trusted latency");
            this.trustedLatency.setValue(trustedLatency);
            return this;
        }
        
        /**
         * Randomizes this parameter by setting its lower and upper bounds (inclusive).
         * @param lower The lower bound
         * @param upper The upper bound        
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder randomizeTrustedLatency(int lower, int upper) {
            this.trustedLatency.randomize(lower, upper);
            return this;
        }

        /**
         * Sets the Parameter to the specified value. The parameter is no longer randomized.
         * @param attackerLatency The mean latency in the attacking network. Default value: 10
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setAttackerLatency(int attackerLatency) {
            if(attackerLatency < 0)
                throw new IllegalArgumentException("Negative attacker latency");
            this.attackerLatency.setValue(attackerLatency);
            return this;
        }
        
        /**
         * Randomizes this parameter by setting its lower and upper bounds (inclusive).
         * @param lower The lower bound
         * @param upper The upper bound        
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder randomizeAttackerLatency(int lower, int upper) {
            this.attackerLatency.randomize(lower, upper);
            return this;
        }

        /**
         * Sets the Parameter to the specified value. The parameter is no longer randomized.
         * @param connectionLatency he mean latency between the trusted and the attacking network. Default value: 10
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setConnectionLatency(int connectionLatency) {
            if(connectionLatency < 0)
                throw new IllegalArgumentException("Negative connection latency");
            this.connectionLatency.setValue(connectionLatency);
            return this;
        }
        
        /**
         * Randomizes this parameter by setting its lower and upper bounds (inclusive).
         * @param lower The lower bound
         * @param upper The upper bound        
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder randomizeConnectionLatency(int lower, int upper) {
           this.connectionLatency.randomize(lower, upper);
            return this;
        }

        /**
         * Sets the Parameter to the specified value. The parameter is no longer randomized.
         * @param trustedGraphDensity The graph density of the trusted network. Default value: 0.8
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setTrustedGraphDensity(double trustedGraphDensity) {
            if(trustedGraphDensity < 0)
                throw new IllegalArgumentException("Negative trusted graph density");
            this.trustedGraphDensity.setValue(trustedGraphDensity);
            return this;
        }
        
        /**
         * Randomizes this parameter by setting its lower and upper bounds (inclusive).
         * @param lower The lower bound
         * @param upper The upper bound        
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder randomizeTrustedGraphDensity(double lower, double upper) {
            this.trustedGraphDensity.randomize(lower, upper);
            return this;
        }

        /**
         * Sets the Parameter to the specified value. The parameter is no longer randomized.
         * @param attackerGraphDensity The graph density of the attacking network. Default value: 0.8
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setAttackerGraphDensity(double attackerGraphDensity) {
            if(attackerGraphDensity < 0)
                throw new IllegalArgumentException("Negative attacker graph density");
            this.attackerGraphDensity.setValue(attackerGraphDensity);
            return this;
        }
        
        /**
         * Randomizes this parameter by setting its lower and upper bounds (inclusive).
         * @param lower The lower bound
         * @param upper The upper bound        
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder randomizeAttackerGraphDensity(double lower, double upper) {
            this.attackerGraphDensity.randomize(lower, upper);
            return this;
        }
        
        /**
         * @param runs The number of double-spend attempts during the simulation. Default value: 100
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setRuns(int runs) {
            if(runs <= 0)
                throw new IllegalArgumentException("Non positive number of runs");
            this.runs = runs;
            return this;
        }

        /**
         * @param epsilon The fault-tolerance defining the maximum trusted lead and blockchain length resulting in a failed double-spend attempt. Default value: 0.00001
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setEpsilon(double epsilon) {
            if(epsilon <= 0 || epsilon >= 1)
                throw new IllegalArgumentException("Epsilon not in (0, 1)");
            this.epsilon = epsilon;
            return this;
        }

        /**
         * Sets the logging level defining the amount of console output.
         * INFO < FINE < FINER < FINEST
         * @param logLevel The logging level. Default value: FINE
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setLogLevel(Level logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        /**
         * Sets the PeerStrategy used by the trusted network.
         * @param tPeerStrat The PeerStrategy defined by its Enum value. Default value: RANDOM
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setTrustedPeerStrategy(PeerStrategyEnum tPeerStrat) {
            this.tps = tPeerStrat;
            return this;
        }

        /**
         * Sets the PeerStrategy used by the attacking network.
         * @param aPeerStrat The PeerStrategy defined by its Enum value. Default value: RANDOM
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setAttackerPeerStrategy(PeerStrategyEnum aPeerStrat) {
            this.aps = aPeerStrat;
            return this;
        }

        /**
         * Sets the ConnectionStrategy used by the attacking network network to connect to the trusted network.
         * @param connStrat The ConnectionStrategy defined by its Enum value. Default value: CONSTANT
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setConnectionStrategy(ConnectionStrategyEnum connStrat) {
            this.cs = connStrat;
            return this;
        }
        
        /**
         * Sets the PeerStrategy used by the trusted network.
         * @param tPeerStrat The concrete PeerStrategy instance to be used. Default value: RANDOM
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setTrustedPeerStrategy(PeerStrategy tPeerStrat) {
            this.tps = null;
            this.tPeerStrat = tPeerStrat;
            return this;
        }

        /**
         * Sets the PeerStrategy used by the attacking network.
         * @param aPeerStrat The concrete PeerStrategy instance to be used. Default value: RANDOM
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setAttackerPeerStrategy(PeerStrategy aPeerStrat) {
            this.aps = null;
            this.aPeerStrat = aPeerStrat;
            return this;
        }

        /**
         * Sets the ConnectionStrategy used by the attacking network network to connect to the trusted network.
         * @param connStrat The concrete ConnectionStrategy instance to be used. Default value: CONSTANT
         * @return The ParametersBuilder instance.
         */
        public ParametersBuilder setConnectionStrategy(ConnectionStrategy connStrat) {
            this.cs = null;
            this.connStrat = connStrat;
            return this;
        }

        /**
         * Instantiates Peer and Connection strategies as defined by the ParametersBuilder.
         * @return the Parameters instance.
         */
        public Parameters build() {
            if(tps != null)
                tPeerStrat = buildPeerStrat(tps);
            if(aps != null)
                aPeerStrat = buildPeerStrat(aps);
            if(cs != null)
                connStrat = buildConnStrat();
            return new Parameters(this);
        }
        
        /**
         * Initializes this ParametersBuilder instance as specified by the property file.
         * @param fileName The path and filename of the property file.
         * @return The ParametersBuilder instance.
         * @throws java.io.IOException If the specified property file can't be loaded.
         */
        public ParametersBuilder loadFromProperty(String fileName) throws IOException {
            p.load(new FileInputStream(fileName));  
            return setTrustedNodes(getInteger("NUM_TRUSTED", trustedNodes)).
                    setAttackerNodes(getInteger("NUM_ATTACKER", attackerNodes)).
                    setDifficulty(getDouble("DIFFICULTY", difficulty)).
                    setConfirmations(getInteger("CONFIRMATIONS", confirmations.getValue())).
                    setTrustedLatency(getInteger("LAT_TRUSTED", trustedLatency.getValue())).
                    setAttackerLatency(getInteger("LAT_ATTACKER", attackerLatency.getValue())).
                    setConnectionLatency(getInteger("LAT_CONNECTION", connectionLatency.getValue())).
                    setTrustedGraphDensity(getDouble("DENS_TRUSTED", trustedGraphDensity.getValue())).
                    setAttackerGraphDensity(getDouble("DENS_ATTACKER", attackerGraphDensity.getValue())).
                    setIntegerBounds(confirmations, getIntegerBounds("CONFIRMATION_BOUNDS")).
                    setIntegerBounds(trustedLatency, getIntegerBounds("LAT_TRUSTED_BOUNDS")).
                    setIntegerBounds(attackerLatency, getIntegerBounds("LAT_ATTACKER_BOUNDS")).
                    setIntegerBounds(connectionLatency, getIntegerBounds("LAT_CONNECTION_BOUNDS")).
                    setDoubleBounds(trustedGraphDensity, getDoubleBounds("DENS_TRUSTED_BOUNDS")).
                    setDoubleBounds(attackerGraphDensity, getDoubleBounds("DENS_ATTACKER_BOUNDS")).
                    setRuns(getInteger("RUNS", runs)).
                    setEpsilon(getDouble("EPSILON", epsilon)).
                    setLogLevel(getLevel("LOGGING", logLevel)).
                    setTrustedPeerStrategy(getPeerStrat("PS_TRUSTED", tps)). 
                    setAttackerPeerStrategy(getPeerStrat("PS_ATTACKER", aps)).
                    setConnectionStrategy(getConnStrat("CONN_STRAT", cs));
        }
        
        private PeerStrategyEnum getPeerStrat(String key, PeerStrategyEnum defaultValue) {
            String value = p.getProperty(key);
            if (value == null) {
                return defaultValue;
            }
            switch(value.toUpperCase()) {
                case "CONSTANT":
                    return PeerStrategyEnum.CONSTANT;
                case "EUCLIDEAN":
                    return PeerStrategyEnum.EUCLIDEAN;
                case "RANDOM":
                    return PeerStrategyEnum.RANDOM;
                default:
                    return defaultValue;
            }
        }
        
        private ConnectionStrategyEnum getConnStrat(String key, ConnectionStrategyEnum defaultValue) {
            String value = p.getProperty(key);
            if (value == null) {
                return defaultValue;
            }
            switch(value.toUpperCase()) {
                case "CONSTANT":
                    return ConnectionStrategyEnum.CONSTANT;
                default:
                    return defaultValue;
            }
        }
        
        private double getDouble(String key, double defaultValue) {
            String value = p.getProperty(key);
            if (value == null) {
                return defaultValue;
            }
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        
        private double[] getDoubleBounds(String key) {
            String value = p.getProperty(key);
            if (value == null)
                return null;
            String[] ul = value.split("\\s*,\\s*");
            if(ul == null || ul.length != 2)
                return null;
            try {
                return new double[]{Double.parseDouble(ul[0]), Double.parseDouble(ul[1])};
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        private ParametersBuilder setDoubleBounds(DoubleParameter p, double[] bounds){
            if(bounds != null){
                p.randomize(bounds[0], bounds[1]);
            }
            return this;
        }

        private int getInteger(String key, int defaultValue) {
            String value = p.getProperty(key);
            if (value == null) {
                return defaultValue;
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        
        private int[] getIntegerBounds(String key) {
            String value = p.getProperty(key);
            if (value == null)
                return null;
            String[] ul = value.split("\\s*,\\s*");
            if(ul == null || ul.length != 2)
                return null;
            try {
                return new int[]{Integer.parseInt(ul[0]), Integer.parseInt(ul[1])};
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        private ParametersBuilder setIntegerBounds(IntParameter p, int[] bounds){
            if(bounds != null){
                p.randomize(bounds[0], bounds[1]);
            }
            return this;
        }
        
        private Level getLevel(String key, Level defaultValue) {
            String value = p.getProperty(key);
            if (value == null) {
                return defaultValue;
            }
            switch(value.toUpperCase()) {
                case "INFO":
                    return Level.INFO;
                case "FINE":
                    return Level.FINE;
                case "FINER":
                    return Level.FINER;
                case "FINEST":
                    return Level.FINEST;
                default:
                    return defaultValue;
            }
        }
        
        private PeerStrategy buildPeerStrat(PeerStrategyEnum ps){
            switch(ps){
                case CONSTANT:
                    return new ConstantPeerStrategy(attackerLatency);
                case EUCLIDEAN:
                    return new EuclideanPeerStrategy(attackerLatency);
                case RANDOM:
                    return new RndGraphPeerStrategy(attackerGraphDensity, attackerLatency);
                default:
                    return null;
            }
        }
        
        private ConnectionStrategy buildConnStrat(){
            switch(cs){
                case CONSTANT:
                    return new ConstantConnectionStrategy(connectionLatency);
                default:
                    return null;
            }
        }
    }
}
