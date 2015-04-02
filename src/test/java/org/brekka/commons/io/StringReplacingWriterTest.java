/*
 * Copyright 2015 the original author or authors.
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;

/**
 * TODO Description of StringReplacingWriterTest
 *
 * @author Andrew Taylor
 */
public class StringReplacingWriterTest {

    @Test
    public void empty() throws Exception {
        test("");
    }
    @Test
    public void noEscapeShort() throws Exception {
        test("test");
    }
    @Test
    public void noEscapeLong() throws Exception {
        test("There is no need to escape this string");
    }
    @Test
    public void atStart() throws Exception {
        test("\\u0000 and some text");
    }
    @Test
    public void atEnd() throws Exception {
        test("and some text \\u0000");
    }
    @Test
    public void inMiddle() throws Exception {
        test("and some \\u0000 text");
    }
    @Test
    public void multipleWithStart() throws Exception {
        test("\\u0000 and \\u0000 some \\u0000 text");
    }
    @Test
    public void multipleWithEnd() throws Exception {
        test("and \\u0000 some \\u0000 text\\u0000");
    }
    @Test
    public void multiple() throws Exception {
        test("and \\u0000 some \\u0000 text\\u0000 test");
    }  
    @Test
    public void consecutive() throws Exception {
        test("\\u0000 and \\u0000\\u0000 some \\u0000\\u0000\\u0000\\u0000 text\\u0000 test \\u0000");
    }    
    @Test
    public void partialStart() throws Exception {
        test("\\u0001 test");
    }   
    @Test
    public void partialEnd() throws Exception {
        test("test \\u0001 ");
    }
    @Test
    public void partialMiddle() throws Exception {
        test("test \\u1000 other");
    }  
    @Test
    public void mixture() throws Exception {
        test("Bob \\u0000 test with a twist of something else \\u0000 for this test \\u89 \\10 Test");
    }
    
    @Test
    public void repeating() throws Exception {
        test("Bob \\\u0000 test \\\\u0000with a twist \u0000 of \\\\\u0000 something else \\u0000 for this test \\u89 \\10 Test");
    }
    
    protected void test(String text) throws IOException {
        StringWriter sw = new StringWriter();
        try (StringReplacingWriter srw = new StringReplacingWriter(sw, "\\u0000", "\\\\u0000");
                Writer w = new BufferedWriter(srw, 16)) {
            w.write(text);
        }
        assertEquals(text.replace("\\u0000", "\\\\u0000"), sw.toString());
    }
}
