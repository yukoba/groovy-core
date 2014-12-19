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

package groovy.util;

import groovy.lang.Tuple2;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IndexPostZippedIterator<E> implements Iterator<Tuple2<E, Integer>> {
    private final Iterator<E> delegate;
    private int index;

    public IndexPostZippedIterator(Iterator<E> delegate) {
        this(delegate, 0);
    }

    public IndexPostZippedIterator(Iterator<E> delegate, int offset) {
        this.delegate = delegate;
        this.index = offset;
    }

    public boolean hasNext() {
        return delegate.hasNext();
    }

    public Tuple2<E, Integer> next() {
        if (!hasNext()) throw new NoSuchElementException();
        return new Tuple2<E, Integer>(delegate.next(), index++);
    }

    public void remove() {
        delegate.remove();
    }
}
