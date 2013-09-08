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

import static java.lang.String.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * An error code identifies where an error occurred within a particular part of the system. It is composed of a area
 * identifier (two letters) and a three digit number that identifies a particular occurrence.
 * </p>
 * <p>
 * Error codes should be implemented using enumerations where one enum class is defined per area.
 * </p>
 * <p>
 * The recommended naming system for the enum values is to use the area ID followed by the error number. For example:
 * </p>
 * <pre>
 *   import org.brekka.commons.lang.ErrorCode;
 *   
 *   public enum UserErrorCode implements ErrorCode {
 *       UR101,
 *   
 *       UR102,
 *   
 *       ;
 *       private static final Area AREA = ErrorCode.Utils.createArea("UR");
 *       private int number = 0;
 *   
 *       public int getNumber() {
 *           return (this.number == 0 ? this.number = ErrorCode.Utils.extractErrorNumber(name(), getArea()) : this.number);
 *       }
 *   
 *       public Area getArea() { return AREA; }
 *   }
 * </pre>
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public interface ErrorCode {
    /**
     * Identifies what area of the system the error occurred in. This should be a sensible partition of the system.
     *
     * @return the area where the error occurred.
     */
    Area getArea();

    /**
     * The identifier for this error, unique within the area. A code that is three digits is recommended but see the
     * implementation for specific bounds.
     *
     * @return the integer number for the error.
     */
    int getNumber();

    /**
     * Must return the formatted area code that should take the form of the area id concatenated with the number.
     *
     * @return the string representation of this error code.
     */
    @Override
    String toString();

    /**
     * Constant for when an area is unknown.
     */
    Area NO_AREA = Utils.createArea("??");
    /**
     * Constant used when no code is specified
     */
    ErrorCode NO_ERROR_CODE = new ErrorCode() {
        public int getNumber() { return 0; }
        public Area getArea() { return NO_AREA; }
    };

    /**
     * Structure that identifies an area of code within a system. The ID must be unique within the system. Ideally this
     * interface should be implemented by an enum that defines all Areas of the system.
     */
    interface Area {
        /**
         * Each area within the system should have a unique Identifier. The recommended format is two characters.
         *
         * @return the area id.
         */
        String getID();
        
    }

    /**
     * Utilities for use with ErrorCodes
     */
    public static final class Utils {
        private static final Pattern CODE_PARSE = Pattern.compile("^(\\w+).*?(\\d+)$");
        
        /** Utility non-con */
        private Utils() {
            // Utility constructor, should not be instantiated
        }
        /**
         * Extract the error number from the specified enum name, validating that the
         * area prefix is correct and that the number is correct.
         * @param enumName the name of the enum error number
         * @param area the area whose ID should appear as the prefix to the name.
         * @return the error number from the enum name.
         */
        public static int extractErrorNumber(final String enumName, final Area area) {
            if (enumName == null) {
                throw new NullPointerException("An error code enum name is required");
            }
            if (area == null) {
                throw new NullPointerException(String.format(
                        "An error code must have an area. Name is '%s'", enumName));
            }
            if (area.getID() == null) {
                throw new NullPointerException(String.format(
                        "An error code must have an area with a valid id. Name is '%s'", enumName));
            }
            if (!enumName.startsWith(area.getID())) {
                throw new IllegalArgumentException(String.format(
                        "The error code name '%s' is not prefixed with the area ID '%s'",
                        enumName, area.getID()));
            }
            try {
                return Integer.parseInt(enumName.substring(area.getID().length()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format(
                        "The error code name '%s' does not have a valid number suffix",
                        enumName), e);
            }
        }
        /**
         * Extract the error number from the specified error code string.
         * 
         * @param errorCodeStr the string to extract the suffixed number from.
         * @return the error number extracted from the end of the string.
         */
        public static int extractErrorNumber(final String errorCodeStr) {
            int number;
            if (errorCodeStr == null) {
                throw new NullPointerException("An error code enum name is required");
            }
            Matcher m = CODE_PARSE.matcher(errorCodeStr);
            if (m.matches()) {
                number = Integer.parseInt(m.group(2));
            } else {
                throw new IllegalArgumentException(format(
                        "Failed to extract number from error code '%s'", errorCodeStr));
            }
            return number;
        }
        
        /**
         * Extract the area from the specified error code string.
         * 
         * @param errorCodeStr the string to extract the prefixed alphabetic area from.
         * @return the area extracted from the beginning of the string.
         */
        public static Area extractArea(String errorCodeStr) {
            Area area;
            if (errorCodeStr == null) {
                throw new NullPointerException("An error code enum name is required");
            }
            Matcher m = CODE_PARSE.matcher(errorCodeStr);
            if (m.matches()) {
                String areaStr = m.group(1);
                area = createArea(areaStr);
            } else {
                throw new IllegalArgumentException(format(
                        "Failed to extract area from error code '%s'", errorCodeStr));
            }
            return area;
        }
        
        /**
         * Create a new typed area code
         * @param areaCode the code to wrap in the {@link Area} type
         * @return the new area instance.
         */
        public static Area createArea(final String areaCode) {
            return new Area() {
                /**
                 * The id of this area
                 */
                public String getID() { return areaCode; }
                @Override
                public boolean equals(Object obj) {
                    if (obj == null) { return false; }
                    if (obj == this) { return true; }
                    if (! (obj instanceof Area)) {
                        return false;
                    }
                    Area area = (Area) obj;
                    return getID().equals(area.getID());
                }
                @Override
                public int hashCode() {
                    return getID().hashCode();
                }
                @Override
                public String toString() {
                    return getID();
                }
            };
        }
        
        /**
         * Attempt to parse and generate an error code based on the input value <code>codeIn</code>. If the code
         * cannot be parse, null is returned.
         * 
         * IMPORTANT! This code will return an object that implements {@link ErrorCode} but is guaranteed not to
         * be a reference to any statically defined constant. This code knows nothing about what static error code
         * instances are defined in your application. The instance will include an equals implementation that will
         * check the number/area values.
         * 
         * @param codeIn the value to parse
         * @return the parsed code or null if it cannot be parsed.
         */
        public static ErrorCode parseCode(String codeIn) {
            if (codeIn == null) {
                return null;
            }
            ErrorCode errorCode = null;
            String code = codeIn.toUpperCase();
            Matcher m = CODE_PARSE.matcher(code);
            if (m.matches()) {
                final Area area = createArea(m.group(1));
                final int number = Integer.parseInt(m.group(2));
                errorCode = new ErrorCode() {
                    public Area getArea() {
                        return area;
                    }
                    public int getNumber() {
                        return number;
                    }
                    
                    @Override
                    public int hashCode() {
                        final int prime = 6991;
                        int result = 1;
                        result = prime * result + ((area == null) ? 0 : area.hashCode());
                        result = prime * result + number;
                        return result;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        if (obj == null) { return false; }
                        if (obj == this) { return true; }
                        if (! (obj instanceof ErrorCode)) {
                            return false;
                        }
                        ErrorCode other = (ErrorCode) obj;
                        if (area == null) {
                            if (other.getArea() != null) {
                                return false;
                            }
                        } else if (!area.equals(other.getArea())) {
                            return false;
                        }
                        return (number == other.getNumber());
                    }
                    
                    @Override
                    public String toString() {
                        return getArea().getID() + getNumber();
                    }
                };
            }
            return errorCode;
        }
    }
}
