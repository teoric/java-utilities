package org.korpora.useful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a simple Loggable trait which adds a log class member
 */
public interface Loggable {

    /**
     * a SLF4J logger
     */
    // Please note that this method is not safe to use as few virtual machines may omit one or more stack frames from the stack trace under special circumstances.
    // <https://www.techiedelight.com/determine-class-name-java/>
    static final Logger log = LoggerFactory
            .getLogger(new Throwable().getStackTrace()[1].getClassName());

}
