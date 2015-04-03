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

import java.io.IOException;
import java.io.Writer;

/**
 * Stream based {@link String} replacement as a {@link Writer}. It is recommended that the {@link Writer} be buffered as
 * this implementation calls the single-character {@link Writer#write(int)} method.
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public class StringReplacingWriter extends Writer {

    /**
     * The writer to output the resulting character stream to.
     */
    private final Writer writer;

    /**
     * The replacement for any strings found.
     */
    private final char[] replacement;

    /**
     * Locate the string being searched for from the characters being fed in one-by-one.
     */
    private final CharSequenceLocator locator;

    /**
     * @param writer
     *            the destination for the character data that has been filtered for replacement.
     * @param find
     *            the string to find
     * @param replacement
     *            the string to replace any found occurrences with.
     */
    public StringReplacingWriter(final Writer writer, final String find, final String replacement) {
        this.writer = writer;
        this.replacement = replacement.toCharArray(); // Won't change
        this.locator = new CharSequenceLocator(find.toCharArray());
    }


    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        for (int i = off; i < len; i++) {
            boolean replacing = locator.isReplacing();
            char displaced = locator.append(cbuf[i]);
            if (replacing) {
                writer.write(displaced);
            }
            if (locator.isFound()) {
                writer.write(replacement, 0, replacement.length);
                locator.clear();
            }
        }
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.write(locator.purge());
        writer.close();
    }
}
