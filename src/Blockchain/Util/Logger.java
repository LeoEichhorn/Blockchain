package Blockchain.Util;

import java.util.logging.Level;

public class Logger {
    private static Level level = Level.INFO;
    
    /**
     * @param level Controls the amount of console output (INFO < FINE < FINER < FINEST)
     */
    public static void setLevel(Level level) {
        Logger.level = level;
    }
    
    public static void log(Level msgLevel, String msg){
        if(level.intValue() <= msgLevel.intValue())
            System.out.println(msg);
    }
}
