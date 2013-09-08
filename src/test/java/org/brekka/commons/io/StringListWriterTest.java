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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * TODO Description of StringListWriterTest
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public class StringListWriterTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link org.brekka.commons.io.StringListWriter#write(char[], int, int)}.
     * @throws IOException 
     */
    @Test
    public void testEmpty() throws IOException {
        StringListWriter writer = new StringListWriter();
        writer.write("");
        writer.close();
        List<String> list = writer.toList();
        assertEquals(Collections.emptyList(), list);
    }
    
    /**
     * Test method for {@link org.brekka.commons.io.StringListWriter#write(char[], int, int)}.
     * @throws IOException 
     */
    @Test
    public void testOneLine() throws IOException {
        StringListWriter writer = new StringListWriter();
        writer.write("This is a test");
        writer.close();
        List<String> list = writer.toList();
        assertEquals(Arrays.asList("This is a test"), list);
    }
    
    /**
     * Test method for {@link org.brekka.commons.io.StringListWriter#write(char[], int, int)}.
     * @throws IOException 
     */
    @Test
    public void testTwoLines() throws IOException {
        StringListWriter writer = new StringListWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println("This is a test");
        out.println("This is also a test");
        out.close();
        List<String> list = writer.toList();
        assertEquals(Arrays.asList("This is a test", "This is also a test"), list);
    }
}
