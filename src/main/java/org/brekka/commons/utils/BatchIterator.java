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

package org.brekka.commons.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Wrap an existing iterator so that the returned elements are lists of the inner iterator's elements. Each returned
 * list will be at most <code>batchSize</code> in length. The final list returned may be smaller.
 *
 * @author Andrew Taylor (andrew@brekka.org)
 * @param <E>
 */
public class BatchIterator<E> implements Iterator<List<E>> {

    private final Iterator<E> source;

    private final int batchSize;

    public BatchIterator(final Iterator<E> source, final int batchSize) {
        this.source = source;
        this.batchSize = batchSize;
    }

    public BatchIterator(final Iterable<E> source, final int batchSize) {
        this.source = source.iterator();
        this.batchSize = batchSize;
    }

    @Override
    public boolean hasNext() {
        return this.source.hasNext();
    }

    @Override
    public List<E> next() {
        if (!this.source.hasNext()) {
            // Make sure we obey the iterator contract.
            throw new NoSuchElementException();
        }
        List<E> block = new ArrayList<>(this.batchSize);
        for (int i = 0; i < this.batchSize; i++) {
            block.add(this.source.next());
        }
        return block;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
