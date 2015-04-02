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
import java.nio.CharBuffer;
import java.util.Arrays;

/**
 * TODO Description of StringReplacingWriter
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public class StringReplacingWriter extends Writer {
    private final Writer writer;

    private final char[] find;
    
    private final char[] replaceWith;
    
    private final CharBuffer buf;
    
    /**
     * @param writer
     * @param find
     * @param replaceWith
     */
    public StringReplacingWriter(Writer writer, String find, String replaceWith) {
        this.writer = writer;
        this.find = find.toCharArray(); // Won't change
        this.replaceWith = replaceWith.toCharArray(); // Won't change
        this.buf = CharBuffer.allocate(this.find.length);
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = off; i < len; i++) {
            buf.append(cbuf[i]);
            if (buf.remaining() == 0) {
                if (Arrays.equals(buf.array(), find)) {
                    writer.write(replaceWith, 0, replaceWith.length);
                    buf.rewind();
                } else {
                    writer.write(buf.get(0));
                    buf.position(1);
                    buf.compact();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() throws IOException {
        if (buf.position() > 0) {
            writer.write(buf.array(), 0, buf.position());
            buf.clear();
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
