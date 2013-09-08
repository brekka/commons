/*
 * Copyright 2012 the original author or authors.
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

package org.brekka.commons.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

/**
 * TODO Description of IterableStringReaderTest
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public class IterableStringReaderTest {


    /**
     * Test method for {@link org.brekka.commons.io.IterableStringReader#read(char[], int, int)}.
     * @throws IOException 
     */
    @Test
    public void testEmpty() throws IOException {
        IterableStringReader reader = new IterableStringReader(Collections.<String>emptyList());
        BufferedReader br = new BufferedReader(reader);
        assertNull(br.readLine());
    }

    @Test
    public void testOneLine() throws IOException {
        IterableStringReader reader = new IterableStringReader(Arrays.asList("This is a test"));
        BufferedReader br = new BufferedReader(reader);
        assertEquals("This is a test", br.readLine());
        assertNull(br.readLine());
    }
    
    @Test
    public void testTwoLines() throws IOException {
        IterableStringReader reader = new IterableStringReader(Arrays.asList("This is a test", "This is also a test"));
        BufferedReader br = new BufferedReader(reader);
        assertEquals("This is a test", br.readLine());
        assertEquals("This is also a test", br.readLine());
        assertNull(br.readLine());
    }
}
