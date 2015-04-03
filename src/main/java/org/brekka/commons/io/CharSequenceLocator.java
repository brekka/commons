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

/**
 * Helper class for identifying a character sequence within a stream of characters. This is implemented internally
 * as a cyclic buffer.
 *
 * @author Andrew Taylor (andrew@brekka.org)
 */
public class CharSequenceLocator {
    /**
     * The static character sequence to locate.
     */
    private final char[] toLocate;

    /**
     * The cyclic buffer
     */
    private final char[] buffer;

    /**
     * Pointer to the last character of the string
     */
    private int tailPointer;

    /**
     * The number of characters currently buffered.
     */
    private int length;

    /**
     * @param toLocate
     *            the character sequence to locate
     */
    public CharSequenceLocator(final char[] toLocate) {
        this.toLocate = toLocate;
        this.buffer = new char[toLocate.length];
    }

    /**
     * Determine whether the current contents of the buffer match the character sequence we are looking for.
     *
     * @return true if <code>toLocate</code> matches the current buffer.
     */
    public boolean isFound() {
        if (length != toLocate.length) {
            return false;
        }
        int cursor = tailPointer;
        for (int i = toLocate.length - 1; i >= 0; i--) {
            if (toLocate[i] != buffer[cursor]) {
                return false;
            }
            cursor--;
            if (cursor < 0) {
                cursor = buffer.length - 1;
            }
        }
        return true;
    }

    /**
     * Determine if the buffer is displacing characters from the head of the sequence.
     *
     * @return true if the buffer is full and any subsequent calls to {@link #append(char)} will displace a character
     *         off the head of the sequence.
     */
    public boolean isReplacing() {
        return length == buffer.length;
    }

    /**
     * Add a character to the buffer. If the buffer is full, the head of the sequence will be removed and this character
     * appended to the tail. The removed character will then be returned. The result should be ignored if a previous
     * call to {@link #isReplacing()} returns false.
     *
     * @param character
     *            the character to add.
     * @return the head of the sequence that was removed if the buffer was already full. Otherwise the result should be
     *         ignored.
     */
    public char append(final char character) {
        tailPointer++;
        if (tailPointer >= buffer.length) {
            tailPointer = 0;
        }
        if (length < toLocate.length) {
            length++;
        }
        char replaced = buffer[tailPointer];
        buffer[tailPointer] = character;
        return replaced;
    }

    /**
     * The number of characters currently in the buffer.
     *
     * @return the number of characters
     */
    public int getLength() {
        return length;
    }

    /**
     * Remove the current contents of the buffer as a new character array.
     *
     * @return the buffer contents, or empty if none.
     */
    public char[] purge() {
        char[] arr = new char[length];
        int cursor = tailPointer;
        for (int i = arr.length - 1; i >= 0; i--) {
            arr[i] = buffer[cursor];
            cursor--;
            if (cursor < 0) {
                cursor = buffer.length - 1;
            }
        }
        length = 0;
        return arr;
    }

    /**
     * Clear the buffer.
     */
    public void clear() {
        tailPointer = 0;
        length = 0;
    }
}
