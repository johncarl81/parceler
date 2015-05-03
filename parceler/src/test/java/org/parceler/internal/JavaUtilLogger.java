package org.parceler.internal;

import org.androidtransfuse.util.Logger;

/**
 * @author John Ericksen
 */
public class JavaUtilLogger implements Logger {

    private final boolean debug;
    private final java.util.logging.Logger logger;

    public JavaUtilLogger(Object targetInstance, boolean debug) {
        this.logger = java.util.logging.Logger.getLogger(targetInstance.getClass().getCanonicalName());
        this.debug = debug;
    }

    @Override
    public void info(String value) {
        logger.info(value);
    }

    @Override
    public void warning(String value) {
        logger.warning(value);
    }

    @Override
    public void error(String value) {
        logger.severe(value);
    }

    @Override
    public void error(String s, Throwable e) {
        logger.throwing(s, e.getMessage(), e);
    }

    @Override
    public void debug(String value) {
        if(debug) {
            info(value);
        }
    }
}