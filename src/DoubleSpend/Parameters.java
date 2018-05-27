package DoubleSpend;

import Blockchain.Peers.AdjMatrixPeerStrategy;
import Blockchain.Peers.ConstantPeerStrategy;
import Blockchain.Peers.EuclideanPeerStrategy;
import Blockchain.Peers.PeerStrategy;
import Blockchain.Peers.RndGraphPeerStrategy;
import Blockchain.Util.Parameter;
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
     * Generates the next set of randomized Parameters
     */
    public void next() {
        confirmations.next();
        trustedLatency.next();
        attackerLatency.next();
        connectionLatency.next();
        trustedGraphDensity.next();
        attackerGraphDensity.next();
    }

    public int getTrustedNodes() {
        return trustedNodes;
    }

    public int getAttackerNodes() {
        return attackerNodes;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public int getConfirmations() {
        return confirmations.getValue();
    }

    public int getTrustedLatency() {
        return trustedLatency.getValue();
    }
    
    public IntParameter getTrustedLatencyParameter() {
        return trustedLatency;
    }

    public int getAttackerLatency() {
        return attackerLatency.getValue();
    }
    
    public IntParameter getAttackerLatencyParameter() {
        return attackerLatency;
    }

    public int getConnectionLatency() {
        return connectionLatency.getValue();
    }
    
    public IntParameter getConnectionLatencyParameter() {
        return connectionLatency;
    }
    
    public double getTrustedGraphDensity() {
        return trustedGraphDensity.getValue();
    }
    
    public DoubleParameter getTrustedGraphDensityParameter() {
        return trustedGraphDensity;
    }
    
    public double getAttackerGraphDensity() {
        return attackerGraphDensity.getValue();
    }
    
    public DoubleParameter getAttackerGraphDensityParameter() {
        return attackerGraphDensity;
    }
    
    public int getRuns() {
        return runs;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public double getProbCatchUpByOne() {
        return probCatchUpByOne;
    }

    public int getMaxLead() {
        return maxLead;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getNodes() {
        return nodes;
    }
    
    public Level getLogLevel() {
        return logLevel;
    }

    public PeerStrategy getTrustedPeerStrategy() {
        return tPeerStrat;
    }
    
    public PeerStrategy getAttackerPeerStrategy() {
        return aPeerStrat;
    }

    public ConnectionStrategy getConnectionStrategy() {
        return connStrat;
    }
    
    public static class IntParameter implements Parameter<Integer> {
        private int value;
        private int upper;
        private int lower;
        private Random rng;
        private boolean randomized;
        
        /**
         * Creates a new randomizable integer Parameter
         * @param value The initial value of this Parameter
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
        
        private void randomize(int lower, int upper){
            randomized = true;
            this.rng = new Random();
            this.upper = upper;
            this.lower = lower;
        }
        
        public int next(){
            if(!randomized || upper < lower)
                return value;
            return value = rng.nextInt((upper - lower) + 1) + lower;
        }  
    }
    
    public static class DoubleParameter implements Parameter<Double> {
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
        
        private void randomize(double lower, double upper){
            randomized = true;
            this.rng = new Random();
            this.upper = upper;
            this.lower = lower;
        }
        
        public double next(){
            if(!randomized || upper < lower)
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

        public ParametersBuilder setTrustedNodes(int trustedNodes) {
            this.trustedNodes = trustedNodes;
            return this;
        }

        public ParametersBuilder setAttackerNodes(int attackerNodes) {
            this.attackerNodes = attackerNodes;
            return this;
        }

        public ParametersBuilder setDifficulty(double difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public ParametersBuilder setConfirmations(int confirmations) {
            this.confirmations.setValue(confirmations);
            return this;
        }
        
        public ParametersBuilder randomizeConfirmations(int lower, int upper) {
            this.confirmations.randomize(lower, upper);
            return this;
        }

        public ParametersBuilder setTrustedLatency(int trustedLatency) {
            this.trustedLatency.setValue(trustedLatency);
            return this;
        }
        
        public ParametersBuilder randomizeTrustedLatency(int lower, int upper) {
            this.trustedLatency.randomize(lower, upper);
            return this;
        }

        public ParametersBuilder setAttackerLatency(int attackerLatency) {
            this.attackerLatency.setValue(attackerLatency);
            return this;
        }
        
        public ParametersBuilder randomizeAttackerLatency(int lower, int upper) {
            this.attackerLatency.randomize(lower, upper);
            return this;
        }

        public ParametersBuilder setConnectionLatency(int connectionLatency) {
            this.connectionLatency.setValue(connectionLatency);
            return this;
        }
        
        public ParametersBuilder randomizeConnectionLatency(int lower, int upper) {
            this.connectionLatency.randomize(lower, upper);
            return this;
        }

        public ParametersBuilder setTrustedGraphDensity(double trustedGraphDensity) {
            this.trustedGraphDensity.setValue(trustedGraphDensity);
            return this;
        }
        
        public ParametersBuilder randomizeTrustedGraphDensity(double lower, double upper) {
            this.trustedGraphDensity.randomize(lower, upper);
            return this;
        }

        public ParametersBuilder setAttackerGraphDensity(double attackerGraphDensity) {
            this.attackerGraphDensity.setValue(attackerGraphDensity);
            return this;
        }
        
        public ParametersBuilder randomizeAttackerGraphDensity(double lower, double upper) {
            this.attackerGraphDensity.randomize(lower, upper);
            return this;
        }
        
        public ParametersBuilder setRuns(int runs) {
            this.runs = runs;
            return this;
        }

        public ParametersBuilder setEpsilon(double epsilon) {
            this.epsilon = epsilon;
            return this;
        }

        public ParametersBuilder setLogLevel(Level logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public ParametersBuilder setTrustedPeerStrategy(PeerStrategyEnum tPeerStrat) {
            this.tps = tPeerStrat;
            return this;
        }

        public ParametersBuilder setAttackerPeerStrategy(PeerStrategyEnum aPeerStrat) {
            this.aps = aPeerStrat;
            return this;
        }

        public ParametersBuilder setConnectionStrategy(ConnectionStrategyEnum connStrat) {
            this.cs = connStrat;
            return this;
        }
        
        public ParametersBuilder setTrustedPeerStrategy(PeerStrategy tPeerStrat) {
            this.tps = null;
            this.tPeerStrat = tPeerStrat;
            return this;
        }

        public ParametersBuilder setAttackerPeerStrategy(PeerStrategy aPeerStrat) {
            this.aps = null;
            this.aPeerStrat = aPeerStrat;
            return this;
        }

        public ParametersBuilder setConnectionStrategy(ConnectionStrategy connStrat) {
            this.cs = null;
            this.connStrat = connStrat;
            return this;
        }

        public Parameters build() {
            if(tps != null)
                tPeerStrat = buildTrustedPeerStrat();
            if(aps != null)
                aPeerStrat = buildAttackerPeerStrat();
            if(cs != null)
                connStrat = buildConnStrat();
            return new Parameters(this);
        }
        
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
                    setTrustedPeerStrategy(getPeerStrat("T_PEER_STRAT", tps)). 
                    setAttackerPeerStrategy(getPeerStrat("A_PEER_STRAT", aps)).
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
        
        private PeerStrategy buildAttackerPeerStrat(){
            switch(aps){
                case CONSTANT:
                    return new ConstantPeerStrategy(attackerLatency);
                case EUCLIDEAN:
                    return new EuclideanPeerStrategy(attackerLatency);
                case RANDOM:
                    return new RndGraphPeerStrategy(attackerNodes, attackerGraphDensity, attackerLatency);
                default:
                    return null;
            }
        }
       
        private PeerStrategy buildTrustedPeerStrat(){
            switch(tps){
                case CONSTANT:
                    return new ConstantPeerStrategy(trustedLatency);
                case EUCLIDEAN:
                    return new EuclideanPeerStrategy(trustedLatency);
                case RANDOM:
                    return new RndGraphPeerStrategy(trustedNodes, trustedGraphDensity, trustedLatency);
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
