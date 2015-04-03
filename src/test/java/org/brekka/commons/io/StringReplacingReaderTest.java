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

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;

/**
 * Tests for {@link StringReplacingReader}
 *
 * @author Andrew Taylor
 */
public class StringReplacingReaderTest {

    @Test
    public void empty() throws Exception {
        test("");
    }
    @Test
    public void noEscapeTiny() throws Exception {
        test("AB");
    }
    @Test
    public void noEscapeShort() throws Exception {
        test("alpha");
    }
    @Test
    public void noEscapeLong() throws Exception {
        test("There is no need to escape this string");
    }
    @Test
    public void atStart() throws Exception {
        test("\\\\u0000 alpha beta charlie");
    }
    @Test
    public void atEnd() throws Exception {
        test("alpha beta charlie \\\\u0000");
    }
    @Test
    public void inMiddle() throws Exception {
        test("alpha beta \\\\u0000 charlie");
    }
    @Test
    public void multipleWithStart() throws Exception {
        test("\\\\u0000 alpha \\\\u0000 beta \\\\u0000 charlie");
    }
    @Test
    public void multipleWithEnd() throws Exception {
        test("alpha \\\\u0000 beta \\\\u0000 charlie\\\\u0000");
    }
    @Test
    public void multiple() throws Exception {
        test("alpha \\\\u0000 beta \\\\u0000 charlie\\\\u0000 delta");
    }
    @Test
    public void consecutive() throws Exception {
        test("\\\\u0000 alpha \\\\u0000\\\\u0000 beta \\\\u0000\\\\u0000\\\\u0000\\\\u0000 charlie\\\\u0000 delta \\\\u0000");
    }
    @Test
    public void partialStart() throws Exception {
        test("\\\\u0001 alpha");
    }
    @Test
    public void partialEnd() throws Exception {
        test("alpha \\u0001 ");
    }
    @Test
    public void partialMiddle() throws Exception {
        test("alpha \\\\u1000 beta");
    }
    @Test
    public void mixture() throws Exception {
        test("alpha \\\\u0000 beta charlie \\\\u0000 delta foxtrot \\\\u89 \\10 golf");
    }
    @Test
    public void repeating() throws Exception {
        test("alpha \\\u0000 beta \\\\u0000charlie \\\\\\u0000 delta \u0000 foxtrot \\\\\u0000 golf \\\\\\\u0000hotel \\u0000 india juliett kilo \\u89 \\10 lima");
    }

    protected void test(final String val) throws Exception {
        StringReader sr = new StringReader(val);
        try (StringReplacingReader srr = new StringReplacingReader(sr, "\\\\u0000", "\\u0000")) {
            // Small buffer size to test bounds
            char[] buf = new char[16];
            int cnt = 0;
            StringBuilder sb = new StringBuilder();
            while ((cnt = srr.read(buf, 0, buf.length)) != -1) {
                sb.append(buf, 0, cnt);
            }
            String result = sb.toString();
            assertEquals(val.replace("\\\\u0000", "\\u0000"), result);
        }
    }
}
