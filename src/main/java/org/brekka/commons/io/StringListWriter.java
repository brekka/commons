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

    private final List<String> list = new ArrayList<>();

    private StringBuffer line = new StringBuffer();

    private boolean closed = false;

    /* (non-Javadoc)
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("This writer is closed");
        }
        for (int i = off; i < off + len; i++) {
            char c = cbuf[i];
            if (c == '\r') {
                // Ignore
                continue;
            }
            if (c == '\n') {
                this.list.add(this.line.toString());
                this.line = new StringBuffer();
            } else {
                this.line.append(c);
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
    public void close() {
        if (this.closed) {
            return;
        }
        if (this.line.length() > 0) {
            this.list.add(this.line.toString());
        }
        this.closed = true;
    }

    /**
     * @return the list
     */
    public List<String> toList() {
        // Make sure we are closed
        close();
        if (this.list.isEmpty()) {
            return Collections.emptyList();
        }
        return this.list;
    }

}
