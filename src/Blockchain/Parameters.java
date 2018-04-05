package Blockchain;

import Blockchain.Util.Util;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

public class Parameters {
    //Number of trusted and attaking nodes (trusted > attacking)
    private final int trustedNodes;
    private final int attackerNodes;
    private final int nodes;

    //Difficulty to mine one block [0,1]
    private final double difficulty;

    //Number of additional blocks needed to validate one block
    private final int confirmations;

    //Number of double spend attemps
    private final int runs;

    //Minimum probability for attackers to catch up
    private final double epsilon;

    //Probability for attackers to reduce the gap by one block
    private final double probCatchUpByOne;

    //Maximum lead by trusted nodes for attackers to stay within epsilon
    private final int maxLead;
    //Maximum length of Blockchain before declaring attempt as failed
    private final int maxLength;
    
    //Controls amount of Console output (INFO < FINE < FINER < FINEST)
    private final Level logLevel;

    public Parameters(ParametersBuilder b) {
        this.trustedNodes = b.trustedNodes;
        this.attackerNodes = b.attackerNodes;
        this.difficulty = b.difficulty;
        this.confirmations = b.confirmations;
        this.runs = b.runs;
        this.epsilon = b.epsilon;
        this.logLevel = b.logLevel;
        
        nodes = trustedNodes + attackerNodes;
        probCatchUpByOne = ((double)attackerNodes) / trustedNodes;
        if (probCatchUpByOne >= 1) {
            maxLead = Integer.MAX_VALUE;
        } else {
            maxLead = (int) Math.ceil(Util.log(probCatchUpByOne, epsilon));
        }
        maxLength = (int) Math.ceil(1 / (epsilon * 100));
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
        return confirmations;
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

    public static class ParametersBuilder {

        private final Properties p;
        private int trustedNodes;
        private int attackerNodes;
        private double difficulty;
        private int confirmations;
        private int runs;
        private double epsilon;
        private Level logLevel;
        
        public ParametersBuilder() {
            this.p = new Properties();
            this.trustedNodes  = 10;
            this.attackerNodes = 3;
            this.difficulty    = 0.000001;
            this.confirmations = 3;
            this.runs          = 100;
            this.epsilon       = 0.000001;
            this.logLevel      = Level.FINE;
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
            this.confirmations = confirmations;
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
        
        public Parameters build() {
            return new Parameters(this);
        }
        
        public ParametersBuilder loadFromProperty(String fileName) throws IOException {
            p.load(new FileInputStream(fileName));  
            return setTrustedNodes(getInteger("NUM_TRUSTED", trustedNodes)).
                    setAttackerNodes(getInteger("NUM_ATTACKER", attackerNodes)).
                    setDifficulty(getDouble("DIFFICULTY", difficulty)).
                    setConfirmations(getInteger("CONFIRMATIONS", confirmations)).
                    setRuns(getInteger("RUNS", runs)).
                    setEpsilon(getDouble("EPSILON", epsilon)).
                    setLogLevel(getLevel("LOGGING", logLevel));
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
    }
}
