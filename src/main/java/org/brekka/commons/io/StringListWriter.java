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
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Collect the output written to this writer into a list of strings.
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public class StringListWriter extends Writer {
    
    private final List<String> list = new ArrayList<String>();
    
    private StringBuffer line = new StringBuffer();

    /* (non-Javadoc)
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = off; i < off + len; i++) {
            char c = cbuf[i];
            if (c == '\r') {
                // Ignore
                continue;
            }
            if (c == '\n') {
                list.add(line.toString());
                line = new StringBuffer();
            } else {
                line.append(c);
            }
        }
    }

    /* (non-Javadoc)
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() throws IOException {
       // Not required
    }

    /* (non-Javadoc)
     * @see java.io.Writer#close()
     */
    @Override
    public void close() throws IOException {
        if (line.length() > 0) {
            list.add(line.toString());
        }
    }
    
    /**
     * @return the list
     */
    public List<String> toList() {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        return list;
    }

}
