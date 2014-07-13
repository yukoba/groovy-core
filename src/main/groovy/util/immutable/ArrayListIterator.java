/*
 * Copyright 2003-2014 the original author or authors.
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

package groovy.util.immutable;

import java.io.Serializable;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Array based {@link java.util.ListIterator} for immutable collections.
 *
 * @author Yu Kobayashi
 * @since 2.4.0
 */
class ArrayListIterator<E> implements ListIterator<E>, Serializable {
    private static final long serialVersionUID = 3478878196035068857L;

    private final E[] ary;
    private int idx;

    ArrayListIterator(E[] ary, int idx) {
        if (idx < 0 || idx > ary.length)
            throw new IndexOutOfBoundsException();

        this.ary = ary;
        this.idx = idx;
    }

    public boolean hasNext() {
        return idx < ary.length;
    }

    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return ary[idx++];
    }

    public boolean hasPrevious() {
        return idx > 0;
    }

    public E previous() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        return ary[--idx];
    }

    public int nextIndex() {
        return idx;
    }

    public int previousIndex() {
        return idx - 1;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void set(E e) {
        throw new UnsupportedOperationException();
    }

    public void add(E e) {
        throw new UnsupportedOperationException();
    }
}
