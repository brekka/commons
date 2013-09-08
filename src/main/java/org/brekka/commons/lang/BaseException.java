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
import java.util.Locale;


/**
 * Base Checked Exception that supports error codes and Java 5 formatted messages.
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public abstract class BaseException extends RuntimeException implements ErrorCoded {
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 3837575639170587967L;

    /**
     * Java 5 message arguments that can be optionally specified.
     */
    private Serializable[] messageArgs = null;

    /**
     * The error code for this exception.
     */
    private ErrorCode errorCode = null;

    /**
     * Construct a new Unchecked Exception. The code will deal robustly if no arguments are specifed but at a minimum the
     * <code>errorCode</code> and <code>message</code> parameters should be specified.
     *
     * @param errorCode the error code for the area where the exception was originally thrown.
     * @param message description of the problem that can optionally use Java 5 bind variables.
     * @param messageArgs objects that should be bound to placeholders in the message string.
     */
    public BaseException(final ErrorCode errorCode, final String message, final Object... messageArgs) {
        this(errorCode, null, message, messageArgs);
    }

    /**
     * Construct a new Unchecked Exception. The code will deal robustly if no arguments are specifed but at a minimum the
     * <code>message</code> parameter should be specified.
     *
     * @param message description of the problem that can optionally use Java 5 bind variables.
     * @param messageArgs objects that should be bound to placeholders in the message string.
     */
    public BaseException(final String message, final Object... messageArgs) {
        this(ErrorCode.NO_ERROR_CODE, null, message, messageArgs);
    }

    /**
     * Construct a new Unchecked Exception. The code will deal robustly if no arguments are specifed but at a minimum the
     * <code>message</code> parameter should be specified.
     *
     * @param cause the (optional) cause of this exception.
     * @param message description of the problem that can optionally use Java 5 bind variables.
     * @param messageArgs objects that should be bound to placeholders in the message string.
     */
    public BaseException(final Throwable cause, final String message,
            final Object... messageArgs) {
        this(ErrorCode.NO_ERROR_CODE, cause, message, messageArgs);
    }

    /**
     * Construct a new Unchecked Exception. The code will deal robustly if no arguments are specifed but at a minimum the
     * <code>errorCode</code> and <code>message</code> parameters should be specified.
     *
     * @param errorCode the error code for the area where the exception was originally thrown.
     * @param cause the (optional) cause of this exception.
     * @param message description of the problem that can optionally use Java 5 bind variables.
     * @param messageArgs objects that should be bound to placeholders in the message string.
     */
    public BaseException(final ErrorCode errorCode, final Throwable cause, final String message,
            final Object... messageArgs) {
        super(message);
        if (errorCode != null) {
            this.errorCode = errorCode;
        } else {
            this.errorCode = ErrorCode.NO_ERROR_CODE;
        }
        if (cause != null) {
            initCause(cause);
        }
        if (messageArgs != null) {
            this.messageArgs = Utils.checkAndCorrectMessageArgs(messageArgs, this, cause == null);
        }
    }

    /**
     * The optionally specified message arguments.
     * @return the message arguments (potentially null).
     */
    public Serializable[] getMessageArgs() {
        return this.messageArgs;
    }

    /**
     * Return the error code. If no error code was set then ErrorCode.NO_ERROR_CODE will be
     * returned. This method will never return null.
     * @return the errorCode.
     */
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
    
    /* (non-Javadoc)
     * @see org.brekka.commons.lang.ErrorCoded#getMessage(java.util.Locale, boolean)
     */
    public String getMessage(Locale locale, boolean prefixCode) {
        return Utils.formatLocalizedMessage(
                prefixCode ? getErrorCode() : ErrorCode.NO_ERROR_CODE, 
                super.getMessage(), this.messageArgs);
    }


    /**
     * Formats the message arguments (if any) with the message to form a complete string.
     *
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    @Override
    public String getMessage() {
        return Utils.formatLocalizedMessage(getErrorCode(), super.getMessage(), this.messageArgs);
    }
}
