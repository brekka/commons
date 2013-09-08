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

import static org.junit.Assert.*;

import java.util.Locale;

import org.brekka.commons.lang.ByteLengthFormat.Mode;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Taylor (andrew@brekka.org)
 *
 */
public class ByteLengthFormatTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link org.brekka.commons.lang.ByteLengthFormat#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)}.
     */
    @Test
    public void testFormatSI() {
        ByteLengthFormat format = new ByteLengthFormat(Locale.ENGLISH, Mode.SI);
        assertEquals("345 B", format.format(345L));
        assertEquals("34.5 kB", format.format(34512L));
        assertEquals("3.5 MB", format.format(3451212L));
        assertEquals("345.1 MB", format.format(345127812L));
        assertEquals("12.3 GB", format.format(12345127812L));
        assertEquals("9.2 EB", format.format(Long.MAX_VALUE));
    }
    
    @Test
    public void testFormatBinary() {
        ByteLengthFormat format = new ByteLengthFormat(Locale.ENGLISH, Mode.BINARY);
        assertEquals("345 B", format.format(345L));
        assertEquals("33.7 KiB", format.format(34512L));
        assertEquals("3.3 MiB", format.format(3451212L));
        assertEquals("329.1 MiB", format.format(345127812L));
        assertEquals("11.5 GiB", format.format(12345127812L));
        assertEquals("8.0 EiB", format.format(Long.MAX_VALUE));
    }

}
