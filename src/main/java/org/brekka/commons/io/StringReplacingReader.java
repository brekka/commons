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
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Objects;

/**
 * Stream based {@link String} replacement as a {@link Reader}.
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public class StringReplacingReader extends Reader {
    /**
     * The reader to obtain the character data from
     */
    private final Reader reader;

    /**
     * Locate the string being searched for from the characters being fed in one-by-one.
     */
    private final CharSequenceLocator locator;

    /**
     * Template {@link CharBuffer} of the string to replace any found occurrences with.
     */
    private final CharBuffer replaceWith;

    /**
     * Buffer of characters waiting to be written (in the event that they won't fit into the current
     * character buffer).
     */
    private CharBuffer pending;

    /**
     * Tracks when the end of stream is reached.
     */
    private boolean endOfStream = false;

    /**
     * @param reader
     *            the source of character data that will be filtered for replacement.
     * @param locator
     *            used to locate the string to replace
     * @param replacement
     *            the string to replace any found occurrences with.
     */
    public StringReplacingReader(final Reader reader, final CharSequenceLocator locator, final String replacement) {
        Objects.requireNonNull(reader, "A reader must be specified");
        Objects.requireNonNull(locator, "A locator must be specified");
        Objects.requireNonNull(replacement, "A replacement must be specified");
        this.reader = reader;
        this.locator = locator;
        this.replaceWith = CharBuffer.wrap(replacement.toCharArray());
    }

    /**
     * Read directly into a {@link CharBuffer}. This is the most allocation-efficient method as the only allocation will
     * be when the tail is appended.
     */
    @Override
    public int read(final CharBuffer target) throws IOException {
        int remaining = target.remaining();
        while (target.hasRemaining()) {
            if (pending != null) {
                while (target.hasRemaining() && pending.hasRemaining()) {
                    target.put(pending.get());
                }
                if (!pending.hasRemaining()) {
                    pending.rewind();
                    pending = null;
                }
            } else if (endOfStream) {
                if (remaining != target.remaining()){
                    // Some data has been added during this invocation, need to return that count.
                    break;
                }
                return -1;
            } else {
                int c = reader.read();
                if (c == -1) {
                    pending = CharBuffer.wrap(locator.purge());
                    endOfStream = true;
                    continue;
                }
                boolean replacing = locator.isReplacing();
                char displaced = locator.append((char) c);
                if (replacing) {
                    target.append(displaced);
                }
                if (locator.isFound()) {
                    // Will be consumed, need to return a duplicate.
                    pending = replaceWith;
                    locator.clear();
                }
            }
        }
        return remaining - target.remaining();
    }

    /**
     * Less efficient read method that allocates a new CharBuffer on each call.
     */
    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        return read(CharBuffer.wrap(cbuf, off, len));
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
