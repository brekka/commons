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

/**
 * TODO Description of StringReplacingReader
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public class StringReplacingReader extends Reader {
    private final Reader reader;

    private final char[] find;
    
    private final char[] replaceWith;
    
    private final char[] buffer;
    private final char[] candidates;
    
    private int index = -1;
    private int position = 0;
    private int available = 0;
    
    private char[] output;
    private int outputIndex;
    private int outputLength;

    public StringReplacingReader(Reader reader, String find, String replaceWith) {
        this.reader = reader;
        this.find = find.toCharArray(); // Won't change
        this.replaceWith = replaceWith.toCharArray(); // Won't change
        this.buffer = new char[128]; // Whatever length
        this.candidates = new char[this.find.length];
    }

    /* (non-Javadoc)
     * @see java.io.Reader#read(char[], int, int)
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (available == -1) {
            return available;
        }
        int count = 0;
        outerloop:
        for (int i = off; i < len; i++) {
            char c = 0;
            if (output != null) {
                c = output[outputIndex];
                outputIndex++;
                if (outputIndex == outputLength) {
                    output = null;
                }
            } else {
                while (available != -1) {
                    if (index == -1 || index == available) {
                        // Fetch data
                        available = reader.read(buffer, 0, buffer.length);
                        if (available == -1) {
                            // No more bytes, break out of outermost loop
                            break outerloop;
                        }
                        index = 0;
                    }
                    c = buffer[index++];
                    candidates[position] = c;
                    if (c == find[position]) {
                        position++;
                        if (position == find.length) {
                            // We have found the string, begin writing back the replacement
                            output = replaceWith;
                            outputIndex = 0;
                            c = output[outputIndex++];
                            outputLength = output.length;
                            position = 0;
                        } else {
                            // Keep fetching more values
                            continue;
                        }
                    } else {
                        if (position > 0) {
                            // Need to write back what is in the buffer
                            output = candidates;
                            outputIndex = 1;
                            outputLength = position + 1;
                        }
                        c = candidates[0];
                        // Reset position
                        position = 0;
                    }
                    // We have a value for c, break out of the inner loop
                    break;
                }
            }
            cbuf[i] = c;
            count++;
        }
        return count;
    }

    /* (non-Javadoc)
     * @see java.io.Reader#close()
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }
}
