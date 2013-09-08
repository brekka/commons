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

import java.io.IOException;
import java.io.Reader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Iterator;

/**
 * Read strings from a list, applying newline at the end of each string.
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public class IterableStringReader extends Reader {
    
    /**
     * The iterator of strings
     */
    private Iterator<String> iter;
    
    /**
     * Current string iterator
     */
    private CharacterIterator current;
    
    /**
     * 
     */
    public IterableStringReader(Iterable<String> source) {
        this.iter = source.iterator();
    }
    
    /* (non-Javadoc)
     * @see java.io.Reader#read(char[], int, int)
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (!iter.hasNext()) {
            // No more strings
            return -1;
        }
        if (current == null) {
            current = new StringCharacterIterator(iter.next());
        }
        int count = 0;
        boolean done = false;
        for (int i = off; i < off + len && !done; i++) {
            char c = current.current();
            if (c == CharacterIterator.DONE) {
                // We are not handling windows
                c = '\n';
                if (iter.hasNext()) {
                    // Move to the next line
                    current = new StringCharacterIterator(iter.next());
                } else {
                    done = true;
                }
            } else {
                current.next();
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
        // Not required
    }
}