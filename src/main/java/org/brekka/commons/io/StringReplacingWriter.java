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
 * TODO Description of StringReplacingWriter
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public class StringReplacingWriter extends Writer {
    private final Writer writer;

    private final char[] find;
    
    private final char[] replaceWith;
    
    private final char[] candidates;
    
    private int length = 0;

    /**
     * @param writer
     * @param find
     * @param replaceWith
     */
    public StringReplacingWriter(Writer writer, String find, String replaceWith) {
        this.writer = writer;
        this.find = find.toCharArray(); // Won't change
        this.replaceWith = replaceWith.toCharArray(); // Won't change
        this.candidates = new char[this.find.length];
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        int offset = 0;
        for (int i = 0; i < cbuf.length; i++) {
            char c = cbuf[i];
            candidates[length] = c;
            if (find[length] == c) {
                length++;
                if (length == find.length) {
                    // Found it
                    // Write what we have up to this point
                    writer.write(cbuf, offset, i - offset - find.length + 1);
                    // Write replacement
                    writer.write(replaceWith, 0, replaceWith.length);
                    offset = i + 1;
                    length = 0;
                } 
            } else if (length > 0) {
                // Write what we have up to this point
                writer.write(cbuf, offset, i - offset - length);
                // Dump the candidates
                writer.write(candidates, 0, length + 1);
                offset = i + 1;
                length = 0;
            } else {
                // Reset
                length = 0;
            }
        }
        writer.write(cbuf, offset, len - offset);
    }

    /* (non-Javadoc)
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() throws IOException {
        if (length > 0) {
            writer.write(candidates, 0, length);
        }
        writer.flush();
    }

    /* (non-Javadoc)
     * @see java.io.Writer#close()
     */
    @Override
    public void close() throws IOException {
        flush();
        writer.close();
    }
}
