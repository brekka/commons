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

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Formatter;
import java.util.Locale;

/**
 * Format a byte length (such as a file size) in a human readable format. Such as 3.4 MB, 12.4 GB.
 * 
 * Based on examples found here: http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
 * 
 * @author Andrew Taylor (andrew@brekka.org)
 */
public class ByteLengthFormat extends Format {
    
    /**
     * Formats the value
     */
    private static final String FORMAT_STR = "%.1f %sB";

    /**
     * Serial UID
     */
    private static final long serialVersionUID = -5474369454401084328L;

    private final Locale locale;
    
    private final Mode mode;
    
    public ByteLengthFormat(Locale locale, Mode mode) {
        this.locale = locale;
        this.mode = mode;
    }

    /* (non-Javadoc)
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof Number == false) {
            throw new IllegalArgumentException("Only supports instances of java.lang.Number");
        }
        Number number = (Number) obj;
        format(number, toAppendTo);
        return toAppendTo;
    }
    
    private void format(Number number, StringBuffer toAppendTo) {
        double value = number.doubleValue();
        if (value < mode.getDivisor()) {
            toAppendTo.append(number);
            toAppendTo.append(" B");
            return;
        }
        int exponent = (int) (Math.log(value) / Math.log(mode.getDivisor()));
        String shortPrefix = mode.getShortPrefix()[exponent - 1];
        new Formatter(toAppendTo, locale).format(FORMAT_STR, value / Math.pow(mode.getDivisor(), exponent), shortPrefix);
    }

    /* (non-Javadoc)
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     */
    @Override
    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException("Does not support parsing");
    }
    
    /**
     * The mode of operation.
     */
    public enum Mode {
        /**
         * SI units (kilobyte is 1000 bytes)
         */
        SI(1000, "k", "M", "G", "T", "P", "E"),
        /**
         * Binary unit (kibibyte is 1024 bytes)
         */
        BINARY(1024, "Ki", "Mi", "Gi", "Ti", "Pi", "Ei"),
        ;
        
        private final String[] shortPrefix;
        private final int divisor;
        private Mode(int divisor, String... labels) {
            this.shortPrefix = labels;
            this.divisor = divisor;
        }
        
        /**
         * @return the divisor
         */
        public int getDivisor() {
            return divisor;
        }
        
        /**
         * @return the shortPrefix
         */
        public String[] getShortPrefix() {
            return shortPrefix;
        }
        
    }

}
