/*
 * Copyright 2011 the original author or authors.
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

package org.brekka.commons.lang;

import java.io.Serializable;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Locale;

/**
 * Interface that can be applied to a class to mark it as supporting error codes. This will normally be used by
 * exceptions.
 * 
 * @author Andrew Taylor (andrew@brekka.org)
 */
public interface ErrorCoded {
    /**
     * Retrieve the error code associated with this object (usually an exception).
     * 
     * @return the error code
     */
    ErrorCode getErrorCode();

    /**
     * Retrieve the message that goes with this code, in the specified locale (if available). The prefix code can be
     * prepended by setting <code>prefixCode</code> to true;
     * 
     * @param locale
     *            the locale to retrive the message in (if available).
     * @param prefixCode
     *            whether to prefix the code to the message in square brackets.
     * @return the formatter message.
     */
    String getMessage(Locale locale, boolean prefixCode);

    /**
     * Utilities for use with ErrorCoded exceptions
     */
    public static final class Utils {
        /**
         * An empty immutable <code>Object</code> array.
         */
        private static final Serializable[] EMPTY_SERIALIZABLE_ARRAY = new Serializable[0];

        /** Utility non-con */
        private Utils() {
        }

        /**
         * Checks the array of message arguments passed into the exception to fix misplaced exceptions. If the array
         * contains an exception as the last argument and no cause was explicitly defined, then remove the exception
         * from the array and initialise the cause. Note that this method will call the <code>initCause</code> method on
         * <code>setCauseOn</code> in the event it finds a misplaced exception.
         * 
         * @param messageArgs
         *            the array of message arguments to process
         * @param setCauseOn
         *            reference to the exception object we are processing arguments on behalf of (usually called with
         *            <code>this</code>).
         * @param noExplicitCause
         *            set to true if the caller has no explicit cause
         * @return the corrected list of message arguments
         */
        public static Serializable[] checkAndCorrectMessageArgs(final Object[] messageArgs, final Throwable setCauseOn,
                final boolean noExplicitCause) {
            Serializable[] retMessageArgs = EMPTY_SERIALIZABLE_ARRAY;
            // Set the message args.
            if (messageArgs != null && messageArgs.length > 0) {
                int length = messageArgs.length;
                if (messageArgs.length > 0 && noExplicitCause && messageArgs[length - 1] instanceof Throwable) {
                    // Backwards compatibility in case the exception is set as a message argument.
                    // This initCause will only be called if input cause is null
                    length = messageArgs.length - 1;
                    setCauseOn.initCause((Throwable) messageArgs[length]);
                }
                retMessageArgs = new Serializable[length];
                for (int i = 0; i < length; i++) {
                    Object arg = messageArgs[i];
                    if (arg instanceof Serializable) {
                        retMessageArgs[i] = (Serializable) arg;
                    } else if (arg != null) {
                        // The formatter will convert the argument to a String anyway so might as well reduce the object
                        // graph at this
                        // point.
                        try {
                            retMessageArgs[i] = arg.toString();
                        } catch (RuntimeException e) {
                            retMessageArgs[i] = "!" + e.getClass().getSimpleName() + " from toString():  "
                                    + e.getMessage() + "!";
                        }
                    } else {
                        // Arg is null, be explicit
                        retMessageArgs[i] = null;
                    }
                }
            }
            return retMessageArgs;
        }

        /**
         * Formats the message args into the message string if any are present. Should an illegal format error occur the
         * original message will be returned along with the list of arguments in braces.
         * 
         * @param originalMessage
         *            the message to format
         * @param messageArgs
         *            the array of objects to format into the message
         * @return the formatter string.
         * @see java.util.Formatter
         */
        public static String formatLocalizedMessage(ErrorCode errorCode, String originalMessage, Object[] messageArgs) {
            // An invalid string will be a programming error so should throw a runtime exception.
            String message = originalMessage;
            if (messageArgs != null && messageArgs.length > 0) {
                // Only enact the formatter if there are some arguments
                try {
                    message = String.format(originalMessage, messageArgs);
                } catch (IllegalFormatException e) {
                    StringBuilder formattedMessage = new StringBuilder();
                    formattedMessage.append("!");
                    formattedMessage.append(originalMessage);
                    formattedMessage.append("! args: ");
                    formattedMessage.append(Arrays.toString(messageArgs));
                    formattedMessage.append(" Formatter Error: '");
                    // We will loose the formatter stack trace but since this should only be used for
                    // generating the message of an exception just the message will do.
                    formattedMessage.append(e.getMessage());
                    formattedMessage.append("'");
                    message = formattedMessage.toString();
                }
            }
            if (errorCode != null && errorCode != ErrorCode.NO_ERROR_CODE) {
                message = "[" + errorCode.toString() + "] " + message;
            }
            return message;
        }
    }
}
