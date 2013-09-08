/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brekka.commons.utils;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Retry when predefined exception types are encountered, up to fixed number of attempts. Default settings result in re-attempts in the
 * following intervals:
 * 
 * Attempt 1:  600 ms
 * Attempt 2: 1200 ms
 * Attempt 3: 2400 ms
 * Attempt 4: 4800 ms
 * Attempt 5: 9600 ms
 * 
 * When the maximum number of re-attempts has been reached, the last exception encountered will be thrown.
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public abstract class AbstractRetryAspect {

    /**
     * Logger
     */
    private static final Log log = LogFactory.getLog(AbstractRetryAspect.class);
    
    /**
     * The default number of reattempts.
     */
    private static final int DEFAULT_MAX_REATTEMPTS = 5;

    /**
     * Default back off factor - 2 will be raised to the power of the attempt number.
     */
    private static final float DEFAULT_BACK_OFF_FACTOR = 2f;

    /**
     * Default base interval - 300 milliseconds.
     */
    private static final int DEFAULT_BASE_INTERVAL_MILLIS = 300;

    
    
    
    /**
     * Maximum re-attempts.
     */
    private int maxReattempts = DEFAULT_MAX_REATTEMPTS;
    
    /**
     * Exponential rolloff
     */
    private float backOffFactor = DEFAULT_BACK_OFF_FACTOR;
    
    /**
     * The number of milliseconds to multiply by the backoff factor raised to the power of the attempt number.
     */
    private int baseIntervalMillis = DEFAULT_BASE_INTERVAL_MILLIS;
    
    
    /**
     * Exceptions that can be retried.
     */
    private List<Class<? extends Throwable>> retriableExceptions;
    
    /**
     * Attempt the operation, retrying if any known exception types are raised.
     * @param pjp
     * @return
     * @throws Throwable
     */
    protected Object attemptWithRetry(ProceedingJoinPoint pjp) throws Throwable {
        int attempts = 0;
        while (true) {
            try {
                Object retVal = pjp.proceed();
                return retVal;
            } catch (Throwable e) {
                handleThrowable(e, attempts);
            }
        }
    }
    
    /**
     * @param e
     */
    protected void handleThrowable(Throwable e, int attempts) throws Throwable {
        if (isRetriable(e)) {
            // Retry!
            attempts++;
            if (attempts > maxReattempts) {
                throw e;
            }
            long waitTime = Math.round(baseIntervalMillis * Math.pow(attempts, backOffFactor));
            if (log.isDebugEnabled()) {
                log.debug(String.format("Encountered %s, attempt %d. Will wait for %d ms",
                        e.getClass().getName(), attempts, waitTime));
            }
            Thread.sleep(waitTime);
        } else {
            throw e;
        }
    }

    /**
     * @param e
     * @return
     */
    protected boolean isRetriable(Throwable e) {
        for (Class<? extends Throwable> type : retriableExceptions) {
            if (type.isAssignableFrom(e.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param baseIntervalMillis the baseIntervalMillis to set
     */
    public void setBaseIntervalMillis(int baseIntervalMillis) {
        this.baseIntervalMillis = baseIntervalMillis;
    }
    
    /**
     * @param backOffFactor the backOffFactor to set
     */
    public void setBackOffFactor(float backOffFactor) {
        this.backOffFactor = backOffFactor;
    }
    
    /**
     * @param maxReattempts the maxReattempts to set
     */
    public void setMaxReattempts(int maxReattempts) {
        this.maxReattempts = maxReattempts;
    }
    
    /**
     * @param retriableExceptions the retriableExceptions to set
     */
    public void setRetriableExceptions(List<Class<? extends Throwable>> retriableExceptions) {
        this.retriableExceptions = retriableExceptions;
    }
}
