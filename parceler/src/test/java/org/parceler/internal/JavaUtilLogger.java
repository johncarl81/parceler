/**
 * Copyright 2011-2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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