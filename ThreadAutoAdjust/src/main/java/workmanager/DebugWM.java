package workmanager;

import java.util.logging.Logger;
import static workmanager.DebugWM.DEBUG_LEVEL.*;

public final class DebugWM {
    public enum DEBUG_LEVEL { OFF, INFO, DEBUG, VERBOSE }
    static public DEBUG_LEVEL debug_level;
    static private Logger logger = Logger.getAnonymousLogger();
    static private DebugWM debugWM = new DebugWM();

    static public DebugWM getInstance() { return debugWM; }

    private DebugWM() {
        String level =System.getProperty("workmanager.debugLevel","").toUpperCase();
        if(level.equals("OFF")) debug_level = OFF;
        else if(level.equals("INFO")) debug_level = INFO;
        else if(level.equals("DEBUG")) debug_level = DEBUG;
        else if(level.equals("VERBOSE")) debug_level = VERBOSE;
        else debug_level = OFF;
        logger.info("DebugLevel = " + level);
    }

    static public workmanager.Logger getLogger(Class clazz) {
        final String prefix = "<" + clazz.getName() + "> ";
        return new workmanager.Logger() {
            public void info(String msg) {
                if(debug_level.ordinal() >= INFO.ordinal())
                    logger.info(prefix + msg);
            }
            public void debug(String msg) {
                if(debug_level.ordinal() >= DEBUG.ordinal())
                    logger.info(prefix + msg);
            }
            public void verbose(String msg) {
                if(debug_level.ordinal() >= VERBOSE.ordinal())
                    logger.info(prefix + msg);
            }
        };
    }
}
